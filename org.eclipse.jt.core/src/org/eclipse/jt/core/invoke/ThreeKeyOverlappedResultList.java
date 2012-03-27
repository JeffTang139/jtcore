package org.eclipse.jt.core.invoke;
/**
 * �����첽��ѯ�б�ľ��
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * �õ���������
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}
