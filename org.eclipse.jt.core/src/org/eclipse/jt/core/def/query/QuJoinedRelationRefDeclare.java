package org.eclipse.jt.core.def.query;

/**
 * ��ѯ������ʹ�õ����ӵĹ�ϵ����
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
