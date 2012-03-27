package org.eclipse.jt.core.type;

/**
 * 对象类型
 * 
 * @author Jeff Tang
 * 
 */
public interface ObjectDataType extends DataType {

	public Class<?> getJavaClass();

	/**
	 * 是否是该枚举类型的实例
	 */
	public boolean isInstance(Object obj);

}
