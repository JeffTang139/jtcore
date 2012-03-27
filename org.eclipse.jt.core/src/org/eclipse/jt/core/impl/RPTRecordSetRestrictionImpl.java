package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.da.ext.RPTRecordSetField;
import org.eclipse.jt.core.da.ext.RPTRecordSetKey;
import org.eclipse.jt.core.da.ext.RPTRecordSetRestriction;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.RPTRecordSetRecordDefine.RPTRecord;


/**
 * ��¼��Լ��
 * 
 * @author Jeff Tang
 * 
 */
final class RPTRecordSetRestrictionImpl implements RPTRecordSetRestriction {

	// ��¼��Լ���ļ�Լ��,��ָ����ʹ��RecordSet��Ĭ��Լ��
	// ��СͬRecordSet��keys�Ĵ�С,�Ҷ�Ӧ
	private RPTRecordSetKeyRestrictionImpl[] keyRestrictions;

	// ��СͬRecordSet��keys�Ĵ�С,�Ҷ�Ӧ,��rstr.newField������,�Ų���null.
	private boolean[] validKeys;

	final StructFieldDefineImpl tryGetKeyRestrictionField(int index) {
		RPTRecordSetKeyRestrictionImpl[] keyRestrictions = this.keyRestrictions;
		if (index < keyRestrictions.length) {
			RPTRecordSetKeyRestrictionImpl kr = keyRestrictions[index];
			if (kr != null) {
				return kr.field;
			}
		}
		return null;
	}

