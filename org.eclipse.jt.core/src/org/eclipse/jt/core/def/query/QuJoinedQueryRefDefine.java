package org.eclipse.jt.core.def.query;

/**
 * ��ѯ��ʹ�õ����Ӳ�ѯ����
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
