package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 查询语句选择列定义
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnDeclare extends SelectColumnDefine,
		RelationColumnDeclare {

	public SelectDeclare getOwner();

	/**
	 * 设置列定义的表达式
	 */
	public void setExpression(ValueExpression value);
}
