package org.eclipse.jt.core.def.query;

/**
 * ��ѯ������ʹ�õĲ�ѯ���ö���
 * 
 * @author Jeff Tang
 * 
 */
public interface QuQueryRefDefine extends QuRelationRefDefine,
		QueryReferenceDefine {

	public DerivedQueryDefine getTarget();
}
