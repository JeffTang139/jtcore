package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

/**
 * �������ӽӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface NetChannel {
	/**
	 * ���Զ�̽ڵ�ID
	 */
	public GUID getRemoteNodeID();

	/**
	 * ���Զ�̽ڵ�ļ�Ⱥ���
	 */
	public int getRemoteNodeClusterIndex();

	/**
	 * ���Զ��Ӧ��ID��һ����Ⱥ�ڲ���ȫ���ڵ㹲��һ��AppID;
	 */
	public GUID getRemoteAppID();

	/**
	 * ��ȡԶ�����л����İ汾��
	 */
	public short getRemoteSerializeVersion();

	/**
	 * ��ȡԶ��Ӧ�õ�ʵ���汾
	 */
	public long getRemoteAppInstanceVersion();

	/**
	 * ��ʼ��������
	 * 
	 * @param <TAttachment>
	 *            ��������
	 * @param handler
	 *            ���ʹ�����
	 * @param attachment
	 *            ����
	 * @return �����첽���ƾ��
	 */
	public <TAttachment> AsyncIOStub<TAttachment> startSendingPackage(
			DataFragmentBuilder<? super TAttachment> builder,
			TAttachment attachment);

}
