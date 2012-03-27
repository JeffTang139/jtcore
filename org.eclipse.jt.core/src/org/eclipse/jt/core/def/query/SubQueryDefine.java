package org.eclipse.jt.core.def.query;

/**
 * �Ӳ�ѯ����
 * 
 * <p>
 * ��ָ�ڷ�from�Ӿ���ʹ�õ��Ӳ�ѯ�ṹ.��DerivedQuery��֮ͬ������:����ʹ����ṹ������Ĺ�ϵ����.
 * 
 * <p>
 * �Ӳ�ѯ�������ת��Ϊֵ���ʽ
 * 
 * @see org.eclipse.jt.core.def.query.DerivedQueryDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface SubQueryDefine extends SelectDefine {

	/**
	 * ��ȡ�Ӳ�ѯ���ʽ
	 * 
	 * @return ֵ���ʽ
	 */
	public SubQueryExpression newExpression();

	/**
	 * ��ȡ��ѯ���ڵ�DML��
	 * 
	 * @return DML��䶨��
	 */
	public DMLDefine getOwner();
}
