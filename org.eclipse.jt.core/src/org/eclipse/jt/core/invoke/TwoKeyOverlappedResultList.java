package org.eclipse.jt.core.invoke;
/**
 * ˫���첽��ѯ�б�ľ��
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface TwoKeyOverlappedResultList<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResultList<TResult, TKey1> {
	/**
	 * �õ��ڶ�����
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}
