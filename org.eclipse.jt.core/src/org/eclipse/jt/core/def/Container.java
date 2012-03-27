package org.eclipse.jt.core.def;

/**
 * ���������Ļ��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface Container<TElement> extends Iterable<TElement> {
	/**
	 * ��ö���ĸ���
	 * 
	 * @return ���ض���ĸ���
	 */
	public int size();

	/**
	 * �ж��Ƿ�Ϊ��
	 */
	public boolean isEmpty();

	/**
	 * ��õ�index������
	 * 
	 * @param index
	 *            λ��
	 * @return ���ض���
	 * @throws IndexOutOfBoundsException
	 *             λ�ò��Ϸ�
	 */
	public TElement get(int index) throws IndexOutOfBoundsException;

	/**
	 * ����Ԫ������λ��
	 * 
	 * @param define
	 *            Ҫ���ҵ�Ԫ��
	 * @return �����ҵ���λ�û���-1
	 */
	public int indexOf(Object define);
}
