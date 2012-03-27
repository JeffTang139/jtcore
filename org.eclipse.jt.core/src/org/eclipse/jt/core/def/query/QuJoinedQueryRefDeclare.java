package org.eclipse.jt.core.def.query;

/**
 * 查询中使用的连接查询引用
 * 
 * @see org.eclipse.jt.core.def.query.QuJoinedQueryRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QuJoinedQueryRefDeclare extends QuJoinedQueryRefDefine,
		QuJoinedRelationRefDeclare, QuQueryRefDeclare,
		JoinedQueryReferenceDeclare {

	public DerivedQueryDeclare getTarget();

}
