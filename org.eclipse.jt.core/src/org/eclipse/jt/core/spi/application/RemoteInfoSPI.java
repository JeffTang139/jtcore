package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.RemoteInfo;

public interface RemoteInfoSPI extends RemoteInfo {
	/**
	 * 客户端的主机名
	 */
	public void setRemoteHost(String host);

	/**
	 * 客户端的地址
	 */
	public void setRemoteAddr(String addr);
}
