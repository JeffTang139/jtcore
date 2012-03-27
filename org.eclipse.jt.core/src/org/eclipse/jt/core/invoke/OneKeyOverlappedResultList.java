package org.eclipse.jt.core.invoke;
/**
 * 单键异步查询列表的句柄
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface OneKeyOverlappedResultList<TResult, TKey> extends AsyncResultList<TResult> {
	/**
	 * 得到第一个键
	 * @return 返回第一个键
	 */
	public TKey getKey1();
}
