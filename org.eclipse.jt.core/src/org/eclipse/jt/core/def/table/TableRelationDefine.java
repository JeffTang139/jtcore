package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 表关系定义
 * 
 * @author Jeff Tang
 * 
 */
public interface TableRelationDefine extends TableReferenceDefine {

	/**
	 * 获得关系所属表定义
	 * 
	 * @return 返回表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获取关系定义的连接条件
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * 获取关系定义的类型
	 * 
	 * @see org.eclipse.jt.core.def.table.TableRelationType
	 * 
	 * @return
	 */
	public TableRelationType getRelationType();

	/**
	 * 获取当前表关系是否是等值表关系
	 * 
	 * @return
	 */
	public boolean isEquiRelation();

	/**
	 * 获取等值关系中当前表的字段定义
	 * 
	 * <p>
	 * isEquiRelation为true时可调用该方法,否则抛出异常
	 * 
	 * @return
	 */
	public TableFieldDefine getEquiRelationSelfField();

	/**
	 * 获取等值关系中目标表的字段定义
	 * 
	 * <p>
	 * isEquiRelation为true时可调用该方法,否则抛出异常
	 * 
	 * @return
	 */
	public TableFieldDefine getEquiRelationTargetField();

	/**
	 * 将当前表关系转换成等值表关系
	 * 
	 * <p>
	 * <strong>强烈建议不再使用该方法，直接使用getEquiRelationSelfField()
	 * 或getEquiRelationTargetField().</strong>
	 * 
	 * <p>
	 * isEquiRelation()返回true时可以调用该方法
	 * 
	 * @return
	 */
	@Deprecated
	public TableEquiRelationDefine castAsEquiRelation();
}
