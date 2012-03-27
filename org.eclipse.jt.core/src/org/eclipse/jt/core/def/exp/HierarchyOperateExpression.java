package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QuRelationRefDefine;

/**
 * 级次函数表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyOperateExpression extends ValueExpression {

	/**
	 * 获取级次运算符
	 */
	public HierarchyOperator getOperator();

	/**
	 * 表引用
	 */
	public QuRelationRefDefine getSource();

	/**
	 * 级次值参数
	 */
	public ValueExpression getLevel();
}
