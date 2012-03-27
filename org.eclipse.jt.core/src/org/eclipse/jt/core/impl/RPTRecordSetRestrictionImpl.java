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
 * 记录集约束
 * 
 * @author Jeff Tang
 * 
 */
final class RPTRecordSetRestrictionImpl implements RPTRecordSetRestriction {

	// 记录集约束的键约束,不指定则使用RecordSet的默认约束
	// 大小同RecordSet的keys的大小,且对应
	private RPTRecordSetKeyRestrictionImpl[] keyRestrictions;

	// 大小同RecordSet的keys的大小,且对应,在rstr.newField方法后,才不会null.
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
	 * updateSize(-1)表示参考RecordSet的keys个数,更新validKeys及键约束的支持信息
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
			throw new IllegalStateException("对象已经失效");
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
	 * 获取使用的键约束
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
		// 如果约束的键约束为空,则使用RecordSet的默认键约束
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
	 * 如果记录的key值为空,尝试设值
	 * 
	 * @param record
	 *            结果对象
	 * @param index
	 *            键的序号
	 * @return
	 */
	final boolean tryUpdateKeyFieldValueIfNull(RPTRecord record, int index) {
		if (!this.isKeySupported(index)) {
			return false;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		// 自身的键约束为空
		if (kr == null) {
			// 则使用RecordSet的默认约束
			kr = this.owner.keys.get(index).defaultKeyRestriction;
		}
		return kr.tryUpdateKeyFieldValueIfNull(record);
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			throw new IllegalArgumentException("本约束不支持键["
					+ this.owner.getKey(index).getName() + "]");
		}
		RPTRecordSetKeyRestrictionImpl[] krs = this.keyRestrictions;
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			// 默认情况下使用rs对应kr的default约束
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
