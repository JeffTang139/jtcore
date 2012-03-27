package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.HierarchyOperatorImpl;

/**
 * ���������
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyOperator {

	/**
	 * ��ȡ������RECID
	 */
	public static final HierarchyOperator PARENT_RECID = HierarchyOperatorImpl.PARENT_RECID;

	/**
	 * ��ȡ���n�������Ƚ���RECID
	 */
	public static final HierarchyOperator RELATIVE_ANCESTOR_RECID = HierarchyOperatorImpl.RELATIVE_ANCESTOR_RECID;

	/**
	 * ��ȡ�������Ϊn�����Ƚ���RECID
	 */
	public static final HierarchyOperator ABUSOLUTE_ANCESTOR_RECID = HierarchyOperatorImpl.ABUSOLUTE_ANCESTOR_RECID;

	/**
	 * ��ȡ���ļ������
	 */
	public static final HierarchyOperator LEVEVL_OF = HierarchyOperatorImpl.LEVEVL_OF;

}
