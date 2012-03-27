package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.impl.SetOperatorImpl;

/**
 * 集合运算
 * 
 * @author Jeff Tang
 * 
 */
public interface SetOperator {

	/**
	 * 集合与
	 */
	public static final SetOperator UNION = SetOperatorImpl.UNION;

	/**
	 * 集合与
	 */
	public static final SetOperator UNION_ALL = SetOperatorImpl.UNION_ALL;

	/**
	 * 集合交
	 */
	public static final SetOperator INTERSECT = SetOperatorImpl.INTERSECT;

	/**
	 * 集合差
	 */
	public static final SetOperator DIFFERENCE = SetOperatorImpl.DIFFERENCE;
}
