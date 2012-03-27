package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.type.DataType;

/**
 * 物理表定义
 * 
 * @author Jeff Tang
 * 
 */
public interface DBTableDeclare extends DBTableDefine, NamedDeclare {

	/**
	 * 所属表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 创建存储在该物理表中的字段
	 */
	public TableFieldDeclare newField(String name, DataType type);

}
