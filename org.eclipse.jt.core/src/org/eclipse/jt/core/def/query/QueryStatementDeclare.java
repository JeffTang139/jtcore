package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * 查询语句定义
 * 
 * <p>
 * 定义一个可执行的查询语句结构
 * 
 * @see org.eclipse.jt.core.def.query.QueryStatementDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryStatementDeclare extends QueryStatementDefine,
		SelectDeclare, StatementDeclare, WithableDeclare {

	/**
	 * 使用样本查询结构,构造当前查询语句的导出查询定义
	 * 
	 * @param sample
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery(SelectDefine sample);

	public ModifiableNamedElementContainer<? extends QueryColumnDeclare> getColumns();

	public QueryColumnDeclare newColumn(RelationColumnDefine field);

	public QueryColumnDeclare newColumn(RelationColumnDefine field, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expr, String alias);

	/**
	 * 获取查询语句的排序规则
	 * 
	 * @return 返回排序项列表
	 */
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param field
	 *            排序依据的关系列
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param field
	 *            排序依据的关系列
	 * @param isDesc
	 *            是否降序
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param value
	 *            排序依据的表达式
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param value
	 *            排序依据的表达式
	 * @param isDesc
	 *            是否降序
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);

}
