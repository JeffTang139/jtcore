package org.eclipse.jt.core.type;

/**
 * ��������
 * 
 * @author Jeff Tang
 * 
 */
public interface ObjectDataType extends DataType {

	public Class<?> getJavaClass();

	/**
	 * �Ƿ��Ǹ�ö�����͵�ʵ��
	 */
	public boolean isInstance(Object obj);

}
