package org.eclipse.jt.core.auth;

/**
 * 操作定义接口
 * 
 * @param <TFacade>
 *            操作对应的资源外观类
 * @author Jeff Tang 2009-11
 */
public interface Operation<TFacade> {

	/**
	 * 获取操作标题<br>
	 * 一般用于显示。
	 * 
	 * @return 返回操作标题
	 */
	public String getTitle();

	/**
	 * 获取操作代码掩码<br>
	 * 授权代码的索引掩码，只有低16位有效，<br>
	 * 也就是说最多定义16种独立的操作（混合操作不算）。
	 * 
	 * <pre>
	 * index 0 : 1
	 * index 1 : 1 &lt;&lt; 1
	 * index 2 : 1 &lt;&lt; 2
	 * ...
	 * (index 1 | index 2) : (1 &lt;&lt; 1) &amp; (1&lt;&lt;2)
	 * </pre>
	 * 
	 * @return 返回操作代码掩码
	 */
	public int getMask();

}
