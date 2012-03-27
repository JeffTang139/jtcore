package org.eclipse.jt.core.invoke;
/**
 * 双键异步查询的句柄
 * @author Jeff Tang
 * 
 * @param <TResult>
 * @param <TKey>
 */
public interface ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * 获得该查询的第三个键
	 * 
	 * @return 返回第三个键
	 */
	public TKey3 getKey3();
}
