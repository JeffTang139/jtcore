package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.exp.AssignableExpression;

/**
 * 参数引用表达式
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentRefExpression extends AssignableExpression {
	/**
	 * 获得引用的参数或结构子段定义
	 * 
	 * @return 返回参数或结构子段定义
	 */
	public ArgumentDefine getArgument();
}
