package org.eclipse.jt.core.impl;

/**
 * ��������Դ
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NewAcquirable {
	/**
	 * ID��ʶ�������ڶ���ڵ��ȷ����Դ
	 */
	long id;
	/**
	 * �������жӵĶ�β
	 */
	@SuppressWarnings("unchecked")
	NewAcquirer tail;
}
