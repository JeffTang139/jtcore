package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.query.RelationRefDefine;

/**
 * 表引用接口
 * 
 * <p>
 * 继承至关系引用定义,表示目标类型为表定义的关系引用.
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface TableReferenceDefine extends RelationRefDefine {

	/**
	 * 获取目标逻辑表
	 */
	public TableDefine getTarget();

}