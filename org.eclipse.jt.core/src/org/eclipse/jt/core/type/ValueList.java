package org.eclipse.jt.core.type;
/**
 * 值列表接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ValueList extends Values {
	/**
	 * 获取跌代器接口
	 * 
	 * @return 返回跌代器接口
	 */
	public ValueIterator newIterator();
	/**
	 * 获得列表的个数
	 * 
	 * @return 返回列表个数
	 */
	public int getCount();
}
