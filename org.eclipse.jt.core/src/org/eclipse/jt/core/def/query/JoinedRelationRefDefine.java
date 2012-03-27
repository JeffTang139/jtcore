package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.table.TableJoinType;

/**
 * 连接的关系引用
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings("deprecation")
public interface JoinedRelationRefDefine extends RelationRefDefine,
		MoJoinedRelationRefDefine {

	/**
	 * 获取连接条件
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * 获取连接类型
	 * 
	 * @return
	 */
	public TableJoinType getJoinType();
}
