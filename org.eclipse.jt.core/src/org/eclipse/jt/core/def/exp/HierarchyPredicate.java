package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.HierarchyPredicateImpl;

/**
 * ¼¶´ÎÎ½´Ê
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyPredicate {

	public static final HierarchyPredicate IS_LEAF = HierarchyPredicateImpl.IS_LEAF;

	public static final HierarchyPredicate IS_CHILD_OF = HierarchyPredicateImpl.IS_CHILD_OF;

	public static final HierarchyPredicate IS_DESCENDANT_OF = HierarchyPredicateImpl.IS_DESCENDANT_OF;

	public static final HierarchyPredicate IS_RELATIVE_DESCENDANT_OF = HierarchyPredicateImpl.IS_RELATIVE_DESCENDANT_OF;

	public static final HierarchyPredicate IS_RANGE_DESCENDANT_OF = HierarchyPredicateImpl.IS_RANGE_DESCENDANT_OF;

	public static final HierarchyPredicate IS_PARENT_OF = HierarchyPredicateImpl.IS_PARENT_OF;

	public static final HierarchyPredicate IS_ANCESTOR_OF = HierarchyPredicateImpl.IS_ANCESTOR_OF;

}
