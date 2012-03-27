package org.eclipse.jt.core.spi.model;

/**
 * 模型调用所在位置
 * 
 * @author Jeff Tang
 * 
 */
public enum ModelCallScope {
	/**
	 * 在属性设置器中
	 */
	IMPL_SETTER,
	/**
	 * 在属性获取器中
	 */
	IMPL_GETTER,
	/**
	 * 在动作中
	 */
	IMPL_ACTION,
	/**
	 * 在约束中
	 */
	IMPL_CONSTRAINT,
	/**
	 * 在构造方法中
	 */
	IMPL_CONSTRUCTOR,
	/**
	 * 在模型实体源中
	 */
	IMPL_SOURCE,
	/**
	 * 外部调用
	 */
	OUTER,
}
