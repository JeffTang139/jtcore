package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * 连接的关系引用
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
	 * 设置连接条件
	 * 
	 * @param condition
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * 设置连接类型
	 * 
	 * @param type
	 */
	public void setJoinType(TableJoinType type);
}