	public final boolean isKeySupported(RPTRecordSetKey key) {
		RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) key;
		if (k.owner != this.owner && k.generation != this.owner.generation) {
			throw new IllegalArgumentException("key");
		}
		return this.isKeySupported(k.index);
	}

	final void ensurePrepared() {
		if (this.keyRestrictions != null) {
			for (RPTRecordSetKeyRestrictionImpl kr : this.keyRestrictions) {
				if (kr != null) {
					kr.ensurePrepared();
				}
			}
		}
	}

	/**
	 * 
	 * <p>
	 * updateSize(-1)��ʾ�ο�RecordSet��keys����,����validKeys����Լ����֧����Ϣ
	 * 
	 * @param oldSize
	 * @return
	 */
	final int updateSize(int oldSize) {
		int keyCount = this.owner.keys.size();
		if (oldSize < keyCount) {
			boolean[] validKeys = this.validKeys;
			if (oldSize < 0) {
				oldSize = validKeys != null ? validKeys.length : 0;
			}
			RPTRecordSetKeyRestrictionImpl[] newKrs = new RPTRecordSetKeyRestrictionImpl[keyCount];
			boolean[] newValidKeys = new boolean[keyCount];
			if (oldSize > 0) {
				if (oldSize < keyCount) {
					oldSize = keyCount;
				}
				System.arraycopy(this.keyRestrictions, 0, newKrs, 0, oldSize);
				System.arraycopy(validKeys, 0, newValidKeys, 0, oldSize);
			}
			this.keyRestrictions = newKrs;
			this.validKeys = newValidKeys;
		}
		return keyCount;
	}

	public final boolean isKeySupported(int index) {
		boolean[] validKeys = this.validKeys;
		int size = validKeys != null ? validKeys.length : 0;
		if (index >= size) {
			size = this.updateSize(size);
			validKeys = this.validKeys;
		}
		if (index < 0 || size <= index) {
			throw new IndexOutOfBoundsException("key count:" + size + ",index:"
					+ index);
		}
		return validKeys[index];
	}

	final RPTRecordSetImpl owner;

	final int generation;

	final ArrayList<RPTRecordSetTableInfo> tables = new ArrayList<RPTRecordSetTableInfo>(
			1);

	final RPTRecordSetFieldImpl newField0(TableFieldDefineImpl tableField) {
		if (this.generation != this.owner.generation) {
			throw new IllegalStateException("�����Ѿ�ʧЧ");
		}
		RPTRecordSetTableInfo tableInfo;
		ensureTableInfo: {
			TableDefineImpl table = tableField.owner;
			for (int i = 0, c = this.tables.size(); i < c; i++) {
				RPTRecordSetTableInfo ti = this.tables.get(i);
				if (ti.table == table) {
					tableInfo = ti;
					break ensureTableInfo;
				}
			}
			this.tables.add(tableInfo = new RPTRecordSetTableInfo(this, table));
			this.updateSize(-1);
			for (RPTRecordSetKeyImpl key : tableInfo.keys) {
				this.validKeys[key.index] = true;
			}
		}
		return tableInfo.newField(tableField);
	}

	public final void clearMatchValues() {
		if (this.keyRestrictions != null) {
			for (RPTRecordSetKeyRestrictionImpl kr : this.keyRestrictions) {
				if (kr != null) {
					kr.clearMatchValues();
				}
			}
		}
	}

	final int index;

	RPTRecordSetRestrictionImpl(RPTRecordSetImpl owner) {
		this.owner = owner;
		this.generation = owner.generation;
		this.index = owner.records.size();
	}

	/**
	 * ��ȡʹ�õļ�Լ��
	 * 
	 * @param index
	 * @param reader
	 * @return
	 */
	final RPTRecordSetKeyRestrictionImpl useKeyRestriction(int index,
			RPTRecordSetRecordReader reader) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		// ���Լ���ļ�Լ��Ϊ��,��ʹ��RecordSet��Ĭ�ϼ�Լ��
		if (kr == null) {
			kr = this.owner.keys.get(index).defaultKeyRestriction;
			reader.addKeyFieldToCache(null, kr.field);
			return kr;
		} else {
			return kr.useKeyRestriction(reader);
		}
	}

	final RPTRecordSetKeyRestrictionImpl useKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			return this.owner.keys.get(index).defaultKeyRestriction;
		} else {
			return kr;
		}
	}

	final RPTRecordSetKeyRestrictionImpl getKeyRestrictionNoCheck(int index) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			return kr = this.owner.keys.get(index).defaultKeyRestriction;
		} else {
			return kr;
		}
	}

	/**
	 * �����¼��keyֵΪ��,������ֵ
	 * 
	 * @param record
	 *            �������
	 * @param index
	 *            �������
	 * @return
	 */
	final boolean tryUpdateKeyFieldValueIfNull(RPTRecord record, int index) {
		if (!this.isKeySupported(index)) {
			return false;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		// ����ļ�Լ��Ϊ��
		if (kr == null) {
			// ��ʹ��RecordSet��Ĭ��Լ��
			kr = this.owner.keys.get(index).defaultKeyRestriction;
		}
		return kr.tryUpdateKeyFieldValueIfNull(record);
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			throw new IllegalArgumentException("��Լ����֧�ּ�["
					+ this.owner.getKey(index).getName() + "]");
		}
		RPTRecordSetKeyRestrictionImpl[] krs = this.keyRestrictions;
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			// Ĭ�������ʹ��rs��Ӧkr��defaultԼ��
			krs[index] = kr = new RPTRecordSetKeyRestrictionImpl(
					this.owner.keys.get(index));
		}
		return kr;
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(
			RPTRecordSetKey key) {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) key;
		RPTRecordSetKeyRestrictionImpl r = this.getKeyRestriction(k.index);
		if (r.key != k) {
			throw new IllegalArgumentException("key");
		}
		return r;
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(String keyName) {
		return this.getKeyRestriction(this.owner.getKey(keyName).index);
	}

	final void load(DBAdapterImpl dbAdapter, RPTRecordSetRecordReader reader)
			throws SQLException {
		for (int i = 0, c = this.tables.size(); i < c; i++) {
			RPTRecordSetTableInfo tableInfo = this.tables.get(i);
			tableInfo.load(dbAdapter, reader);
		}
	}

	public final RPTRecordSetField newField(TableFieldDefine tableField) {
		return this.newField0((TableFieldDefineImpl) tableField);
	}
}
