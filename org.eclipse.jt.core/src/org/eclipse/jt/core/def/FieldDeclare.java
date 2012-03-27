package org.eclipse.jt.core.def;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 可设置的字段基接口定义
 * 
 * @author Jeff Tang
 * 
 */
public interface FieldDeclare extends FieldDefine, NamedDeclare {
	/**
	 * 设置是否要求保持有效
	 * 
	 * @param value 是否要求保持有效
	 */
	public void setKeepValid(boolean value);

	/**
	 * 设置只读属性
	 * 
	 * @param value 是否只读
	 */
	public void setReadonly(boolean value);

	/**
	 * 设置默认值
	 * 
	 * @param exp 默认值表达式
	 */
	public void setDefault(ValueExpression exp);
}
