package org.eclipse.jt.core;

/**
 * ����ڵ�
 * 
 * @author Jeff Tang
 * 
 */
public interface LinkNode<E> {
	/**
	 * ������ڵ��Ԫ��
	 */
	public E getElement();

	/**
	 * ������һ���ڵ�
	 */
	public LinkNode<E> nextNode();
}
