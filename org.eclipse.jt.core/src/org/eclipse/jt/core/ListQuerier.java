package org.eclipse.jt.core;

import java.util.Comparator;
import java.util.List;

/**
 * �б�������
 * 
 * @author Jeff Tang
 * 
 */
public interface ListQuerier {
	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys);

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys);

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);

	/**
	 * ��������ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);
	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------

	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
}
