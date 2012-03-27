package org.eclipse.jt.core.invoke;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 异步查询的句柄
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 *            结果类型
 */
public interface AsyncResult<TResult> extends AsyncHandle {
	/**
	 * 获得执行完后的结果
	 * 
	 * @return 返回结果
	 * @throws IllegalStateException
	 *             如果结果还未返回，则抛出该异常
	 * @throws MissingObjectException
	 *             如果没有返回结果（null）则抛出该异常
	 */
	public TResult getResult() throws IllegalStateException,
	        MissingObjectException;

	/**
	 * 返回结果是否为空
	 * 
	 * @return 返回结果是否为空
	 * @throws IllegalStateException
	 *             如果结果还未返回，则抛出该异常
	 */
	public boolean isNull() throws IllegalStateException;

	/**
	 * 获得请求结果的类
	 * 
	 * @return 返回请求结果的类
	 */
	public Class<TResult> getResultClass();
}
