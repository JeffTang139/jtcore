package org.eclipse.jt.core;

import java.util.Iterator;

/**
 * ֻ�������
 * 
 * @author Jeff Tang
 * 
 * @param <E>
 *            ������ϴ�ŵ���������
 */
public interface ReadOnlyTreeNode<E> extends Iterable<E> {
    /**
     * ���ظ��ڵ�
     * 
     * @return ���ظ��ڵ�,���
     */
    public ReadOnlyTreeNode<E> getParent();

    /**
     * ��ýڵ�����
     * 
     * @return ���ؽڵ�����
     */
    public E getElement();

    /**
     * ���ֱ���ӽڵ�ĸ���
     * 
     * @return ���ظ���
     */
    public int getChildCount();

    /**
     * ����ĳ��ֱ���ӽڵ�
     * 
     * @param index
     *            λ��
     * @return �����ӽڵ�
     */
    public ReadOnlyTreeNode<E> getChild(int index)
            throws IndexOutOfBoundsException;

    /**
     * �����ӽڵ��λ��,-1��ʾ�����ӽڵ�
     * 
     * @param node
     *            ���Բ���λ�õ��ӽڵ�
     * @return �����ӽڵ��λ��,-1��ʾ�����ӽڵ�
     */
    public int indexOf(ReadOnlyTreeNode<E> node);

    /**
     * �Ƿ���Ҷ�ӽڵ�
     * 
     * @return �����Ƿ���Ҷ�ӽڵ�
     */
    boolean isLeaf();

    /**
     * ����һ�������Ա����Ϊ����������������Ч���ĵ�������
     * 
     * ����˳����������ȱ����㷨Ϊ���ݡ�
     */
    Iterator<E> iterator();
}
