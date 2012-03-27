package org.eclipse.jt.core.def.model;
/**
 * 模型调用的有效性
 * @author Jeff Tang
 *
 */
public enum ModelInvokeValidity {
	/**
	 * 不可读写
	 */
	NONE,
	/**
	 * 可访问，对应属性的读
	 */
	R,
	/**
	 * 可调用，对应属性的写和动作调用
	 */
	W,
	/**
	 * 可访问和调用，对应属性的读写写和动作的调用
	 */
	RW
}
