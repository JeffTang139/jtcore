package org.eclipse.jt.core.type;

/**
 * Ԫ������
 * 
 * @author Jeff Tang
 * 
 */
public interface TupleType extends Type {
	/**
	 * ��ȡԪ���Ԫ�ظ���
	 * 
	 * @return ����Ԫ�ظ���
	 */
	public int getTupleElementCount();

	/**
	 * ��ȡĳ��Ԫ�������
	 * 
	 * @param index λ��
	 * @return ����Ԫ�������
	 */
	public Typable getTupleElementType(int index);
}
