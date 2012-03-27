package org.eclipse.jt.core;

import java.util.Comparator;

/**
 * 树结点请求器
 * 
 * @author Jeff Tang
 * 
 */
public interface TreeQuerier {
	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;
	
	// -----------------------------------------以下为权限相关---------------------------------------------------


	
	// -----------------------------------------以上为权限相关---------------------------------------------------
	
}
