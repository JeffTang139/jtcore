package org.eclipse.jt.core.type;
/**
 * ֵ�б�ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueList extends Values {
	/**
	 * ��ȡ�������ӿ�
	 * 
	 * @return ���ص������ӿ�
	 */
	public ValueIterator newIterator();
	/**
	 * ����б�ĸ���
	 * 
	 * @return �����б����
	 */
	public int getCount();
}
