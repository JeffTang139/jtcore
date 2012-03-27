package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.HierarchyOperatorImpl;

/**
 * 级次运算符
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyOperator {

	/**
	 * 获取父结点的RECID
	 */
	public static final HierarchyOperator PARENT_RECID = HierarchyOperatorImpl.PARENT_RECID;

	/**
	 * 获取相对n级的祖先结点的RECID
	 */
	public static final HierarchyOperator RELATIVE_ANCESTOR_RECID = HierarchyOperatorImpl.RELATIVE_ANCESTOR_RECID;

	/**
	 * 获取绝对深度为n的祖先结点的RECID
	 */
	public static final HierarchyOperator ABUSOLUTE_ANCESTOR_RECID = HierarchyOperatorImpl.ABUSOLUTE_ANCESTOR_RECID;

	/**
	 * 获取结点的级次深度
	 */
	public static final HierarchyOperator LEVEVL_OF = HierarchyOperatorImpl.LEVEVL_OF;

}
