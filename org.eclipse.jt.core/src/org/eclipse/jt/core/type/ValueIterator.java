package org.eclipse.jt.core.type;


/**
 * 值迭代器，迭代器构建好后就处在第一个位置
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueIterator extends ReadableValue {
	/**
	 * 移动到下一位置，并返回是否有效。
	 * 
	 * @return 返回是否有效
	 */
	public boolean next();
	/**
	 * 返回当前位置是否有效
	 * 
	 * @return 返回是否有效
	 */
	public boolean valid();
}
