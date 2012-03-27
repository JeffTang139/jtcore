package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.DBTableDeclare;
import org.eclipse.jt.core.exception.TableSynchronizationException;
import org.eclipse.jt.core.type.DataType;

/**
 * �������
 * 
 * @author Jeff Tang
 */
final class DBTableDefineImpl extends NamedDefineImpl implements DBTableDeclare {

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final int getFieldCount() {
		return this.fields.size();
	}

	public final TableFieldDefineImpl newField(String name, DataType type) {
		return this.owner.newField(this, name, type, false);
	}

	final TableDefineImpl owner;

	final StringKeyMap<TableFieldDefineImpl> fields = new StringKeyMap<TableFieldDefineImpl>();

	private String namedb;

	private String pkeyName;

	DBTableDefineImpl(TableDefineImpl owner, String name) {
		super(name.toUpperCase());
		this.owner = owner;
		this.namedb = name.toUpperCase();
		this.pkeyName = TableDefineImpl.DNA_PK_PREFIX.concat(this.namedb);
	}

	final boolean isPrimary() {
		return this == this.owner.primary;
	}

	final void store(TableFieldDefineImpl field) {
		this.fields.put(field.namedb(), field, true);
	}

	final void unstore(TableFieldDefineImpl field) {
		this.fields.remove(field.namedb());
	}

	final String namedb() {
		return this.namedb;
	}

	final void setNamedb(String namedb) {
		if (namedb == null || namedb.length() == 0) {
			throw new NullPointerException();
		}
		this.namedb = namedb.toUpperCase();
	}

	final String getPkeyName() {
		return this.pkeyName;
	}

	final void setPkeyName(String name) {
		if (name != null && name.length() > 0) {
			this.pkeyName = name.toUpperCase();
		}
	}

	final int index() {
		return this.owner.dbTables.indexOf(this);
	}

	/**
	 * ��¡��Ŀ���߼�����
	 * 
	 * @param table
	 * @return
	 */
	final DBTableDefineImpl clone(TableDefineImpl table) {
		return new DBTableDefineImpl(table, this);
	}

	/**
	 * ��¡�Ĺ��췽��
	 * 
	 * @param owner
	 * @param sample
	 */
	private DBTableDefineImpl(TableDefineImpl owner, DBTableDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.namedb = sample.namedb;
		this.pkeyName = sample.pkeyName;
	}

	/**
	 * ֻ�ڴ����ñ�֮ǰ����,ȷ���������Ϸ�
	 * 
	 * <ul>
	 * <li>�������ȳ������ݿ����ƻ����Ϊ���ݿ�Ĺؼ������׳��쳣.
	 * <li>���ֶ����Ƴ��ȳ������ݿ����ƻ������ݿ�ؼ�����ͬ���ؽ�����.
	 * <li>������������������ȳ������ݿ��������ؽ�����.
	 * </ul>
	 */
	final boolean ensureValid(final DBLang lang) {
		final String namedb = this.namedb;
		if (namedb.length() > lang.getMaxTableNameLength()) {
			throw new TableSynchronizationException(this.owner, "�����[" + namedb
					+ "]��������[" + namedb.length() + "]�������ݿ�����������ֵ["
					+ lang.getMaxTableNameLength() + "].");
		}
		if (lang.filterKeyword(namedb)) {
			if (SystemVariables.TABLE_NAME_KEYWORD_EXCEPTION) {
				throw new TableSynchronizationException(this.owner, "�����["
						+ namedb + "]����Ϊ������");
			} else {
				System.err.println("���߼�����[" + this.owner.name + "],���������["
						+ this.namedb + "]Ϊ���ݿ�ؼ���,ǿ�Ҳ�����ʹ��.");
			}

		}
		boolean r = false;
		for (int i = 0, c = this.owner.fields.size(); i < c; i++) {
			TableFieldDefineImpl field = this.owner.fields.get(i);
			if (field.dbTable == this) {
				r |= field.ensureValid(lang);
			}
		}
		return r;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "dbtable";

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		DBTableDefineImpl from = (DBTableDefineImpl) sample;
		this.namedb = from.namedb;
		this.pkeyName = from.pkeyName;
	}

}
