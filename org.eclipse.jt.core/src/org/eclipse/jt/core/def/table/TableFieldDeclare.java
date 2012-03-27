package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.FieldDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDeclare;
import org.eclipse.jt.core.type.DataType;

/**
 * 逻辑字段定义
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldDeclare extends TableFieldDefine, FieldDeclare,
		RelationColumnDeclare {

	/**
	 * 表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 数据库表
	 */
	public DBTableDeclare getDBTable();

	/**
	 * 设置是否是主键字段（逻辑主键）
	 */
	public void setPrimaryKey(boolean value);

	/**
	 * 设置默认值
	 * 
	 * @param value
	 */
	public void setDefault(Object value);

	/**
	 * 尝试改变字段的精度，只针对字符串和二进制串有定点小数有效。
	 */
	public boolean adjustType(DataType newType);

	/**
	 * 设置数据库中列名称
	 */
	public void setNameInDB(String nameInDB);

	/**
	 * 设置是否作为模板字段
	 * 
	 * @param templated
	 */
	public void setTemplated(boolean templated);
}
