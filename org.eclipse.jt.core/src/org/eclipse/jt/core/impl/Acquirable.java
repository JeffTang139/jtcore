package org.eclipse.jt.core.impl;

/**
 * �ɱ�����ģ���д��������Ļ���
 * 
 * @author Jeff Tang
 * 
 */
class Acquirable {
	/**
	 * �������ж�
	 */
	@SuppressWarnings("unchecked")
	volatile Acquirer acquirer;
}
