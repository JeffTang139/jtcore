package org.eclipse.jt.core.def.query;

/**
 * ��ѯ���ö���
 * 
 * <p>
 * �̳�����ϵ���ö���,��ʾĿ������Ϊ��ѯ����Ĺ�ϵ����.
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 */
public interface QueryReferenceDefine extends RelationRefDefine {

	public SelectDefine getTarget();
}
