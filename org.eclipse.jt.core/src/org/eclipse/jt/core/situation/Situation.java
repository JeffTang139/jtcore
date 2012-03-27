package org.eclipse.jt.core.situation;

import org.eclipse.jt.core.Context;

/**
 * �龰
 * 
 * @author Jeff Tang
 * 
 */
public interface Situation extends Context {
	/**
	 * ��ø����龰
	 */
	public Situation getParent();

	/**
	 * ��ø����龰
	 */
	public Situation getRoot();

	/**
	 * ���Լ��Լ������¼�������Ϣ����Ϣ�������֮��ŷ��ء�
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @return ������Ϣ��������е�һЩ��Ϣ
	 */
	public <TMessage> MessageResult<TMessage> broadcastMessage(TMessage message);

	/**
	 * ���Լ��Լ������¼��㲥��Ϣ����Ϣ�������֮��ŷ��ء�
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @param maxDistance
	 *            ���㲥���
	 * @return ������Ϣ��������е�һЩ��Ϣ
	 */
	public <TMessage> MessageResult<TMessage> broadcastMessage(
			TMessage message, int maxDistance);

	/**
	 * ���Լ��Լ����ϼ����η�����Ϣ����Ϣ�������֮��ŷ��ء�
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @return ������Ϣ��������е�һЩ��Ϣ
	 */
	public <TMessage> MessageResult<TMessage> bubbleMessage(TMessage message);

	/**
	 * ���Լ��Լ����ϼ����η�����Ϣ����Ϣ�������֮��ŷ��ء�
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @param maxDistance
	 *            ���ð�ݸ߶�
	 * @return ������Ϣ��������е�һЩ��Ϣ
	 */
	public <TMessage> MessageResult<TMessage> bubbleMessage(TMessage message,
			int maxDistance);

	/**
	 * �ύ�첽��Ϣ����Ϣ�ŵ����н�β���ȴ��龰�����´���Ӧ
	 */
	public <TMessage> PendingMessage<TMessage> postBroadcastMessage(
			TMessage message, int maxDistance);

	/**
	 * �ύ�첽��Ϣ����Ϣ�ŵ����н�β���ȴ��龰�����´���Ӧ
	 */
	public <TMessage> PendingMessage<TMessage> postBroadcastMessage(
			TMessage message);

	/**
	 * �ύ�첽��Ϣ����Ϣ�ŵ����н�β���ȴ��龰�����´���Ӧ
	 */
	public <TMessage> PendingMessage<TMessage> postBubbleMessage(
			TMessage message, int maxDistance);

	/**
	 * �ύ�첽��Ϣ����Ϣ�ŵ����н�β���ȴ��龰�����´���Ӧ
	 */
	public <TMessage> PendingMessage<TMessage> postBubbleMessage(
			TMessage message);

	/**
	 * ��������ĳ��Ϣ
	 * 
	 * @param <TMessage>
	 *            ��Ϣ����
	 * @param messageClass
	 *            ��Ϣ����
	 * @param listener
	 *            ������
	 * @param directions
	 *            ������Ϣ�ķ��򣬲�ָ������������ַ���
	 */
	public <TMessage> MessageListenerRegHandle<TMessage> regMessageListener(
			Class<TMessage> messageClass,
			MessageListener<? super TMessage> listener);
}
