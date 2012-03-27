package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * ���ӵĹ�ϵ����
 * 
 * @see org.eclipse.jt.core.def.query.JoinedRelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDeclare extends JoinedRelationRefDefine,
		RelationRefDeclare, MoJoinedRelationRefDeclare {

	/**
	 * ������������
	 * 
	 * @param condition
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * ������������
	 * 
	 * @param type
	 */
	public void setJoinType(TableJoinType type);
}
