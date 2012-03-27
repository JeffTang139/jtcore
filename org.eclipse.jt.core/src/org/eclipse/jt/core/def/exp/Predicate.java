package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.PredicateImpl;

/**
 * ν��
 * 
 * <p>
 * �����Ƚ��������,�����߼������
 * 
 * @author Jeff Tang
 * 
 */
public interface Predicate {

	/**
	 * С��
	 */
	public static final Predicate LESS_THAN = PredicateImpl.LESS_THAN;

	/**
	 * С�ڻ����
	 */
	public static final Predicate LESS_THAN_OR_EQUAL_TO = PredicateImpl.LESS_THAN_OR_EQUAL_TO;

	/**
	 * ����
	 */
	public static final Predicate GREATER_THAN = PredicateImpl.GREATER_THAN;

	/**
	 * ���ڻ����
	 */
	public static final Predicate GREATER_THAN_OR_EQUAL_TO = PredicateImpl.GREATER_THAN_OR_EQUAL_TO;

	/**
	 * ����
	 */
	public static final Predicate EQUAL_TO = PredicateImpl.EQUAL_TO;

	/**
	 * ������
	 */
	public static final Predicate NOT_EQUAL_TO = PredicateImpl.NOT_EQUAL_TO;

	/**
	 * �ڷ�Χ��,������
	 */
	public static final Predicate BETWEEN = PredicateImpl.BETWEEN;

	/**
	 * �ڷ�Χ��,���ұ�����
	 */
	public static final Predicate BETWEEN_EXCLUDE_LEFT_SIDE = PredicateImpl.BETWEEN_EXCLUDE_LEFT_SIDE;

	/**
	 * �ڷ�Χ��,����ҿ�����
	 */
	public static final Predicate BETWEEN_EXCLUDE_RIGHT_SIDE = PredicateImpl.BETWEEN_EXCLUDE_RIGHT_SIDE;

	/**
	 * �ڷ�Χ��,������
	 */
	public static final Predicate BETWEEN_EXCLUDE_BOTH_SIDES = PredicateImpl.BETWEEN_EXCLUDE_BOTH_SIDES;

	/**
	 * ���Ӳ�ѯ���б�ƥ��
	 */
	public static final Predicate IN = PredicateImpl.IN;

	/**
	 * �ַ���like
	 */
	public static final Predicate STR_LIKE = PredicateImpl.STR_LIKE;

	/**
	 * �ַ����Ƿ�ָ��ǰ׺
	 */
	public static final Predicate STR_STARTS_WITH = PredicateImpl.STR_STARTS_WITH;

	/**
	 * �ַ����Ƿ�ָ����׺
	 */
	public static final Predicate STR_ENDS_WITH = PredicateImpl.STR_ENDS_WITH;

	/**
	 * �ַ����Ƿ����
	 */
	public static final Predicate STR_CONTAINS = PredicateImpl.STR_CONTAINS;

	/**
	 * Ϊ��
	 */
	public static final Predicate IS_NULL = PredicateImpl.IS_NULL;

	/**
	 * ��Ϊ��
	 */
	public static final Predicate IS_NOT_NULL = PredicateImpl.IS_NOT_NULL;

}
