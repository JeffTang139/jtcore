package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.PredicateImpl;

/**
 * 谓词
 * 
 * <p>
 * 包括比较运算符符,部分逻辑运算符
 * 
 * @author Jeff Tang
 * 
 */
public interface Predicate {

	/**
	 * 小于
	 */
	public static final Predicate LESS_THAN = PredicateImpl.LESS_THAN;

	/**
	 * 小于或等于
	 */
	public static final Predicate LESS_THAN_OR_EQUAL_TO = PredicateImpl.LESS_THAN_OR_EQUAL_TO;

	/**
	 * 大于
	 */
	public static final Predicate GREATER_THAN = PredicateImpl.GREATER_THAN;

	/**
	 * 大于或等于
	 */
	public static final Predicate GREATER_THAN_OR_EQUAL_TO = PredicateImpl.GREATER_THAN_OR_EQUAL_TO;

	/**
	 * 等于
	 */
	public static final Predicate EQUAL_TO = PredicateImpl.EQUAL_TO;

	/**
	 * 不等于
	 */
	public static final Predicate NOT_EQUAL_TO = PredicateImpl.NOT_EQUAL_TO;

	/**
	 * 在范围内,闭区间
	 */
	public static final Predicate BETWEEN = PredicateImpl.BETWEEN;

	/**
	 * 在范围内,左开右闭区间
	 */
	public static final Predicate BETWEEN_EXCLUDE_LEFT_SIDE = PredicateImpl.BETWEEN_EXCLUDE_LEFT_SIDE;

	/**
	 * 在范围内,左闭右开区间
	 */
	public static final Predicate BETWEEN_EXCLUDE_RIGHT_SIDE = PredicateImpl.BETWEEN_EXCLUDE_RIGHT_SIDE;

	/**
	 * 在范围内,开区间
	 */
	public static final Predicate BETWEEN_EXCLUDE_BOTH_SIDES = PredicateImpl.BETWEEN_EXCLUDE_BOTH_SIDES;

	/**
	 * 与子查询或列表匹配
	 */
	public static final Predicate IN = PredicateImpl.IN;

	/**
	 * 字符串like
	 */
	public static final Predicate STR_LIKE = PredicateImpl.STR_LIKE;

	/**
	 * 字符串是否指定前缀
	 */
	public static final Predicate STR_STARTS_WITH = PredicateImpl.STR_STARTS_WITH;

	/**
	 * 字符串是否指定后缀
	 */
	public static final Predicate STR_ENDS_WITH = PredicateImpl.STR_ENDS_WITH;

	/**
	 * 字符串是否包含
	 */
	public static final Predicate STR_CONTAINS = PredicateImpl.STR_CONTAINS;

	/**
	 * 为空
	 */
	public static final Predicate IS_NULL = PredicateImpl.IS_NULL;

	/**
	 * 不为空
	 */
	public static final Predicate IS_NOT_NULL = PredicateImpl.IS_NOT_NULL;

}
