package org.eclipse.jt.core.type;


/**
 * ֵ�������������������ú�ʹ��ڵ�һ��λ��
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueIterator extends ReadableValue {
	/**
	 * �ƶ�����һλ�ã��������Ƿ���Ч��
	 * 
	 * @return �����Ƿ���Ч
	 */
	public boolean next();
	/**
	 * ���ص�ǰλ���Ƿ���Ч
	 * 
	 * @return �����Ƿ���Ч
	 */
	public boolean valid();
}
