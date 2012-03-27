package org.eclipse.jt.core.def;

/**
 * 有名字的，可命名的方面接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Namable {
	/**
	 * 获得元素的名
	 * 
	 * @return 返回名
	 */
	public String getName();

	/**
	 * 获得元素的标题
	 * 
	 * @return 返回标题
	 */
	public String getTitle();
}
