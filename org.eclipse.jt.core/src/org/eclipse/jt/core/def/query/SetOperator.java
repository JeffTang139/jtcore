package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.impl.SetOperatorImpl;

/**
 * ��������
 * 
 * @author Jeff Tang
 * 
 */
public interface SetOperator {

	/**
	 * ������
	 */
	public static final SetOperator UNION = SetOperatorImpl.UNION;

	/**
	 * ������
	 */
	public static final SetOperator UNION_ALL = SetOperatorImpl.UNION_ALL;

	/**
	 * ���Ͻ�
	 */
	public static final SetOperator INTERSECT = SetOperatorImpl.INTERSECT;

	/**
	 * ���ϲ�
	 */
	public static final SetOperator DIFFERENCE = SetOperatorImpl.DIFFERENCE;
}
