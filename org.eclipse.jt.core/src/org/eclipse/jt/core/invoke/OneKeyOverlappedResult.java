package org.eclipse.jt.core.invoke;
/**
 * �����첽��ѯ�ľ��
 * @author Jeff Tang
 * 
 * @param <TResult>
 * @param <TKey>
 */
public interface OneKeyOverlappedResult<TResult, TKey> extends AsyncResult<TResult> {
	/**
	 * ��øò�ѯ�ĵ�һ����
	 * 
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}
