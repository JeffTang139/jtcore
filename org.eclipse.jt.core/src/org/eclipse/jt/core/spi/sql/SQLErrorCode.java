package org.eclipse.jt.core.spi.sql;

/**
 * 错误代码
 * 
 * @author Jeff Tang
 * 
 */
public enum SQLErrorCode {
	/**
	 * 语法错误
	 */
	SYNTAX,
	/**
	 * IO错误
	 */
	IO_ERROR,
	/**
	 * 不支持
	 */
	NOT_SUPPORT,
	/**
	 * 符号未定义
	 */
	TOKEN_UNDEFINED,
	/**
	 * 字面量格式不正确
	 */
	VALUE_FORMAT,
	/**
	 * 操作数类型不正确
	 */
	OPERAND_TYPE_INVALID,
	/**
	 * 列数不正确
	 */
	COLUMN_COUNT,
	/**
	 * 函数未定义
	 */
	FUNC_UNDEFINED,
	/**
	 * 符号未找到
	 */
	TOKEN_NOT_FOUND,
	/**
	 * 缺少表定义
	 */
	TABLE_NOT_FOUND,
	/**
	 * 缺少列定义
	 */
	COLUMN_NOT_FOUND,
	/**
	 * 变量名重复
	 */
	VAR_DUPLICATE,
	/**
	 * 变量未定义
	 */
	VAR_UNDEFINED,
	/**
	 * 别名重复
	 */
	ALIAS_DUPLICATE,
	/**
	 * 别名未定义
	 */
	ALIAS_UNDEFINED,
	/**
	 * 类型未找到
	 */
	CLASS_NOT_FOUND,
	/**
	 * 表关系未找到
	 */
	RELATION_NOT_FOUND,
	/**
	 * 级次未找到
	 */
	HIERARCHY_NOT_FOUND,
	/**
	 * ORM未找到
	 */
	ORM_NOT_FOUND,
	/**
	 * 名称重复
	 */
	NAMED_REDEFINED
}
