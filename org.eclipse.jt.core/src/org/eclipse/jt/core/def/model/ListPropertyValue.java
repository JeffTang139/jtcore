package org.eclipse.jt.core.def.model;

/**
 * �б�����
 * 
 * @author Jeff Tang
 * 
 */
public interface ListPropertyValue extends Iterable<Object> {
	/**
	 * �б��С
	 */
	public int size();

	/**
	 * ��ȡĳԪ��
	 * 
	 * @param index
	 *            λ��
	 */
	public Object get(int index);
}
