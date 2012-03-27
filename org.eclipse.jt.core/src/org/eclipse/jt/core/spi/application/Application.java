package org.eclipse.jt.core.spi.application;

import java.io.File;
import java.util.List;

import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 应用对象接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Application {
	public <TUserData> Session newSession(
			SessionIniter<TUserData> sessionIniter, TUserData userData);

	/**
	 * 根据ID得到会话，得不到则返回SessionDisposedException异常
	 * 
	 * @param sessionID
	 *            会话ID
	 * @throws SessionDisposedException
	 *             无法定位会话时抛出的异常
	 */
	public Session getSession(long sessionID) throws SessionDisposedException;

	/**
	 * 获取系统会话
	 */
	public Session getSystemSession();

	/**
	 * 获得普通会话个数（界面会话）
	 */
	public int getNormalSessionCount();

	/**
	 * 获得所有普通会话列表
	 */
	public List<? extends Session> getNormalSessions();

	/**
	 * 获得HTTP请求的字节数累计
	 */
	public long getHTTPRequestBytes();

	/**
	 * 获得HTTP应答的字节数累计
	 */
	public long getHTTPResponseBytes();

	/**
	 * 获得D&A的根目录
	 */
	public File getDNARoot();

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name);

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name1, String name2);

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name1, String name2, String... names);

	/**
	 * 获取应用程序启动时间戳(毫秒 )
	 * 
	 * @return
	 */
	public long getBornTime();
}
