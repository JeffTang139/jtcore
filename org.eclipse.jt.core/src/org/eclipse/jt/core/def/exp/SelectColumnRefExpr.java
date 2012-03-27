package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QueryReferenceDefine;
import org.eclipse.jt.core.def.query.SelectColumnDefine;

/**
 * ��ѯ�����ñ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnRefExpr extends RelationColumnRefExpr {

	/**
	 * ��ȡ��ѯ�ж���
	 */
	public SelectColumnDefine getColumn();

	/**
	 * ��ȡ���ڵĲ�ѯ���ö���
	 */
	public QueryReferenceDefine getReference();
}
