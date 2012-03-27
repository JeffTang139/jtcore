package org.eclipse.jt.core.situation;

/**
 * ��Ϣ���ݿ�����
 * 
 * @author Jeff Tang
 * 
 * @param <TMessage>
 */
public interface MessageTransmitter<TMessage> extends MessageResult<TMessage> {
	/**
	 * �����Ϣ�ķ����ߵ��龰
	 */
	public Situation getSender();

	/**
	 * ��ȡ��ǰ�龰�������ģ�
	 */
	public Situation getContext();

	/**
	 * ��õ�ǰ��Ϣ�Ĵ��ݷ���
	 */
	public MessageDirection getDirection();

	/**
	 * ��ü�����ע���������Ըı��������
	 */
	public MessageListenerRegHandle<TMessage> getRegHandle();

	/**
	 * ��õ�ǰ�龰����Ϣ�ķ����龰��ľ���(�뷢����֮��)
	 */
	public int getDistance();

	/**
	 * ��ȡ��Ϣ������������Զ���루�뷢����֮�䣩
	 */
	public int getMaxDistance();

	/**
	 * ������Ϣ������������Զ���루�뷢����֮�䣩
	 */
	public void setMaxDistance(int value);

	/**
	 * ��ֹ��ǰ��Ϣ�ļ������ݣ���������Զ����������Ϊ<0��
	 */
	public void terminate();
}
