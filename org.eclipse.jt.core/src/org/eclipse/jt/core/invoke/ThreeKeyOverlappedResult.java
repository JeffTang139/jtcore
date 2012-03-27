package org.eclipse.jt.core.invoke;
/**
 * ˫���첽��ѯ�ľ��
 * @author Jeff Tang
 * 
 * @param <TResult>
 * @param <TKey>
 */
public interface ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * ��øò�ѯ�ĵ�������
	 * 
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}
