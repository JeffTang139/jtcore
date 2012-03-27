package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.DBTableDeclare;
import org.eclipse.jt.core.exception.TableSynchronizationException;
import org.eclipse.jt.core.type.DataType;

/**
 * 物理表定义
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
	 * 克隆到目标逻辑表中
	 * 
	 * @param table
	 * @return
	 */
	final DBTableDefineImpl clone(TableDefineImpl table) {
		return new DBTableDefineImpl(table, this);
	}

	/**
	 * 克隆的构造方法
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
	 * 只在创建该表之前调用,确保物理表定义合法
	 * 
	 * <ul>
	 * <li>表名长度超过数据库限制或表名为数据库的关键字则抛出异常.
	 * <li>各字段名称长度超过数据库限制或与数据库关键字相同则重建名称.
	 * <li>主键索引名称如果长度超过数据库限制则重建名称.
	 * </ul>
	 */
	final boolean ensureValid(final DBLang lang) {
		final String namedb = this.namedb;
		if (namedb.length() > lang.getMaxTableNameLength()) {
			throw new TableSynchronizationException(this.owner, "物理表[" + namedb
					+ "]表名长度[" + namedb.length() + "]超过数据库表名长度最大值["
					+ lang.getMaxTableNameLength() + "].");
		}
		if (lang.filterKeyword(namedb)) {
			if (SystemVariables.TABLE_NAME_KEYWORD_EXCEPTION) {
				throw new TableSynchronizationException(this.owner, "物理表["
						+ namedb + "]表名为保留字");
			} else {
				System.err.println("在逻辑表定义[" + this.owner.name + "],物理表名称["
						+ this.namedb + "]为数据库关键字,强烈不建议使用.");
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
