package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * 查询结构定义
 * 
 * <p>
 * 即一个select语句的结构.
 * 
 * <p>
 * 不能定义orderby子句
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectDefine extends RelationDefine, RelationRefDomainDefine {

	/**
	 * 根据名称查找当前查询结构定义的关系引用
	 * 
	 * <p>
	 * 不存在则返回null
	 * 
	 * @param name
	 *            关系引用名称
	 * @return
	 * @deprecated 使用findRelationRef
	 */
	@Deprecated
	public QuRelationRefDefine findReference(String name);

	public QuRelationRefDefine findRelationRef(String name);

	/**
	 * 根据名称获取当前查询结构定义的关系引用
	 * 
	 * <p>
	 * 不存在则抛出异常
	 * 
	 * @param name
	 *            关系引用名称
	 * @return
	 * @deprecated 使用getRelationRef
	 */
	@Deprecated
	public QuRelationRefDefine getReference(String name);

	public QuRelationRefDefine getRelationRef(String name);

	/**
	 * 返回当前查询结构的第一个关系引用定义
	 * 
	 * @return
	 */
	public QuRelationRefDefine getRootReference();

	/**
	 * 返回当前查询结构定义的所有关系引用的<strong>先序遍历</strong>的可迭代接口
	 * 
	 * @return
	 */
	public Iterable<? extends QuRelationRefDefine> getReferences();

	/**
	 * 获得行过滤条件
	 * 
	 * <p>
	 * 及where子句定义条件
	 * 
	 * @return 返回行过滤条件,未定义则返回null
	 */
	public ConditionalExpression getCondition();

	/**
	 * 获取分组规则定义
	 * 
	 * @return 未定义则返回null
	 */
	public Container<? extends GroupByItemDefine> getGroupBys();

	/**
	 * 获取分组类型
	 * 
	 * @see org.eclipse.jt.core.def.query.GroupByType
	 * 
	 * @return 默认为GroupByType.DEFAULT
	 */
	public GroupByType getGroupByType();

	/**
	 * 获取分组过滤条件
	 * 
	 * @return 未定义则返回null
	 */
	public ConditionalExpression getHaving();

	public SelectColumnDefine findColumn(String columnName);

	public SelectColumnDefine getColumn(String columnName);

	/**
	 * 获取是否排除重复行
	 * 
	 * <p>
	 * 默认为false,即不排除重复行
	 */
	public boolean getDistinct();

	/**
	 * 获得输出字段列表
	 * 
	 * @return 不会返回null
	 */
	public NamedElementContainer<? extends SelectColumnDefine> getColumns();

	/**
	 * 返回集合运算定义
	 * 
	 * @return 未定义则返回null
	 */
	public Container<? extends SetOperateDefine> getSetOperates();

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Container<? extends OrderByItemDefine> getOrderBys();

}
