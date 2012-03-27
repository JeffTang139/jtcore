package org.eclipse.jt.core.impl;

/**
 * 可等待接口<br>
 * 用于统一Context.WaitFor(...)的实现
 * 
 * @author Jeff Tang
 * 
 */
interface Waitable {
	/**
	 * 等待直到结束或超时
	 * 
	 * @param timeout
	 *            超时毫秒数，0表示无限时间
	 */
	public void waitStop(long timeout) throws InterruptedException;
}
