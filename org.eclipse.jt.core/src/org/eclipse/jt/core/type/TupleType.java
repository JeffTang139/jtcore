package org.eclipse.jt.core.type;

/**
 * 元组类型
 * 
 * @author Jeff Tang
 * 
 */
public interface TupleType extends Type {
	/**
	 * 获取元组的元素个数
	 * 
	 * @return 返回元素个数
	 */
	public int getTupleElementCount();

	/**
	 * 获取某个元组的类型
	 * 
	 * @param index 位置
	 * @return 返回元组的类型
	 */
	public Typable getTupleElementType(int index);
}
