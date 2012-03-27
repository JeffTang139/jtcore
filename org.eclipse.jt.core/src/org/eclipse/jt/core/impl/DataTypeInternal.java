package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * �ڲ����ͽӿ�
 * 
 * @author Jeff Tang
 * 
 */
interface DataTypeInternal extends DataType {
	public void setArrayOf(ArrayDataTypeBase type);

	/**
	 * ���ص�ǰ���͵���������
	 */
	public ArrayDataTypeBase arrayOf();

	public DataTypeInternal getRootType();

	/**
	 * �������ע�����͵�Java���ͣ�����null��ʾ����ע��Java����Ӱ��
	 */
	public Class<?> getRegClass();
}
