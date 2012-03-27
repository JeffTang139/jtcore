package org.eclipse.jt.core.spi.model;

/**
 * ģ�͵�������λ��
 * 
 * @author Jeff Tang
 * 
 */
public enum ModelCallScope {
	/**
	 * ��������������
	 */
	IMPL_SETTER,
	/**
	 * �����Ի�ȡ����
	 */
	IMPL_GETTER,
	/**
	 * �ڶ�����
	 */
	IMPL_ACTION,
	/**
	 * ��Լ����
	 */
	IMPL_CONSTRAINT,
	/**
	 * �ڹ��췽����
	 */
	IMPL_CONSTRUCTOR,
	/**
	 * ��ģ��ʵ��Դ��
	 */
	IMPL_SOURCE,
	/**
	 * �ⲿ����
	 */
	OUTER,
}
