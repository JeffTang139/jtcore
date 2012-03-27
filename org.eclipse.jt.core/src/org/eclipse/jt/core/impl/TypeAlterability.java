package org.eclipse.jt.core.impl;

/**
 * 列类型的可修改性
 * 
 * <p>
 * 仅考虑可修改性本身,不考虑列被索引,存在约束,转换导致精度丢失等情景.
 * 
 * @author Jeff Tang
 * 
 */
enum TypeAlterability {

	/**
	 * 总是支持类型修改
	 */
	Always,

	/**
	 * 所有记录的该列值为空才能修改类型,也即意味着表为空亦可.
	 */
	ColumnNull,

	/**
	 * 新长度或精度超过所有已经存在的值
	 */
	ExceedExist,

	/**
	 * 在任何条件下都不允许修改,即使表为空
	 */
	Never
}
