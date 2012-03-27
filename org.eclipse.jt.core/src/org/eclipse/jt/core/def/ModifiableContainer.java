package org.eclipse.jt.core.def;

/**
 * 定义容器的基接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ModifiableContainer<TElement> extends Container<TElement> {
	/**
	 * 移除某位置的元素
	 * 
	 * @param index
	 *            移除的位置
	 * @return 返回被移除的元素
	 */
	public TElement remove(int index) throws IndexOutOfBoundsException;

	/**
	 * 移除某元素
	 * 
	 * @param toRemove
	 *            需要被移除的元素
	 * @return 返回是否被成功移除
	 */
	public boolean remove(Object declare);

	/**
	 * 清空
	 */
	public void clear();

	/**
	 * 移动元素到位置
	 * 
	 * @param from
	 *            从
	 * @param to
	 *            到
	 */
	public void move(int from, int to);
}
