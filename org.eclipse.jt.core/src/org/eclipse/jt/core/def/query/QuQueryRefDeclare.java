package org.eclipse.jt.core.def.query;

/**
 * ��ѯ������ʹ�õĲ�ѯ���ö���
 * 
 * @see org.eclipse.jt.core.def.query.QuQueryRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QuQueryRefDeclare extends QuQueryRefDefine,
		QuRelationRefDeclare, QueryReferenceDeclare {

	public DerivedQueryDeclare getTarget();
}
