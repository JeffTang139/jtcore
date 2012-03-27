package org.eclipse.jt.core;

/**
 * 上下文类型
 * 
 * @author Jeff Tang
 * 
 */
public enum ContextKind {
	/**
	 * 系统的初始化上下文，由框架发起
	 */
	INITER,
	/**
	 * 情景上下文，由框架外发起
	 */
	SITUATION,
	/**
	 * 一般上下文，由框架外发起
	 */
	NORMAL,
	/**
	 * 临时上下文，为异步调用和远程调用准备，由框架发起
	 */
	TRANSIENT,
	/**
	 * 会话销毁上下文，由框架发起
	 */
	DISPOSER;
	/**
	 * 抛出无效类别异常
	 */
	public static final void throwIllegalContextKind(ContextKind kind) {
		throw new IllegalStateException("无效的上下文类别" + kind);
	}
}