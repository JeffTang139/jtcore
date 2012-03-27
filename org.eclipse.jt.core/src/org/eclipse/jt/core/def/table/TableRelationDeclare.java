package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 可设置的表关系定义
 * 
 * @author Jeff Tang
 * 
 */
public interface TableRelationDeclare extends TableRelationDefine,
		TableReferenceDeclare {

	/**
	 * 获得关系所属表定义
	 * 
	 * @return 返回表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 设置连接条件
	 */
	public void setJoinCondition(ConditionalExpression condition);

	/**
	 * 设置关系类型
	 */
	public void setRelationType(TableRelationType type);

	public TableFieldDeclare getEquiRelationSelfField();

	public TableFieldDeclare getEquiRelationTargetField();

	@Deprecated
	public TableEquiRelationDeclare castAsEquiRelation();
}
