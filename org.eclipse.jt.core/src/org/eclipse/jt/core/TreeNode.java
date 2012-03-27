package org.eclipse.jt.core;

import org.eclipse.jt.core.impl.TreeNodeImpl;

/**
 * ���ṹ���ݽӿ�
 * 
 * @author Jeff Tang
 * 
 * @param <E>
 *            ������ϴ�ŵ���������
 */
public interface TreeNode<E> extends ReadOnlyTreeNode<E> {
	/**
	 * �������ӿ�
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface Helper {
		/**
		 * ����һ�����ĸ��ڵ�
		 * 
		 * @param <E>
		 *            ���ڵ��Ԫ������
		 * @param e
		 *            ���ڵ��Ԫ��
		 */
		public <E> TreeNode<E> newRootNode(E e);
	}

	/**
	 * ������
	 */
	public final static Helper helper = TreeNodeImpl.helper;

	/**
	 * ����ĳ��ֱ���ӽڵ�
	 * 
	 * @param index
	 *            λ��
	 * @return �����ӽڵ�
	 */
	public TreeNode<E> getChild(int index) throws IndexOutOfBoundsException;

	/**
	 * ���ظ��ڵ�
	 * 
	 * @return ���ظ��ڵ�
	 */
	public TreeNode<E> getParent();

	/**
	 * ���ýڵ�����
	 * 
	 * @param data
	 */
	public void setElement(E data);

	/**
	 * �����ӽڵ�
	 * 
	 * @param data
	 *            ׷�ӵ��ӽڵ������
	 * @return ����׷�ӵ��ӽڵ�
	 */
	public TreeNode<E> append(E data);

	/**
	 * �Ƴ��ӽڵ�
	 * 
	 * @param index
	 *            �ӽڵ��λ��
	 * @return �����Ƴ����ӽڵ�,��λ����Чʱ����null
	 */
	public TreeNode<E> remove(int index);

	/**
	 * �Ƴ����е��ӽڵ�
	 */
	public void clear();
}
