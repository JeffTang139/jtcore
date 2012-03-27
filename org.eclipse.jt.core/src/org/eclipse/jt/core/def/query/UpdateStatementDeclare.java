package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * ������䶨��
 * 
 * @see org.eclipse.jt.core.def.query.UpdateStatementDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface UpdateStatementDeclare extends UpdateStatementDefine,
		ModifyStatementDeclare, FieldValueAssignable, RelationJoinable {

	/**
	 * ���ø�������
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);

}
