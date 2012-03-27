package org.eclipse.jt.core;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * ��������������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ObjectQuerier {
	/**
	 * ����ָ�����͵Ľӿڻ����
	 * 
	 * @param <TFacade> ���������
	 * @param facadeClass ��������͵���
	 * @return ���ض����ӿ�
	 * @throws UnsupportedOperationException ������������֧����������
	 * @throws MissingObjectException ����������֧���������͵���û�з�����Ч�Ķ���
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ����������������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key ��
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ��������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key1 ��1
	 * @param key2 ��2
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException;

	/**
	 * ��������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key1 ��1
	 * @param key2 ��2
	 * @param key3 ��3
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException;

	/**
	 * ������
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param keys ���б�
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ����ָ�����͵Ľӿڻ����
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException;

	/**
	 * ��������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key ��
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException;

	/**
	 * ��������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key1 ��1
	 * @param key2 ��2
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException;

	/**
	 * ��������ӿ�
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key1 ��1
	 * @param key2 ��2
	 * @param key3 ��3
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ������
	 * 
	 * @param <TFacade> ������������ͻ�ӿ�����
	 * @param facadeClass �����������
	 * @param key1 ��1
	 * @param key2 ��2
	 * @param key3 ��3
	 * @param keys ���б�
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException;

	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
	

	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
}
