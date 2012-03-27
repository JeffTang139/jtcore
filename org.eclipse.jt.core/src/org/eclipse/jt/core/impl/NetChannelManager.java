package org.eclipse.jt.core.impl;

import java.net.URL;

/**
 * ���ӹ������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface NetChannelManager {
	/**
	 * ���ݵ�ַ�������
	 * 
	 * @param address
	 *            Զ�̽ڵ��ַ
	 */
	public NetChannel getChannel(URL address);

	/**
	 * ����IO������
	 * 
	 * @param handler
	 *            IO������
	 */
	public DataPackageReceiver setNetIOHandler(DataPackageReceiver handler);

	/**
	 * ��ȡIO������
	 */
	public DataPackageReceiver getNetIOHandler();
}
