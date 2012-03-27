package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.exp.ConstExpression;
import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * 逻辑字段定义
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldDefine extends FieldDefine, RelationColumnDefine {

	/**
	 * 获取所属的逻辑表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获取字段实际存储的数据库表定义
	 * 
	 * @return
	 */
	public DBTableDefine getDBTable();

	/**
	 * 获取是否是主键字段（逻辑主键）
	 * 
	 * @return
	 */
	public boolean isPrimaryKey();

	/**
	 * 获取是否是记录行标识字段(RECID);
	 * 
	 * @return
	 */
	public boolean isRECID();

	/**
	 * 获取是否是行版本字段(RECVER)
	 * 
	 * @return
	 */
	public boolean isRECVER();

	public ConstExpression getDefault();

	/**
	 * 数据库中的实际名，该名称通常是定义名，但有可能会随数据库不同而有差异
	 * 
	 * @return 返回实际名
	 */
	public String getNameInDB();

	/**
	 * 是否为模板字段
	 * 
	 * @return
	 */
	public boolean isTemplated();

}
