package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 删除语句定义
 * 
 * @author Jeff Tang
 * 
 */
public interface DeleteStatementDefine extends ModifyStatementDefine {

	/**
	 * 获取删除语句的条件
	 * 
	 * @return
	 */
	public ConditionalExpression getCondition();
}
