package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 删除语句定义
 * 
 * @see org.eclipse.jt.core.def.query.DeleteStatementDefine
 * 
 * @author Jeff Tang
 */
public interface DeleteStatementDeclare extends DeleteStatementDefine,
		ModifyStatementDeclare, RelationJoinable {

	/**
	 * 设置删除语句条件
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);

}
