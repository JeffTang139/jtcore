package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * ɾ����䶨��
 * 
 * @see org.eclipse.jt.core.def.query.DeleteStatementDefine
 * 
 * @author Jeff Tang
 */
public interface DeleteStatementDeclare extends DeleteStatementDefine,
		ModifyStatementDeclare, RelationJoinable {

	/**
	 * ����ɾ���������
	 * 
	 * @param condition
	 */
	public void setCondition(ConditionalExpression condition);

}
