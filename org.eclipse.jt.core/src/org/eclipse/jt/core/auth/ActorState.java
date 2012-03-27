package org.eclipse.jt.core.auth;

/**
 * 访问者状态<br>
 * 目前框架只支持<code>NORMAL</code>(正常)<code>DISABLE</code>(禁用)两种状态。
 * 
 * @author Jeff Tang 2009-11
 */
public enum ActorState {

	/**
	 * 正常状态
	 */
	NORMAL,

	/**
	 * 禁用状态
	 */
	DISABLE,

	/**
	 * 无效状态
	 */
	DISPOSED

}
