package org.eclipse.jt.core.def.table;

/**
 * 等值表关系
 * 
 * @author Jeff Tang
 * 
 */
@Deprecated
public interface TableEquiRelationDefine extends TableRelationDefine {

	/**
	 * 等值条件中当前表的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine getSelfField();

	/**
	 * 等值条件中目标表的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine getTargetField();
}
