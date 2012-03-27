package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationRefDefine;
import org.eclipse.jt.core.def.table.HierarchyDefine;

public interface HierarchyOperatable extends
		org.eclipse.jt.core.def.query.HierarchyOperatable {

	public HierarchyOperateExpr xParentRECID(HierarchyDefine hierarchy);

	public HierarchyOperateExpr xAncestorRECID(HierarchyDefine hierarchy,
			Object relative);

	public HierarchyOperateExpr xAncestorRECIDOfLevel(
			HierarchyDefine hierarchy, Object absolute);

	public HierarchyOperateExpr xLevelOf(HierarchyDefine hierarchy);

	public HierarchyPredicateExpr xIsLeaf(HierarchyDefine hierarchy);

	public HierarchyPredicateExpr xIsChildOf(HierarchyDefine hierarchy,
			RelationRefDefine parent);

	public HierarchyPredicateExpr xIsDescendantOf(HierarchyDefine hierarchy,
			RelationRefDefine ancestor);

	public HierarchyPredicateExpr xIsDescendantOf(HierarchyDefine hierarchy,
			RelationRefDefine ancestor, Object range);

	public HierarchyPredicateExpr xIsRelativeDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor,
			Object relative);

	public HierarchyPredicateExpr xIsParentOf(HierarchyDefine hierarchy,
			RelationRefDefine child);

	public HierarchyPredicateExpr xIsAncestorOf(HierarchyDefine hierarchy,
			RelationRefDefine descendant);

	public HierarchyPredicateExpr xIsRelativeAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant,
			Object relative);
}
