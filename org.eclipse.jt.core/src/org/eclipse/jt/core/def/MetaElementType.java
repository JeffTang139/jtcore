package org.eclipse.jt.core.def;

/**
 * 元数据类型
 */
public enum MetaElementType {

	/**
	 * 信息定义
	 */
	INFO,

	/**
	 * 逻辑表定义
	 */
	TABLE,

	/**
	 * 查询语句定义
	 */
	QUERY,

	/**
	 * ORM查询语句定义
	 */
	ORM,

	/**
	 * 插入语句定义
	 */
	INSERT,

	/**
	 * 删除语句定义
	 */
	DELETE,

	/**
	 * 更新语句定义
	 */
	UPDATE,

	/**
	 * 存储过程定义
	 */
	STORED_PROC,

	/**
	 * 模型
	 */
	MODEL;
}
