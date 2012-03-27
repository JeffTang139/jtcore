package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;

/**
 * 更新语句使用的关系引用定义
 * 
 * @deprecated 使用RelationRefDeclare
 * 
 */
@Deprecated
public interface MoRelationRefDeclare extends MoRelationRefDefine,
		RelationJoinable {

	public RelationDeclare getTarget();

	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	public RelationColumnRefExpr expOf(String columnName);

}
