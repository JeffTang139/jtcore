package org.eclipse.jt.core.impl;

/**
 * 列类型对逻辑表字段类型存储的兼容性
 * 
 * @author Jeff Tang
 * 
 */
enum TypeCompatiblity {

	/**
	 * 可以兼容存储,不存在空间浪费
	 */
	Exactly,

	/**
	 * 可以兼容存储,但存在空间浪费
	 */
	Overflow,

	/**
	 * 可以兼容存储,但建议修改
	 */
	NotSuggest,

	/**
	 * 不能兼容存储
	 */
	Unable
}
