package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;

/**
 * 关系引用
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
@SuppressWarnings({ "unused", "deprecation" })
public interface RelationRefDeclare extends RelationRefDefine,
		RelationJoinable, HierarchyOperatable, NamedDeclare,
		MoRelationRefDeclare {

	public RelationDeclare getTarget();

	/**
	 * 构造关系列引用表达式
	 * 
	 * @param column
	 *            关系列定义
	 * @return
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * 构造关系列引用表达式
	 * 
	 * @param columnName
	 *            关系列名称
	 * @return
	 */
	public RelationColumnRefExpr expOf(String columnName);

}
