package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.obja.StructFieldDefine;

/**
 * 查询语句定义的输出列定义
 * 
 * @author Jeff Tang
 */
public interface QueryColumnDefine extends SelectColumnDefine {

	/**
	 * 获取所属的查询语句定义
	 * 
	 * @return
	 */
	public QueryStatementDefine getOwner();

	/**
	 * 获取映射到的字段,可以用以读取MO,ORMEntity,RO的对应值
	 * 
	 * @return 返回映射到的字段
	 */
	public StructFieldDefine getMapingField();
}
