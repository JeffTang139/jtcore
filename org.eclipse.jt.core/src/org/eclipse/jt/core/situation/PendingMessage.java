package org.eclipse.jt.core.situation;

/**
 * �ж���Ϣ
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
public interface PendingMessage<TMessage> {
	/**
	 * ��ȡ���ж���Ϣ�Ƿ���Ч
	 */
	public boolean isValid();

	/**
	 * ��÷���
	 */
	public MessageDirection getDirection();

	/**
	 * ���Եõ����
	 * 
	 * @return
	 */
	public MessageResult<TMessage> tryGetResult();
}
