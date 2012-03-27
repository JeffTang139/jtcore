package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.Context;

public interface ContextSPI extends Context {
	/**
	 * 提交或回滚事务,释放数据库资源，内存事务资源
	 * 
	 * @return 返回之前的异常对象
	 */
	public Throwable resolveTrans();

	/**
	 * 指定异常
	 */
	public void exception(Throwable exception);

	/**
	 * 强制释放
	 */
	public void dispose();

	/**
	 * 更新上下文所在空间
	 */
	public void updateSpace(String spacePath, char spaceSeparator);
}
