package org.eclipse.jt.core.invoke;
/**
 * �����첽��ѯ�б�ľ��
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface OneKeyOverlappedResultList<TResult, TKey> extends AsyncResultList<TResult> {
	/**
	 * �õ���һ����
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}
