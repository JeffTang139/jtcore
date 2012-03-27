package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 更新语句定义
 * 
 * @author Jeff Tang
 */
public interface UpdateStatementDefine extends ModifyStatementDefine {

	/**
	 * 获取更新语句的条件
	 * 
	 * @return
	 */
	ConditionalExpression getCondition();
}
