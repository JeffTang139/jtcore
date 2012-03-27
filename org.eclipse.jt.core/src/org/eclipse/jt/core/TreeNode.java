package org.eclipse.jt.core;

import org.eclipse.jt.core.impl.TreeNodeImpl;

/**
 * 树结构数据接口
 * 
 * @author Jeff Tang
 * 
 * @param <E>
 *            树结点上存放的数据类型
 */
public interface TreeNode<E> extends ReadOnlyTreeNode<E> {
	/**
	 * 帮助器接口
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface Helper {
		/**
		 * 创建一个树的根节点
		 * 
		 * @param <E>
		 *            根节点的元素类型
		 * @param e
		 *            根节点的元素
		 */
		public <E> TreeNode<E> newRootNode(E e);
	}

	/**
	 * 帮助器
	 */
	public final static Helper helper = TreeNodeImpl.helper;

	/**
	 * 返回某个直接子节点
	 * 
	 * @param index
	 *            位置
	 * @return 返回子节点
	 */
	public TreeNode<E> getChild(int index) throws IndexOutOfBoundsException;

	/**
	 * 返回父节点
	 * 
	 * @return 返回父节点
	 */
	public TreeNode<E> getParent();

	/**
	 * 设置节点数据
	 * 
	 * @param data
	 */
	public void setElement(E data);

	/**
	 * 增加子节点
	 * 
	 * @param data
	 *            追加的子节点的数据
	 * @return 返回追加的子节点
	 */
	public TreeNode<E> append(E data);

	/**
	 * 移除子节点
	 * 
	 * @param index
	 *            子节点的位置
	 * @return 返回移除的子节点,或当位置无效时返回null
	 */
	public TreeNode<E> remove(int index);

	/**
	 * 移除所有的子节点
	 */
	public void clear();
}
