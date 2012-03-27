package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.exception.NotDBTypeException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;

/**
 * �߼����ֶζ���ʵ����
 * 
 * @author Jeff Tang
 * 
 */
final class TableFieldDefineImpl extends NamedDefineImpl implements
		TableFieldDeclare, RelationColumn {

	public final boolean isRECID() {
		return this.owner.f_recid == this;
	}

	public final boolean isRECVER() {
		return this == this.owner.f_recver;
	}

	public final void digestType(Digester digester) {
		this.digestAuthAndName(digester);
		this.type.digestType(digester);
	}

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final DBTableDefineImpl getDBTable() {
		return this.dbTable;
	}

	public final DataType getType() {
		return this.type;
	}

	public final boolean adjustType(DataType newType) {
		return this.internalAdjustType(newType);
	}

	public final boolean isPrimaryKey() {
		IndexDefineImpl index = this.owner.logicalKey;
		if (this.isRECID()) {
			// too disgusting!!! but...must remain
			return index == null;
		} else {
			return index != null && index.findItem(this) != null;
		}
	}

	public final void setPrimaryKey(boolean logicalKey) {
		this.checkModifiable();
		if (logicalKey) {
			this.owner.addKey(this);
		} else {
			this.owner.removeKey(this);
		}
	}

	public final boolean isKeepValid() {
		return this.notNull;
	}

	public final void setKeepValid(boolean value) {
		this.checkModifiable();
		if (this.isRECID()) {
			throw new UnsupportedOperationException("�����޸�RECID�ֶεķǿ�����.");
		} else if (this.isRECVER()) {
			throw new IllegalArgumentException("�����޸�RECVER�ֶεķǿ�����.");
		}
		if (value) {
			this.notNull = true;
		} else if (!this.isPrimaryKey()) {
			this.notNull = false;
		}
	}

	public final ConstExpr getDefault() {
		return this.defaultValue;
	}

	public final void setDefault(ValueExpression expr) {
		this.checkModifiable();
		if (expr == null || expr == NullExpr.NULL) {
			this.defaultValue = null;
		} else {
			try {
				this.defaultValue = (ConstExpr) expr;
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("�ֶε�Ĭ��ֵ����Ϊ����.", e);
			}
		}
	}

	public final void setDefault(Object value) {
		this.checkModifiable();
		if (value == null) {
			this.defaultValue = null;
		} else {
			this.defaultValue = this.type.detect(ConstExpr.parser, value);
		}
	}

	public final boolean isReadonly() {
		return false;
	}

	public final void setReadonly(boolean value) {
		throw new UnsupportedOperationException();
	}

	public final String getNameInDB() {
		return this.namedb;
	}

	public final void setNameInDB(String namedb) {
		this.checkModifiable();
		if (namedb == null || namedb.length() == 0) {
			throw new NullArgumentException("�ֶ������ݿ�������");
		}
		this.dbTable.unstore(this);
		this.namedb = namedb.toUpperCase();
		this.dbTable.store(this);
	}

	public final boolean isTemplated() {
		return this.templated;
	}

	public final void setTemplated(boolean templated) {
		this.checkModifiable();
		this.templated = templated;
	}

	final TableDefineImpl owner;

	final DBTableDefineImpl dbTable;

	private DataType type;

	private ConstExpr defaultValue;

	private boolean notNull;

	private String namedb;

	boolean templated;

	TableFieldDefineImpl(TableDefineImpl owner, DBTableDefineImpl dbTable,
			String name, String namedb, DataType type, boolean notNull) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		// CORE2.5 ���������ֶθ���
		this.namedb = namedb.toUpperCase();
		this.type = type;
		this.notNull = notNull;
	}

	final String namedb() {
		return this.namedb;
	}

	final boolean internalAdjustType(DataType type) {
		this.checkModifiable();
		if (!type.isDBType()) {
			throw new NotDBTypeException(type.toString());
		}
		if (this.type == null) {
			this.type = type;
			return true;
		} else if (this.type == type) {
			return false;
		} else if (this.type.canDBTypeConvertTo(type)) {
			this.type = type;
			return true;
		}
		return false;
	}

	static final TableFieldDefineImpl newForMerge(TableDefineImpl owner,
			DBTableDefineImpl dbTable, String name) {
		return new TableFieldDefineImpl(owner, dbTable, name);
	}

	private TableFieldDefineImpl(TableDefineImpl owner,
			DBTableDefineImpl dbTable, String name) {
		super(name);
		this.owner = owner;
		this.dbTable = dbTable;
		this.namedb = name.toUpperCase();
	}

	final int index() {
		return this.owner.fields.indexOf(this);
	}

	final TableFieldDefineImpl clone(TableDefineImpl table) {
		DBTableDefineImpl dbTable = table.dbTables.get(this.dbTable.name);
		TableFieldDefineImpl clone = new TableFieldDefineImpl(table, dbTable,
				this);
		dbTable.store(clone);
		return clone;
	}

	private TableFieldDefineImpl(TableDefineImpl owner,
			DBTableDefineImpl dbTable, TableFieldDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.dbTable = owner.dbTables.get(sample.dbTable.name);
		this.type = sample.type;
		this.namedb = sample.namedb;
		this.templated = sample.templated;
		this.notNull = sample.notNull;
		this.defaultValue = sample.defaultValue;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "field";

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		TableFieldDefineImpl f = (TableFieldDefineImpl) sample;
		if (!this.dbTable.name.equals(f.dbTable.name)) {
			throw new UnsupportedOperationException();
		}
		this.type = f.type;
		this.namedb = f.namedb;
		this.templated = f.templated;
		this.notNull = f.notNull;
		this.defaultValue = f.defaultValue;
	}

	/**
	 * ȷ���ֶζ����namedb�Ϸ�
	 * 
	 * <p>
	 * �ظ����Ѿ���map��֤;����Ƿ�Ϊ�ؼ���;����Ƿ񳬹����ݿⳤ������.
	 * 
	 * @param lang
	 * @return namedb�Ƿ����޸�
	 */
	final boolean ensureValid(final DBLang lang) {
		final int maxlen = lang.getMaxColumnNameLength();
		final String namedb = this.namedb;
		if (namedb.length() > maxlen || lang.filterKeyword(namedb)) {
			String rebuild = Utils.buildIdentityName(namedb, maxlen,
					new Filter<String>() {
						public boolean accept(String item) {
							return TableFieldDefineImpl.this.dbTable.fields
									.containsKey(item)
									|| lang.filterKeyword(item);
						}
					});
			this.dbTable.unstore(this);
			this.namedb = rebuild;
			this.dbTable.store(this);
			return true;
		}
		return false;
	}

	final void adjustNamedb(final DBLang lang) {
		this.namedb = Utils.buildIdentityName(this.namedb,
				lang.getMaxColumnNameLength(), new Filter<String>() {
					public boolean accept(String item) {
						return TableFieldDefineImpl.this.dbTable.fields
								.containsKey(item) || lang.filterKeyword(item);
					}
				});
	}

}
