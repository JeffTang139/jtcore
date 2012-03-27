package org.eclipse.jt.core.impl;

/**
 * �첽������
 * 
 * @author Jeff Tang
 * 
 */
public interface AsyncIOStub<TAttachment> {
	/**
	 * ȡ���첽����
	 */
	public void cancel();

	/**
	 * ����
	 */
	public void suspend();

	/**
	 * �ָ�
	 */
	public void resume();

	/**
	 * ��ȡ����Ӧ�ĸ���
	 */
	public TAttachment getAttachment();
}
