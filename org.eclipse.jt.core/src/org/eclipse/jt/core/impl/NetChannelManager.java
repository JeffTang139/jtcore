package org.eclipse.jt.core.impl;

import java.net.URL;

/**
 * 连接管理器接口
 * 
 * @author Jeff Tang
 * 
 */
public interface NetChannelManager {
	/**
	 * 根据地址获得连接
	 * 
	 * @param address
	 *            远程节点地址
	 */
	public NetChannel getChannel(URL address);

	/**
	 * 设置IO处理器
	 * 
	 * @param handler
	 *            IO处理器
	 */
	public DataPackageReceiver setNetIOHandler(DataPackageReceiver handler);

	/**
	 * 获取IO处理器
	 */
	public DataPackageReceiver getNetIOHandler();
}
