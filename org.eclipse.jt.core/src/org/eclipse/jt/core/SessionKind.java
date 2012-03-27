package org.eclipse.jt.core;

/**
 * 会话类型
 * 
 * @author Jeff Tang
 * 
 */
public enum SessionKind {
	/**
	 * 系统会话，由框架发起
	 */
	SYSTEM,
	/**
	 * 远程调用会话，由框架发起
	 */
	REMOTE,
	/**
	 * 临时会话，由框架发起，异步调用等使用
	 */
	TRANSIENT,
	/**
	 * 普通本地会话，由界面框架发起
	 */
	NORMAL
}