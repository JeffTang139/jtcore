package org.eclipse.jt.core.type;
/**
 * ֵ��
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueTree extends ValueList {
	/**
	 * ��ȡĳ���ڵ��µĺ�����
	 * 
	 * @param index λ��
	 * @return ��������
	 */
	public ValueTree getChildren(int index);
	/**
	 * ���ص����ӿ�
	 * 
	 * @return ���ص����ӿ�
	 */
	public ValueTreeIterator newTreeIterator();
}
