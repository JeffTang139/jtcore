package org.eclipse.jt.core.invoke;

/**
 * �첽���������״̬
 * 
 * @author Jeff Tang
 * 
 */
public enum AsyncState {
	/**
	 * �ύ���첽���󣨱��ص��ã�
	 */
	POSTING(false, false, false),
	/**
	 * �ȴ������̵߳ĵ��ȣ����ص��ã�
	 */
	SCHEDULING(false, false, false),
	/**
	 * ��Ϊ�������Ƶ�ԭ�򣬽��벢�������ж���
	 */
	QUEUING(false, false, false),
	/**
	 * ���벢�������ж��У��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	QUEUING_WAITED(false, true, false),
	/**
	 * �Ѿ�����ִ���жӣ�ֻҪ�п��е��߳̾ͻῪʼ����
	 */
	STARTING(false, false, false),
	/**
	 * �Ѿ�����ִ���жӣ��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	STARTING_WAITED(false, true, false),
	/**
	 * ���ڴ���
	 */
	PROCESSING(false, false, false),
	/**
	 * ���ڴ����ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	PROCESSING_WAITED(false, true, false),
	/**
	 * ����ȡ����
	 */
	CANCELING(false, false, true),
	/**
	 * ����ȡ���У��ұ��ȴ��������Ѿ����ϣ���״̬��Զ�������
	 */
	@Deprecated
	CANCELING_WAITED(false, true, true),
	/**
	 * ����첽����
	 */
	FINISHED(true, false, false),
	/**
	 * �������
	 */
	ERROR(true, false, false),
	/**
	 * ȡ����ֹ
	 */
	CANCELED(true, false, true);
	/**
	 * �Ƿ��Ѿ�ֹͣ
	 */
	public final boolean stopped;

	/**
	 * �Ƿ������ȴ�����Զ����false
	 */
	@Deprecated
	public final boolean waited;
	/**
	 * �Ƿ���ȡ������
	 */
	public final boolean canceling;

	AsyncState(boolean stopped, boolean waited, boolean canceling) {
		this.stopped = stopped;
		this.waited = waited;
		this.canceling = canceling;
	}
}
