package org.eclipse.jt.core.situation;

/**
 * ��Ϣ������ע����
 * 
 * @author Jeff Tang
 * 
 */
public interface MessageListenerRegHandle<TMessage> {
	/**
	 * ��ȡע�����Ϣ������
	 */
	public MessageListener<? super TMessage> getListener();

	/**
	 * ��ȡ��Ϣ����
	 */
	public Class<TMessage> getMessageClass();

	/**
	 * �����Ч�ԣ�Ĭ��Ϊtrue
	 */
	public boolean isEnabled();

	/**
	 * ������Ч�ԣ�Ĭ��Ϊtrue
	 * 
	 * @param value
	 *            �������Ƿ���Ч
	 */
	public void setEnabled(boolean value);

	/**
	 * �Ƿ����ע����Ϣ���͵������ͣ�Ĭ��Ϊfalse
	 */
	public boolean isListenSubMessage();

	/**
	 * �����Ƿ����ע����Ϣ���͵������ͣ�Ĭ��Ϊfalse
	 */
	public void setListenSubMessage(boolean value);

	/**
	 * �Ƿ����ð����Ϣ��Ĭ��Ϊtrue
	 */
	public boolean isListenBubble();

	/**
	 * �����Ƿ����ð�ݣ����ϼ����ͣ���Ϣ��Ĭ��Ϊtrue
	 */
	public void setListenBubble(boolean value);

	/**
	 * �Ƿ�����㲥�����¼����ͣ���Ϣ��Ĭ��Ϊtrue
	 */
	public boolean isListenBroadcast();

	/**
	 * �����Ƿ�����㲥�����¼����ͣ���Ϣ��Ĭ��Ϊtrue
	 */
	public void setListenBroadcast(boolean value);

	/**
	 * ע���ü�����
	 */
	public void unRegister();

	/**
	 * �����Ƿ���ע��״̬
	 */
	public boolean isRegistered();

	/**
	 * ���������,Ĭ����ע�ᵽ���龰����,�����߹ر�ʱ��ע��ӵ�еļ�������ע���󷵻�null
	 */
	public Situation getOwner();

	/**
	 * ����������,�����������߹ر�ʱע��������
	 */
	public void setOwner(Situation owner);

	/**
	 * ��ü�����ע�ᵽ���龰��ע���󷵻�null
	 * 
	 * @return
	 */
	public Situation getSituation();
}
