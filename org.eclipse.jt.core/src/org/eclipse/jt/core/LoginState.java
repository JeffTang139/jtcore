package org.eclipse.jt.core;

/**
 * ��½״̬
 * 
 * @author Jeff Tang
 * 
 */
public enum LoginState {
	/**
	 * ����״̬��û���û���Ϣ���û���صĲ�����ֹ
	 */
	ANONYNOUS,
	/**
	 * �ɹ���½���״̬�����Խ���һϵ���û���صĲ���
	 */
	LOGIN,
	/**
	 * ����״̬����������ͻỰ��ʱ���жϺ�Ĺ����û���صĲ�����ֹ
	 */
	// SUSPENDED,
	/**
	 * ������״̬
	 */
	DISPOSING,
	/**
	 * ����״̬
	 */
	DISPOSED
}
