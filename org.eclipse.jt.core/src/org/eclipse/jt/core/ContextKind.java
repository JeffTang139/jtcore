package org.eclipse.jt.core;

/**
 * ����������
 * 
 * @author Jeff Tang
 * 
 */
public enum ContextKind {
	/**
	 * ϵͳ�ĳ�ʼ�������ģ��ɿ�ܷ���
	 */
	INITER,
	/**
	 * �龰�����ģ��ɿ���ⷢ��
	 */
	SITUATION,
	/**
	 * һ�������ģ��ɿ���ⷢ��
	 */
	NORMAL,
	/**
	 * ��ʱ�����ģ�Ϊ�첽���ú�Զ�̵���׼�����ɿ�ܷ���
	 */
	TRANSIENT,
	/**
	 * �Ự���������ģ��ɿ�ܷ���
	 */
	DISPOSER;
	/**
	 * �׳���Ч����쳣
	 */
	public static final void throwIllegalContextKind(ContextKind kind) {
		throw new IllegalStateException("��Ч�����������" + kind);
	}
}