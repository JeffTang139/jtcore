package org.eclipse.jt.core;

import java.util.Locale;

import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.type.GUID;


/**
 * ��½���󣬹����û���Ϣ��Ȩ��
 * 
 * @author Jeff Tang
 * 
 */
public interface Login {
	/**
	 * ��õ�½��״̬
	 * 
	 * @return
	 */
	public LoginState getState();

	/**
	 * ��õ�½ID
	 * 
	 * @return
	 */
	public long getID();

	/**
	 * ��õ�½��Ӧ���û������û���֤��Ķ���ΪĿ���û�����֤ǰΪ�����û�
	 */
	public User getUser();

	/**
	 * ��ÿͻ��˻�������˵���Ϣ
	 */
	public RemoteInfo getRemoteInfo();

	/**
	 * ��ȡ�Ự�ķ�λ
	 */
	public Locale getLocale();

	/**
	 * ��ȡ�Ự����
	 */
	public SessionKind getKind();

	/**
	 * Ȩ����أ���ȡ�Ự�û���ǰӳ�����֯����ID
	 * 
	 * @return ���ػỰ�û���ǰӳ�����֯����ID
	 */
	public GUID getUserCurrentOrg();

	/**
	 * Ȩ����أ������û���ǰ����֯����ӳ��
	 * 
	 * @param context
	 *            ��ǰ�����ģ�����Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	public void setUserCurrentOrg(GUID orgID);

	/**
	 * ���ݼ��ָ����Ҽ��ֵ�ռ������������null��ʾû���κοͻ��˼�ظ�����ָ��
	 */
	public PerformanceValueCollector<?> findPerformanceValueCollector(
			PerformanceIndexDefine performanceIndexDefine);

}
