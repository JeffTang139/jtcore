package org.eclipse.jt.core.invoke;

import java.util.List;

/**
 * �첽��ѯ�б�ľ��
 * 
 * @author Jeff Tang
 * 
 * @param <TResult>
 */
public interface AsyncResultList<TResult> extends AsyncHandle {
	/**
	 * ���ִ�����Ľ���б�
	 * 
	 * @return ���ؽ���б�
	 * @throws IllegalStateException
	 *             ��������δ���أ����׳����쳣
	 */
	public List<TResult> getResultList() throws IllegalStateException;

	/**
	 * �������������
	 * 
	 * @return ��������������
	 */
	public Class<TResult> getResultClass();
}
