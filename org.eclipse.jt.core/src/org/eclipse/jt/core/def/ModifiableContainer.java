package org.eclipse.jt.core.def;

/**
 * ���������Ļ��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModifiableContainer<TElement> extends Container<TElement> {
	/**
	 * �Ƴ�ĳλ�õ�Ԫ��
	 * 
	 * @param index
	 *            �Ƴ���λ��
	 * @return ���ر��Ƴ���Ԫ��
	 */
	public TElement remove(int index) throws IndexOutOfBoundsException;

	/**
	 * �Ƴ�ĳԪ��
	 * 
	 * @param toRemove
	 *            ��Ҫ���Ƴ���Ԫ��
	 * @return �����Ƿ񱻳ɹ��Ƴ�
	 */
	public boolean remove(Object declare);

	/**
	 * ���
	 */
	public void clear();

	/**
	 * �ƶ�Ԫ�ص�λ��
	 * 
	 * @param from
	 *            ��
	 * @param to
	 *            ��
	 */
	public void move(int from, int to);
}
