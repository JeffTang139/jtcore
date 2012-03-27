package org.eclipse.jt.core.spi.log;

import org.eclipse.jt.core.RemoteInfo;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.info.Info;

/**
 * 日志项
 * 
 * @author Jeff Tang
 * 
 */
public interface LogEntry {
	/**
	 * 获得会话ID
	 */
	public long getSessionID();

	/**
	 * 获得用户信息
	 */
	public User getUser();

	/**
	 * 获得远程信息
	 */
	public RemoteInfo getRemoteInfo();

	/**
	 * 获得信息项，需要时可以强制转换为ProcessInfo
	 */
	public Info getInfo();

	/**
	 * 获得日志项类别<br>
	 * 当日志项类别为"PROCESS_BEGIN"时<br>
	 * 保存过程时请使用ProcessInfo.getID()作为记录的RECID
	 */
	public LogEntryKind getKind();
}
