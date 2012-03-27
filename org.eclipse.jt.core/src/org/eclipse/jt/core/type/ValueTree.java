package org.eclipse.jt.core.type;
/**
 * 值树
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueTree extends ValueList {
	/**
	 * 获取某个节点下的孩子树
	 * 
	 * @param index 位置
	 * @return 返回子树
	 */
	public ValueTree getChildren(int index);
	/**
	 * 返回迭代接口
	 * 
	 * @return 返回迭代接口
	 */
	public ValueTreeIterator newTreeIterator();
}
