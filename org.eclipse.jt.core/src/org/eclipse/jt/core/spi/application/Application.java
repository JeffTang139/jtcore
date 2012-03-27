package org.eclipse.jt.core.spi.application;

import java.io.File;
import java.util.List;

import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.misc.SXElement;


/**
 * Ӧ�ö���ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface Application {
	public <TUserData> Session newSession(
			SessionIniter<TUserData> sessionIniter, TUserData userData);

	/**
	 * ����ID�õ��Ự���ò����򷵻�SessionDisposedException�쳣
	 * 
	 * @param sessionID
	 *            �ỰID
	 * @throws SessionDisposedException
	 *             �޷���λ�Ựʱ�׳����쳣
	 */
	public Session getSession(long sessionID) throws SessionDisposedException;

	/**
	 * ��ȡϵͳ�Ự
	 */
	public Session getSystemSession();

	/**
	 * �����ͨ�Ự����������Ự��
	 */
	public int getNormalSessionCount();

	/**
	 * ���������ͨ�Ự�б�
	 */
	public List<? extends Session> getNormalSessions();

	/**
	 * ���HTTP������ֽ����ۼ�
	 */
	public long getHTTPRequestBytes();

	/**
	 * ���HTTPӦ����ֽ����ۼ�
	 */
	public long getHTTPResponseBytes();

	/**
	 * ���D&A�ĸ�Ŀ¼
	 */
	public File getDNARoot();

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name);

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name1, String name2);

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name1, String name2, String... names);

	/**
	 * ��ȡӦ�ó�������ʱ���(���� )
	 * 
	 * @return
	 */
	public long getBornTime();
}
