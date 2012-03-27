package org.eclipse.jt.core.def.model;

/**
 * 列表属性
 * 
 * @author Jeff Tang
 * 
 */
public interface ListPropertyValue extends Iterable<Object> {
	/**
	 * 列表大小
	 */
	public int size();

	/**
	 * 获取某元素
	 * 
	 * @param index
	 *            位置
	 */
	public Object get(int index);
}
