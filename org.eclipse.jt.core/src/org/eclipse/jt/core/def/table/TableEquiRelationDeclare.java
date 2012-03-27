package org.eclipse.jt.core.def.table;

/**
 * 等值表关系
 * 
 * @see org.eclipse.jt.core.def.table.TableEquiRelationDefine
 * 
 * @author Jeff Tang
 */
@Deprecated
public interface TableEquiRelationDeclare extends TableEquiRelationDefine,
		TableRelationDeclare {

	public TableFieldDeclare getSelfField();

	public TableFieldDeclare getTargetField();

	/**
	 * 设置本表的等值关系字段
	 */
	public void setSelfField(TableFieldDefine selfField);

	/**
	 * 设置目标表的等值关系字段
	 */
	public void setTargetField(TableFieldDefine targetField);
}
