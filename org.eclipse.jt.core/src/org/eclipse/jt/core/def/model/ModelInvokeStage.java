package org.eclipse.jt.core.def.model;

/**
 * 模型的调用阶段
 * 
 * @author Jeff Tang
 * 
 */
public enum ModelInvokeStage {
	/**
	 * 某调用前
	 */
	BEFORE,
	/**
	 * 当前调用，即不是由其他调用触发的
	 */
	DOING,
	/**
	 * 某调用后，调用中出现异常则忽略
	 */
	AFTER,
	/**
	 * 改变后，只针对属性有效
	 */
	CHANGED,
	/**
	 * 最终，即使异常也处理
	 */
	FINALLY,
}
