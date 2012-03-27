package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDefine;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 关系引用定义
 * 
 * <p>
 * 关系引用定义表示二维表类型的数据结构.
 * 
 * @author Jeff Tang
 * 
 * @param <TRelation>
 */
abstract class RelationRefImpl<TRelation extends Relation> extends
		NamedDefineImpl implements RelationRef {

	RelationRefImpl(String name, TRelation target) {
		super(name);
		this.target = target;
	}

	public final TRelation getTarget() {
		return this.target;
	}

	final TRelation target;

	private volatile transient int modCount;

	public final void increaseModCount() {
		this.modCount++;
	}

	public final int modCount() {
		return this.modCount;
	}

	static final IllegalArgumentException notSupportedRelationColumnRefException(
			RelationRef relationRef, RelationColumnDefine column) {
		throw new IllegalArgumentException("不能从当前"
				+ (relationRef instanceof TableRef ? "表引用" : "查询引用") + "["
				+ relationRef.getName() + "]构造指向"
				+ (column instanceof TableFieldDefineImpl ? "表字段" : "查询输出列")
				+ "[" + column.getName() + "]的关系列引用表达式.");
	}

	static final IllegalArgumentException notSupportedRelationColumnRefException(
			RelationRef relationRef, String columnName) {
		throw new IllegalArgumentException("不能从当前"
				+ (relationRef instanceof TableRef ? "表引用" : "查询引用") + "["
				+ relationRef.getName() + "]构造指向关系列[" + columnName
				+ "]的关系列引用表达式.");
	}

	static final NullArgumentException relationColumnNull() {
		return new NullArgumentException("关系列定义为空");
	}

	public final HierarchyOperateExpr xParentRECID(HierarchyDefine hierarchy) {
		return new HierarchyOperateExpr(this, hierarchy,
				HierarchyOperatorImpl.PARENT_RECID, null);
	}

	public final HierarchyOperateExpr xAncestorRECID(HierarchyDefine hierarchy,
			Object relative) {
		return new HierarchyOperateExpr(this, hierarchy,
				HierarchyOperatorImpl.RELATIVE_ANCESTOR_RECID,
				ValueExpr.expOf(relative));
	}

	public final HierarchyOperateExpr xAncestorRECIDOfLevel(
			HierarchyDefine hierarchy, Object absolute) {
		return new HierarchyOperateExpr(this, hierarchy,
				HierarchyOperatorImpl.ABUSOLUTE_ANCESTOR_RECID,
				ValueExpr.expOf(absolute));
	}

	public final HierarchyOperateExpr xLevelOf(HierarchyDefine hierarchy) {
		return new HierarchyOperateExpr(this, hierarchy,
				HierarchyOperatorImpl.LEVEVL_OF, null);
	}

	public final HierarchyPredicateExpr xIsLeaf(HierarchyDefine hierarchy) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_LEAF, null, null);
	}

	public final HierarchyPredicateExpr xIsChildOf(HierarchyDefine hierarchy,
			RelationRefDefine parent) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_CHILD_OF, parent, null);
	}

	public final HierarchyPredicateExpr xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_DESCENDANT_OF, ancestor, null);
	}

	public final HierarchyPredicateExpr xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor, Object range) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_RANGE_DESCENDANT_OF, ancestor,
				ValueExpr.expOf(range));
	}

	public final HierarchyPredicateExpr xIsRelativeDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor,
			Object relative) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_RELATIVE_DESCENDANT_OF, ancestor,
				ValueExpr.expOf(relative));
	}

	public final HierarchyPredicateExpr xIsParentOf(HierarchyDefine hierarchy,
			RelationRefDefine child) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_PARENT_OF, child, null);
	}

	public final HierarchyPredicateExpr xIsAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_ANCESTOR_OF, descendant, null);
	}

	public final HierarchyPredicateExpr xIsRelativeAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant,
			Object relative) {
		return new HierarchyPredicateExpr(false, this, hierarchy,
				HierarchyPredicateImpl.IS_RELATIVE_ANCESTOR_OF, descendant,
				null);
	}

}
