package org.eclipse.jt.core.def.query;

/**
 * ��ѯ��ʹ�õ����Ӳ�ѯ����
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
