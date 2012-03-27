package org.eclipse.jt.core.def.query;

/**
 * 查询定义中使用的连接的关系引用
 * 
 * @see org.eclipse.jt.core.def.query.QuJoinedRelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QuJoinedRelationRefDeclare extends QuJoinedRelationRefDefine,
		QuRelationRefDeclare, JoinedRelationRefDeclare {

	@Deprecated
	public QuJoinedTableRefDeclare castAsTableRef();

	@Deprecated
	public QuJoinedQueryRefDeclare castAsQueryRef();
}
