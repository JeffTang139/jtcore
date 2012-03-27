package org.eclipse.jt.core.def.query;

/**
 * 查询引用定义
 * 
 * <p>
 * 继承至关系引用定义,表示目标类型为查询定义的关系引用.
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 */
public interface QueryReferenceDefine extends RelationRefDefine {

	public SelectDefine getTarget();
}
