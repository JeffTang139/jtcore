package org.eclipse.jt.core.invoke;
/**
 * ˫���첽��ѯ�ľ��
 * @author Jeff Tang
 *
 * @param <TResult>
 * @param <TKey>
 */
public interface TwoKeyOverlappedResult<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResult<TResult, TKey1> {
	/**
	 * ��øò�ѯ�ĵڶ�����
	 * 
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}
