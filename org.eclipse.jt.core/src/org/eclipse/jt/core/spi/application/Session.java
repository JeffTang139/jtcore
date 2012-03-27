package org.eclipse.jt.core.spi.application;

import java.util.Locale;

import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.exception.SituationReentrantException;
import org.eclipse.jt.core.type.GUID;


/**
 * �Ự�ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface Session extends Login {
	/**
	 * �Ự����֤��
	 */
	public long getVerificationCode();

	/**
	 * �����Ự
	 * 
	 * @param asSituation
	 *            �Ƿ���Ϊ�龰�����ģ�����UI���̣߳�
	 * @throws SessionDisposedException
	 *             �Ự�Ѿ�����
	 * @throws SituationReentrantException
	 *             ������龰�����ģ��򱨸��龰�����쳣���Ѿ�������������δ�˳���UI���̣߳�
	 */
	public <TUserData> ContextSPI newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException;

	/**
	 * ��ȡApplication
	 */
	public Application getApplication();

	/**
	 * ��ø��龰����
	 */
	public SituationSPI getSituation();

	/**
	 * �����龰����
	 */
	public SituationSPI resetSituation();

	/**
	 * ��ȡ�Ự��������
	 */
	public Object getData();

	/**
	 * ���ûỰ����
	 */
	public Object setData(Object data);

	/**
	 * ��ȡԶ����Ϣ
	 */
	public RemoteInfoSPI getRemoteInfo();

	/**
	 * ���ûỰ�ķ�λ
	 */
	public void setLocale(Locale locale);

	/**
	 * Ĭ�ϵ�������ʱʱ�䣺5����
	 */
	public final static int DEFAULT_HEARTBEAT_SECs = 60 * 5;

	/**
	 * ���������ʱʱ�䣨�룩��0��ʾ������ʱ��Ĭ��5����
	 */
	public int getHeartbeatTimeoutSec();

	/**
	 * ����������ʱʱ�䣨�룩��0��ʾ������ʱ��Ĭ��Ϊ5����
	 */
	public void setHeartbeatTimeoutSec(int heartbeatTimeoutSec);

	/**
	 * Ĭ�ϵĻỰ��ʱʱ�䣺30����
	 */
	public final static int DEFAULT_TIMEOUT_MINUTEs = 30;

	/**
	 * ��ûỰ��ʱʱ�䣨���ӣ���0��ʾ������ʱ��Ĭ��30����
	 */
	public int getSessionTimeoutMinutes();

	/**
	 * ���ûỰ��ʱʱ�䣨���ӣ���0��ʾ������ʱ��Ĭ��30����
	 */
	public void setSessionTimeoutMinutes(int sessionTimeoutMinutes);

	/**
	 * ������Ľ���ʱ�䣬ֻ�����ͨ�Ự��Ч
	 */
	public long getLastInteractiveTime();

	/**
	 * �������٣��ڳ�ʱǰ�ȴ�����ʱ��ǿ�ƹر�����������
	 * 
	 * @param timeout
	 *            ��������<=0��ʾ��������
	 */
	public void dispose(long timeout);

	/**
	 * Ȩ����أ������û���ǰ����֯����ӳ��
	 * 
	 * @param context
	 *            ��ǰ�����ģ�����Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	public void setUserCurrentOrg(GUID orgID);

}
