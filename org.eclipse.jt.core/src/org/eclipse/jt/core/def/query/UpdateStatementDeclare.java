package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 更新语句定义
 * 
 * @see org.eclipse.jt.core.def.query.UpdateStatementDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface UpdateStatementDeclare extends UpdateStatementDefine,
		ModifyStatementDeclare, FieldValueAssignable, RelationJoinable {

	/**
	 * 设置更新条件
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);

}
