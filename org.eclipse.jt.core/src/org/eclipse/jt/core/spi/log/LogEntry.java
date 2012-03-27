package org.eclipse.jt.core.spi.log;

import org.eclipse.jt.core.RemoteInfo;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.info.Info;

/**
 * ��־��
 * 
 * @author Jeff Tang
 * 
 */
public interface LogEntry {
	/**
	 * ��ûỰID
	 */
	public long getSessionID();

	/**
	 * ����û���Ϣ
	 */
	public User getUser();

	/**
	 * ���Զ����Ϣ
	 */
	public RemoteInfo getRemoteInfo();

	/**
	 * �����Ϣ���Ҫʱ����ǿ��ת��ΪProcessInfo
	 */
	public Info getInfo();

	/**
	 * �����־�����<br>
	 * ����־�����Ϊ"PROCESS_BEGIN"ʱ<br>
	 * �������ʱ��ʹ��ProcessInfo.getID()��Ϊ��¼��RECID
	 */
	public LogEntryKind getKind();
}
