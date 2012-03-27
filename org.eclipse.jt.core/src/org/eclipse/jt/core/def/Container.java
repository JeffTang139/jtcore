package org.eclipse.jt.core.def;

/**
 * 定义容器的基接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Container<TElement> extends Iterable<TElement> {
	/**
	 * 获得定义的个数
	 * 
	 * @return 返回定义的个数
	 */
	public int size();

	/**
	 * 判断是否为空
	 */
	public boolean isEmpty();

	/**
	 * 获得第index个定义
	 * 
	 * @param index
	 *            位置
	 * @return 返回定义
	 * @throws IndexOutOfBoundsException
	 *             位置不合法
	 */
	public TElement get(int index) throws IndexOutOfBoundsException;

	/**
	 * 查找元素所在位置
	 * 
	 * @param define
	 *            要查找的元素
	 * @return 返回找到的位置或者-1
	 */
	public int indexOf(Object define);
}
