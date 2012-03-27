package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.HierarchyOperateExpression;
import org.eclipse.jt.core.def.exp.HierarchyPredicateExpression;
import org.eclipse.jt.core.def.table.HierarchyDefine;

public interface HierarchyOperatable {

	/**
	 * 使用指定的级次定义,返回节点的直接父节点的RECID的表达式
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @return
	 */
	public HierarchyOperateExpression xParentRECID(HierarchyDefine hierarchy);

	/**
	 * 使用指定的级次定义,返回节点向上第n级父节点的RECID的表达式.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表.
	 * @param relative
	 *            向上父节点的级数,范围为[1,31].可使用ValueExpression.builder转化为整型表达式.
	 * 
	 * @return
	 */
	public HierarchyOperateExpression xAncestorRECID(HierarchyDefine hierarchy,
			Object relative);

	/**
	 * 使用指定的级次定义,返回节点绝对深度为n的父节点的RECID的表达式
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param absolute
	 *            父节点的绝对深度,级次深度范围为[1,31].可使用ValueExpression.builder转化为整型表达式.
	 * 
	 * @return
	 */
	public HierarchyOperateExpression xAncestorRECIDOfLevel(
			HierarchyDefine hierarchy, Object absolute);

	/**
	 * 使用指定级次定义,返回节点的级次深度的表达式
	 * 
	 * <p>
	 * 深度的范围为[1,32]
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @return
	 */
	public HierarchyOperateExpression xLevelOf(HierarchyDefine hierarchy);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集的各行是否为叶子节点的条件表达式
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @return
	 */
	public HierarchyPredicateExpression xIsLeaf(HierarchyDefine hierarchy);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的直接子节点的条件表达式
	 * 
	 * <p>
	 * 适合从父节点范围已经确定,查询子节点的情况. 如果使用该方法从子节点查询父节点则效率十分底下
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param parent
	 *            指定父节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * @return
	 */
	public HierarchyPredicateExpression xIsChildOf(HierarchyDefine hierarchy,
			RelationRefDefine parent);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的子孙节点(直接子或间接子节点)的条件表达式
	 * 
	 * <p>
	 * 适合从父节点范围已经确定,查询子节点的情况.如果使用该方法从子节点查询父节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param ancestor
	 *            指定祖先节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * @return
	 */
	public HierarchyPredicateExpression xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的不超过n级深度的子孙节点的条件表达式
	 * <p>
	 * 适合从父节点范围已经确定,查询子节点的情况. 如果使用该方法从子节点查询父节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param ancestor
	 *            指定祖先节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * @param relative
	 *            指定第n级的子孙节点目标节点向下第n级子孙,取值范围为[1,31],直接子节点为1.可使用ValueExpression.
	 *            builder转化为整型表达式.
	 * @return
	 */
	public HierarchyPredicateExpression xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor, Object range);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的第n级的子孙节点的条件表达式
	 * 
	 * <p>
	 * 适合从父节点范围已经确定,查询子节点的情况. 如果使用该方法从子节点查询父节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param ancestor
	 *            指定祖先节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * @param relative
	 *            相差级次深度的最大值.可使用ValueExpression.builder转化为整型表达式.
	 * @return
	 */
	public HierarchyPredicateExpression xIsRelativeDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor,
			Object relative);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的的直接父节点的条件表达式
	 * 
	 * <p>
	 * 适合从子节点范围已经确定,查询父节点的情况. 如果使用该方法从父节点查询子节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param child
	 *            指定子节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * 
	 * @return
	 */
	public HierarchyPredicateExpression xIsParentOf(HierarchyDefine hierarchy,
			RelationRefDefine child);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的祖先节点(直接或间接父节点)的条件表达式
	 * 
	 * <p>
	 * 适合从子节点范围已经确定,查询父节点的情况. 如果使用该方法从父节点查询子节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param descendant
	 *            指定子节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * 
	 * @return
	 */
	public HierarchyPredicateExpression xIsAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant);

	/**
	 * 使用指定的级次定义,返回当前表引用限定行集中是目标表引用限定行集的祖先节点(直接或间接父节点)的条件表达式
	 * 
	 * <p>
	 * 适合从子节点范围已经确定,查询父节点的情况. 如果使用该方法从父节点查询子节点则效率十分底下.
	 * 
	 * @param hierarchy
	 *            使用的级次定义,应属于当前表引用的目标逻辑表
	 * @param descendant
	 *            指定子节点行集的表引用,其目标表应于当前表引用的目标表一致
	 * @param relative
	 *            相差级次深度的最大值.可使用ValueExpression.builder转化为整型表达式.
	 * @return
	 */
	public HierarchyPredicateExpression xIsRelativeAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant,
			Object relative);
}
