package org.eclipse.jt.core;
/**
 * 客户端或浏览器信息
 * @author Jeff Tang
 *
 */
public interface RemoteInfo {
	/**
	 * 客户端的主机名
	 */
	public String getRemoteHost();
	/**
	 * 客户端的地址
	 */
	public String getRemoteAddr();
}
