package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.RemoteInfo;

public interface RemoteInfoSPI extends RemoteInfo {
	/**
	 * �ͻ��˵�������
	 */
	public void setRemoteHost(String host);

	/**
	 * �ͻ��˵ĵ�ַ
	 */
	public void setRemoteAddr(String addr);
}
