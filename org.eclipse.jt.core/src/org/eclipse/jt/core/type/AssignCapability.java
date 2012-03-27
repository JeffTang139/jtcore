package org.eclipse.jt.core.type;

/**
 * 赋值和转换能力
 * 
 * @author Jeff Tang
 * 
 */
public enum AssignCapability {
	/**
	 * 相同
	 */
	SAME,
	/**
	 * 隐式转换
	 */
	IMPLICIT,
	/**
	 * 显示转换
	 */
	EXPLICIT,
	/**
	 * 需要类型转换
	 */
	CONVERT,
	/**
	 * 不支持赋值
	 */
	NO,
}
