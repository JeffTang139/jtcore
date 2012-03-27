package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDefine;

/**
 * ��ϵ�����ñ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationColumnRefExpr extends ValueExpression {

	/**
	 * ��ȡָ��Ĺ�ϵ�ж���
	 * 
	 * @return
	 */
	public RelationColumnDefine getColumn();

	/**
	 * ��ȡ���ڵĹ�ϵ���ö���
	 * 
	 * @return
	 */
	public RelationRefDefine getReference();
}
