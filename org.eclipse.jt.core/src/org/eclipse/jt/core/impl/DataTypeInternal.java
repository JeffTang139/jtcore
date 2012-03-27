package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

/**
 * 内部类型接口
 * 
 * @author Jeff Tang
 * 
 */
interface DataTypeInternal extends DataType {
	public void setArrayOf(ArrayDataTypeBase type);

	/**
	 * 返回当前类型的数组类型
	 */
	public ArrayDataTypeBase arrayOf();

	public DataTypeInternal getRootType();

	/**
	 * 获得用于注册类型的Java类型，返回null表示不用注则Java类型影射
	 */
	public Class<?> getRegClass();
}
