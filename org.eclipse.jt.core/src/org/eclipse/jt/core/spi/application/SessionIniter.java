package org.eclipse.jt.core.spi.application;

/**
 * �Ự�������ⲿҪʵ�ָýӿڣ����ṩ�Ự�ı���
 * 
 * @author Jeff Tang
 * 
 * @param <TUserData>
 */
public interface SessionIniter<TUserData> {
	/**
	 * ����Session��ڣ�û��������sessionBuilder���������֣�Holder���Ҫ�ͷ�Session
	 * 
	 * @param sessionBuilder
	 * @param userData
	 * @return
	 * @throws Throwable
	 */
	public void initSession(Session session, TUserData userData)
	        throws Throwable;
}
