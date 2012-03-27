package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QuRelationRefDefine;

/**
 * ���κ������ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyOperateExpression extends ValueExpression {

	/**
	 * ��ȡ���������
	 */
	public HierarchyOperator getOperator();

	/**
	 * ������
	 */
	public QuRelationRefDefine getSource();

	/**
	 * ����ֵ����
	 */
	public ValueExpression getLevel();
}
