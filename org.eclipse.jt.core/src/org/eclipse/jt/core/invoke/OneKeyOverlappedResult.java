package org.eclipse.jt.core.invoke;
/**
 * 单键异步查询的句柄
 * @author Jeff Tang
 * 
 * @param <TResult>
 * @param <TKey>
 */
public interface OneKeyOverlappedResult<TResult, TKey> extends AsyncResult<TResult> {
	/**
	 * 获得该查询的第一个键
	 * 
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
