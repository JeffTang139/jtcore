package org.eclipse.jt.core;

/**
 * 远程登录的生命周期（相对于本地相关对象而言）
 * 
 * @author Jeff Tang
 * 
 */
public enum RemoteLoginLife {
	/**
	 * 与当前事务同一生命周期，这是默认选项
	 */
	TRANS,
	/**
	 * 与当前登陆同一生命周期
	 */
	LOGIN,
	/**
	 * 与当前会话同一生命周期
	 */
	SESSION,
	/**
	 * 生命仅限于每次调用中
	 */
	INVOKE
}
