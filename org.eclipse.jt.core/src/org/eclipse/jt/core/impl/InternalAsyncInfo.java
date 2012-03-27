package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.service.AsyncInfo.SessionMode;

/**
 * 异步处理信息
 * 
 * @author Jeff Tang
 * 
 */
public abstract class InternalAsyncInfo {
	/**
	 * 开始的时间
	 */
	protected long start;
	/**
	 * 重复执行的周期小于或等于0表示不重复
	 */
	protected long period;

	protected SessionMode sessionMode;
	/**
	 * 关心异步调用的中产生的信息
	 */
	protected boolean careInfos;

	public InternalAsyncInfo(long start, long period, SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		this.start = start;
		this.period = period;
		this.sessionMode = sessionMode;
	}
}
