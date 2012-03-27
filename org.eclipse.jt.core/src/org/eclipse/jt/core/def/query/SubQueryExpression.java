package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 子查询表达式
 * 
 * @author Jeff Tang
 */
public interface SubQueryExpression extends ValueExpression {

	/**
	 * 获得子查询定义
	 * 
	 * @return 返回子查询定义
	 */
	public SubQueryDefine getSubQuery();
}
