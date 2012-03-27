package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jt.core.impl.RPTRecordSetRecordDefine.RPTRecord;
import org.eclipse.jt.core.type.DataType;


final class RPTRecordSetRecordReader extends ResultSetDynObjReader {

	final ArrayList<Object> paramCache = new ArrayList<Object>();
	final ArrayList<ConditionalExpr> condisCache = new ArrayList<ConditionalExpr>(
			2);

	RPTRecordSetRecordReader(RPTRecordSetImpl recordSet) {
		super(null);
		this.dummyArg = new ArgumentRefExpr(recordSet.recordStruct.fields
				.get(0));
		int keyL = recordSet.keys.size();
		this.keyValuesCache = new Object[keyL];
		this.keyStoreFields = new StructFieldDefineImpl[keyL];
		this.hashKeyMatchFields = new StructFieldDefineImpl[keyL];
	}

	private final ArgumentRefExpr dummyArg;

	final ConditionalExpr newInCondi(TableFieldRefImpl fre, int inCount) {
		ValueExpr[] ins = new ValueExpr[inCount + 1];
		ins[0] = fre;
		for (int k = 1; k <= inCount; k++) {
			ins[k] = this.dummyArg;
		}
		return new PredicateExpr(false, PredicateImpl.IN, ins);
	}

	final void readField(StructFieldDefineImpl field) {
		this.targetField = field;
		field.type.detect(this, null);
		this.columnIndex++;
	}

	final Object readFieldReturn(StructFieldDefineImpl field) {
		this.targetField = field;
		Object o = field.type.detect(this, Boolean.TRUE);
		this.columnIndex++;
		return o;
	}

	final Object readOnlyReturn(DataType type) {
		Object o = type.detect(this, read_only_return);
		this.columnIndex++;
		return o;
	}

	final void readRecidFieldAndPutToMap(StructFieldDefineImpl field) {
		this.targetField = field;
		Object o = field.type.detect(this, Boolean.TRUE);
		HashMap<Object, DynObj> recidMap = this.recidMap;
		if (recidMap == null) {
			this.recidMap = recidMap = new HashMap<Object, DynObj>();
		}
		recidMap.put(o, this.obj);
		this.columnIndex++;
	}

	final boolean readRecidAndLocateWithMap() {
		Object o = GUIDType.TYPE.detect(this, read_only_return);
		this.columnIndex++;
		if (o == null) {
			return false;
		}
		return (this.obj = this.recidMap.get(o)) != null;
	}

	private HashMap<Object, DynObj> recidMap;

	final HashMap<Object, DynObj> getRecidMap() {
		if (this.recidMap == null) {
			this.recidMap = new HashMap<Object, DynObj>();
		}
		return this.recidMap;
	}

	private final ArrayList<StructFieldDefineImpl> fieldsCatch = new ArrayList<StructFieldDefineImpl>();

	final void addDataFieldToCache(StructFieldDefineImpl field) {
		this.fieldsCatch.add(field);
	}

	private int keyFieldSize;

	final void addKeyFieldToCache(StructFieldDefineImpl storeField,
			StructFieldDefineImpl matchField) {
		int keyFieldSize = this.keyFieldSize;
		if (this.hashKeyMatchFields[keyFieldSize] != matchField) {
			this.hashKeyMatchFields[keyFieldSize] = matchField;
			this.needRebuildHash = true;
		}
		this.keyStoreFields[keyFieldSize] = storeField;
		this.keyFieldSize = keyFieldSize + 1;
	}

	final void resetQuery() {
		this.fieldsCatch.clear();
		this.keyFieldSize = 0;
	}

	final void resetLoadTable() {
		if (this.recidMap != null) {
			this.recidMap.clear();
		}
	}

	private int recordHashSize;
	private RPTRecord[] recordHashTable = new RPTRecord[4];
	private final Object[] keyValuesCache;
	private RPTRecordSetTableInfo recordHashTableInfo;
	private final StructFieldDefineImpl[] hashKeyMatchFields;
	private boolean needRebuildHash;
	private final StructFieldDefineImpl[] keyStoreFields;

