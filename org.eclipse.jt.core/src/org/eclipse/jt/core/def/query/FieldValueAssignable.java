package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.DataType;

public interface FieldValueAssignable {

	/**
	 * 设置字段的常量值
	 * 
	 * @param field
	 *            字段定义
	 * @param value
	 *            常量值
	 */
	public void assignConst(TableFieldDefine field, Object value);

	/**
	 * 设置字段的参数值
	 * 
	 * @param field
	 * @param argument
	 */
	public void assignArgument(TableFieldDefine field, ArgumentDefine argument);

	/**
	 * 以指定参数名及类型构造参数定义,并设置字段的值为该参数,返回参数定义
	 * 
	 * @param field
	 *            插入的表字段
	 * @param name
	 *            参数名
	 * @param type
	 *            参数类型
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field, String name,
			DataType type);

	/**
	 * 根据表字段构造参数定义,并设置字段的值为该参数,返回参数定义
	 * 
	 * @param field
	 *            插入的表字段
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field);

	/**
	 * 设置字段的表达式值
	 * 
	 * @param field
	 * @param value
	 */
	public void assignExpression(TableFieldDefine field, ValueExpression value);
}
