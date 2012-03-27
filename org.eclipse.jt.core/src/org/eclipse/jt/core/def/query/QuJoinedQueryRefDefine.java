package org.eclipse.jt.core.def.query;

/**
 * 查询中使用的连接查询引用
 * 
 * @see org.eclipse.jt.core.def.query.QuJoinedRelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QuJoinedQueryRefDefine extends QuJoinedRelationRefDefine,
		QuQueryRefDefine, JoinedQueryReferenceDefine {

	public DerivedQueryDefine getTarget();

}
