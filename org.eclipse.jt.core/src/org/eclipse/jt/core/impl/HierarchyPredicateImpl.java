package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.HierarchyPredicate;

/**
 * ����ν��
 * 
 * @author Jeff Tang
 * 
 */
public enum HierarchyPredicateImpl implements HierarchyPredicate {

	/**
	 * ָʾ��ǰ��������ΪҶ�ӽڵ����
	 */
	IS_LEAF("xIsLeaf") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õ��ӽڵ����
	 */
	IS_CHILD_OF("xIsChildOf") {

	},

	IS_CHILD_OF_OR_SELF("") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õ�����ڵ����
	 */
	IS_DESCENDANT_OF("xIsDescendantOf") {

	},

	IS_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õĵ�n���������
	 */
	IS_RELATIVE_DESCENDANT_OF("xIsRelativeDescendantOf") {

	},

	IS_RELATIVE_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õĲ�����n�����������
	 */
	IS_RANGE_DESCENDANT_OF("xIsDescendantOf") {

	},

	IS_RANGE_DESCENDANT_OF_OR_SELF("") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õĸ��ڵ����
	 */
	IS_PARENT_OF("xIsParentOf") {

	},

	/**
	 * ָʾ��ǰ��������ΪĿ������õ����Ƚڵ����
	 */
	IS_ANCESTOR_OF("xIsAncestorOf") {

	},

	IS_RELATIVE_ANCESTOR_OF("xIsAncestorOf") {

	};

	/**
	 * ��QuRelationRefDeclare�Ϲ���ָ�����ʽ�ķ�������
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
