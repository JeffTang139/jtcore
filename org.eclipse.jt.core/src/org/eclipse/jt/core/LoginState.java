package org.eclipse.jt.core;

/**
 * 登陆状态
 * 
 * @author Jeff Tang
 * 
 */
public enum LoginState {
	/**
	 * 匿名状态，没有用户信息，用户相关的操作禁止
	 */
	ANONYNOUS,
	/**
	 * 成功登陆后的状态，可以进行一系列用户相关的操作
	 */
	LOGIN,
	/**
	 * 挂起状态，主动挂起和会话超时或中断后的挂起，用户相关的操作禁止
	 */
	// SUSPENDED,
	/**
	 * 销毁中状态
	 */
	DISPOSING,
	/**
	 * 销毁状态
	 */
	DISPOSED
}