	private final void locateOrNewRecord(RPTRecordSetTableInfo tableInfo) {
		final RPTRecordSetKeyImpl[] keys = tableInfo.keys;
		final int keyCount = keys.length;
		final StructFieldDefineImpl[] hashKeyMatchFields = this.hashKeyMatchFields;
		RPTRecord[] recordHashTable = this.recordHashTable;
		int oldHashLen = recordHashTable.length;
		// 1. �������Ϣ�����仯�����ж��Ƿ���Ҫ�ؽ�������
		if (this.recordHashTableInfo != tableInfo) {
			boolean needRebuildHash = this.needRebuildHash;
			for (int i = keyCount, c = hashKeyMatchFields.length; i < c; i++) {
				if (hashKeyMatchFields[i] != null) {
					hashKeyMatchFields[i] = null;
					needRebuildHash = true;
				}
			}
			// �ؽ�����
			if (needRebuildHash) {
				RPTRecord[] newHashTable = new RPTRecord[oldHashLen];
				final int hashHigh = oldHashLen - 1;
				for (int i = 0; i < oldHashLen; i++) {
					for (RPTRecord record = recordHashTable[i]; record != null;) {
						int hash = 0;
						for (int ki = 0; ki < keyCount; ki++) {
							StructFieldDefineImpl keyField = hashKeyMatchFields[ki];
							if (keyField != null) {
								Object keyValue = keyField
										.getFieldValueAsObjectNoCheck(record);
								if (keyValue == null) {
									hash *= 31;
								} else {
									hash = hash * 31 + keyValue.hashCode();
								}
							}
						}
						RPTRecord next = record.nextSameHash;
						int newIndex = hash & hashHigh;
						record.hash = hash;
						record.nextSameHash = newHashTable[newIndex];
						newHashTable[newIndex] = record;
						record = next;
					}
				}
				this.recordHashTable = recordHashTable = newHashTable;
				this.needRebuildHash = false;
			}
			this.recordHashTableInfo = tableInfo;
		}
		// 2. װ�ؼ�ֵ��������Hashֵ
		int hash = 0;
		final Object[] keyValuesCache = this.keyValuesCache;
		for (int i = 0; i < keyCount; i++) {
			Object keyValue = this.readOnlyReturn(keys[i].field.type);
			keyValuesCache[i] = keyValue;
			StructFieldDefineImpl matchKeyField = hashKeyMatchFields[i];// ƥ���ֶ�
			if (matchKeyField != null) {
				if (keyValue == null) {
					hash *= 31;
				} else {
					hash = hash * 31 + keyValue.hashCode();
				}
			}
		}
		// 3. ���ҹ�����û���ҵ����½���¼�����������
		boolean newRecord;
		RPTRecord record;
		hashLocate: {
			record = recordHashTable[hash & (oldHashLen - 1)];
			while (record != null) {
				if (record.hash == hash) {
					recordMatch: {
						for (int i = 0; i < keyCount; i++) {
							StructFieldDefineImpl matchKeyField = hashKeyMatchFields[i];// ƥ���ֶ�
							if (matchKeyField != null) {
								Object matchValue = matchKeyField
										.getFieldValueAsObjectNoCheck(record);
								Object keyValue = keyValuesCache[i];
								if (!(matchValue == keyValue || matchValue != null
										&& keyValue != null
										&& keyValue.equals(matchValue))) {
									break recordMatch;
								}
							}
						}
						// �ҵ���Ӧ��¼
						newRecord = false;
						this.obj = record;
						break hashLocate;
					}
				}
				record = record.nextSameHash;
			}
			// û���ҵ�����Ҫ�����¼�¼
			this.obj = record = tableInfo.restriction.owner
					.newRecord(DynObj.r_db);
			newRecord = true;
			record.hash = hash;
			// ���¼�¼���뵽��������
			if (++this.recordHashSize > oldHashLen * 0.75) {
				int newLen = oldHashLen * 2;
				RPTRecord[] newTable = new RPTRecord[newLen];
				for (int j = 0; j < oldHashLen; j++) {
					for (RPTRecord e = recordHashTable[j], next; e != null; e = next) {
						int index = e.hash & (newLen - 1);
						next = e.nextSameHash;
						e.nextSameHash = newTable[index];
						newTable[index] = e;
					}
				}
				this.recordHashTable = recordHashTable = newTable;
				oldHashLen = newLen;
			}
			int index = hash & (oldHashLen - 1);
			record.nextSameHash = recordHashTable[index];
			recordHashTable[index] = record;
		}
		final StructFieldDefineImpl[] keyStoreFields = this.keyStoreFields;
		// 4. ����ʱ��ֵд���¼
		for (int i = 0; i < keyCount; i++) {
			StructFieldDefineImpl keyField = keyStoreFields[i];// �洢�ֶ�
			if (keyField == null) {
				if (newRecord) {
					keyField = hashKeyMatchFields[i];// ƥ���ֶ�
					if (keyField == null) {
						continue;
					}
				} else {
					continue;
				}
			}
			keyField.setFieldValueAsObjectNoCheck(record, keyValuesCache[i]);
		}
	}

	final int readTablePart(RPTRecordSetTableInfo tableInfo,
			boolean isFirstPart, boolean isLastPart) throws SQLException {
		final ArrayList<StructFieldDefineImpl> fieldsCatch = this.fieldsCatch;
		final int fieldCount = fieldsCatch.size();
		int count = 0;
		while (this.resultSet.next()) {
			this.columnIndex = 1;
			if (isFirstPart) {
				// װ�ؼ�ֵ����λ���½���¼�������ֵ
				this.locateOrNewRecord(tableInfo);
				// RECVER
				this.readField(tableInfo.recverSf);
				// RECID
				if (isLastPart) {
					this.readField(tableInfo.recidSf);
				} else {
					// �������һ�������ʱ��Ϊ����������¼RECID
					this.readRecidFieldAndPutToMap(tableInfo.recidSf);
				}
			} else {
				if (!this.readRecidAndLocateWithMap()) {
					// δ�ҵ���Ӧ�ļ�¼
					continue;
				}
			}
			// װ�������ֶ�
			for (int i = 0; i < fieldCount; i++) {
				this.readField(fieldsCatch.get(i));
			}
			count++;
		}
		return count;
	}
}