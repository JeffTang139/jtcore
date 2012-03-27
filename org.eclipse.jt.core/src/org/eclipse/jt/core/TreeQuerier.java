package org.eclipse.jt.core;

import java.util.Comparator;

/**
 * �����������
 * 
 * @author Jeff Tang
 * 
 */
public interface TreeQuerier {
	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facade
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
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
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
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
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
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ��������ӿ������
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
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;
	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------


	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
}
