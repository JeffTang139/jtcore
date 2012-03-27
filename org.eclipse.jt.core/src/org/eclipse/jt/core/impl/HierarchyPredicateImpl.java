package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.HierarchyPredicate;

/**
 * 级次谓词
 * 
 * @author Jeff Tang
 * 
 */
public enum HierarchyPredicateImpl implements HierarchyPredicate {

	/**
	 * 指示当前表引用中为叶子节点的行
	 */
	IS_LEAF("xIsLeaf") {

	},

	/**
	 * 指示当前表引用中为目标表引用的子节点的行
	 */
	IS_CHILD_OF("xIsChildOf") {

	},

	IS_CHILD_OF_OR_SELF("") {

	},

	/**
	 * 指示当前表引用中为目标表引用的子孙节点的行
	 */
	IS_DESCENDANT_OF("xIsDescendantOf") {

	},

	IS_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * 指示当前表引用中为目标表引用的第n级子孙的行
	 */
	IS_RELATIVE_DESCENDANT_OF("xIsRelativeDescendantOf") {

	},

	IS_RELATIVE_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * 指示当前表引用中为目标表引用的不超过n级的子孙的行
	 */
	IS_RANGE_DESCENDANT_OF("xIsDescendantOf") {

	},

	IS_RANGE_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * 指示当前表引用中为目标表引用的父节点的行
	 */
	IS_PARENT_OF("xIsParentOf") {

	},

	/**
	 * 指示当前表引用中为目标表引用的祖先节点的行
	 */
	IS_ANCESTOR_OF("xIsAncestorOf") {

	},

	IS_RELATIVE_ANCESTOR_OF("xIsAncestorOf") {

	};

	/**
	 * 从QuRelationRefDeclare上构造指定表达式的方法名称
	 */
	final String callName;

	HierarchyPredicateImpl(final String callName) {
		this.callName = callName;
	}

	// @Deprecated
	// static final void markLeft(HierarchyPredicateExpr expr) {
	// expr.left.tableUsage().use(expr.hierarchy);
	// }
	//
	// @Deprecated
	// static final void markBoth(HierarchyPredicateExpr expr) {
	// expr.left.tableUsage().use(expr.hierarchy);
	// expr.right.tableUsage().use(expr.hierarchy);
	// }
	//
	// @Deprecated
	// static final void markRight(HierarchyPredicateExpr expr) {
	// expr.right.tableUsage().use(expr.hierarchy);
	// }

}
