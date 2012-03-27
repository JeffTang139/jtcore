package org.eclipse.jt.core.spi.application;

/**
 * 会话容器，外部要实现该接口，以提供会话的保存
 * 
 * @author Jeff Tang
 * 
 * @param <TUserData>
 */
public interface SessionIniter<TUserData> {
	/**
	 * 返回Session借口，没有则利用sessionBuilder创建并保持，Holder最后要释放Session
	 * 
	 * @param sessionBuilder
	 * @param userData
	 * @return
	 * @throws Throwable
	 */
	public void initSession(Session session, TUserData userData)
	        throws Throwable;
}
