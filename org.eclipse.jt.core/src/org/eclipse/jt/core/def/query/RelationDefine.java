package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * 关系的元定义
 * 
 * <p>
 * 关系的元定义即为二维表形式的数据结构的元数据定义
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationDefine extends NamedDefine {

	/**
	 * 查找指定名称的关系列定义
	 * 
	 * @param columnName
	 * @return 返回关系列定义或null
	 */
	public RelationColumnDefine findColumn(String columnName);

	/**
	 * 获取指定名称的关系列定义
	 * 
	 * @param columnName
	 * @return 返回关系列定义或抛出异常
	 */
	public RelationColumnDefine getColumn(String columnName);
}
