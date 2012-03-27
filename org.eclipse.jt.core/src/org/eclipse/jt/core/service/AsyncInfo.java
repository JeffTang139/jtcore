package org.eclipse.jt.core.service;

import java.util.Date;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.InternalAsyncInfo;


/**
 * 异步调用信息
 * 
 * @author Jeff Tang
 * 
 */
public final class AsyncInfo extends InternalAsyncInfo {
	/**
	 * 会话模式
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum SessionMode {
		/**
		 * 相同的会话中，
		 */
		SAME,
		/**
		 * 不同的会话，相同的用户，对于系统会话下发起的异步处理则会使用匿名用户
		 */
		INDIVIDUAL,
		/**
		 * 不同的会话，匿名用户
		 */
		INDIVIDUAL_ANONYMOUS
	}

	/**
	 * 获取绘画模式
	 */
	public final SessionMode getSessionMode() {
		return super.sessionMode;
	}

	/**
	 * 设置会话模式
	 */
	public final void setSessionMode(SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		super.sessionMode = sessionMode;
	}

	/**
	 * 设置是否关心异步调用的中产生的信息
	 */
	public final void setCareInfos(boolean value) {
		super.careInfos = value;
	}

	/**
	 * 获得是否关心异步调用的中产生的信息
	 */
	public final boolean isCareInfos() {
		return this.careInfos;
	}

	/**
	 * 开始执行的时间，小于当前时间表示立即执行
	 */
	public final long getStartTime() {
		return super.start;
	}

	/**
	 * 设置开始执行的时间，小于当前时间表示立即执行
	 */
	public final void setStartTime(long value) {
		super.start = value;
	}

	/**
	 * 获取执行周期，小于等于0表示不周期执行
	 */
	public final long getPeriod() {
		return super.period;
	}

	/**
	 * 设置执行周期，小于等于0表示不周期执行
	 */
	public final void setPeiod(long value) {
		this.period = value;
	}

	/**
	 * 构造方法
	 * 
	 * @param delay
	 *            延迟开始时间
	 * @param period
	 *            执行周期，小于等于0表示不周期执行
	 */
	public AsyncInfo(long delay, long period) {
		super(System.currentTimeMillis() + delay, period, SessionMode.SAME);
	}

	public AsyncInfo(long delay, long period, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, period, sessionMode);
	}

	/**
	 * 构造方法，无周期
	 * 
	 * @param delay
	 *            延迟开始时间
	 */
	public AsyncInfo(long delay) {
		super(System.currentTimeMillis() + delay, 0, SessionMode.SAME);
	}

	public AsyncInfo(long delay, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, 0, sessionMode);
	}

	/**
	 * 构造方法，无周期
	 * 
	 * @param start
	 *            开始时间，小于当前时间表示立即执行
	 * @param period
	 *            执行周期，小于等于0表示不周期执行
	 */
	public AsyncInfo(Date start, long period) {
		super(start.getTime(), period, SessionMode.SAME);
	}

	public AsyncInfo(Date start, long period, SessionMode sessionMode) {
		super(start.getTime(), period, sessionMode);
	}

	/**
	 * 构造方法
	 * 
	 * @param start
	 *            开始时间，小于当前时间表示立即执行
	 */
	public AsyncInfo(Date start) {
		super(start.getTime(), 0, SessionMode.SAME);
	}

	public AsyncInfo(Date start, SessionMode sessionMode) {
		super(start.getTime(), 0, sessionMode);
	}

	public AsyncInfo() {
		super(0, 0, SessionMode.SAME);
	}

	public AsyncInfo(SessionMode sessionMode) {
		super(0, 0, sessionMode);
	}
}
