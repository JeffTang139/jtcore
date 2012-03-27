package org.eclipse.jt.core.invoke;
/**
 * 三键异步查询列表的句柄
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * 得到第三个键
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
