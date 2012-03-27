package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 查询分组规则定义
 * 
 * @see org.eclipse.jt.core.def.query.GroupByItemDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface GroupByItemDeclare extends GroupByItemDefine {

	/**
	 * 设置分组规则的表达式
	 * 
	 * @param expression
	 */
	public void setExpression(ValueExpression expression);
}
