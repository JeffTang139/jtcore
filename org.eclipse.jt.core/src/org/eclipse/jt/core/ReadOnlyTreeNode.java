package org.eclipse.jt.core;

import java.util.Iterator;

/**
 * 只读树结点
 * 
 * @author Jeff Tang
 * 
 * @param <E>
 *            树结点上存放的数据类型
 */
public interface ReadOnlyTreeNode<E> extends Iterable<E> {
    /**
     * 返回父节点
     * 
     * @return 返回父节点,或空
     */
    public ReadOnlyTreeNode<E> getParent();

    /**
     * 获得节点数据
     * 
     * @return 返回节点数据
     */
    public E getElement();

    /**
     * 获得直接子节点的个数
     * 
     * @return 返回个数
     */
    public int getChildCount();

    /**
     * 返回某个直接子节点
     * 
     * @param index
     *            位置
     * @return 返回子节点
     */
    public ReadOnlyTreeNode<E> getChild(int index)
            throws IndexOutOfBoundsException;

    /**
     * 返回子节点的位置,-1表示不是子节点
     * 
     * @param node
     *            尝试查找位置的子节点
     * @return 返回子节点的位置,-1表示不是子节点
     */
    public int indexOf(ReadOnlyTreeNode<E> node);

    /**
     * 是否是叶子节点
     * 
     * @return 返回是否是叶子节点
     */
    boolean isLeaf();

    /**
     * 返回一个迭代以本结点为根结点的树的所有有效结点的迭代器。
     * 
     * 迭代顺序以树的深度遍历算法为依据。
     */
    Iterator<E> iterator();
}
