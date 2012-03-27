package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.def.query.QuRelationRefDefine;

/**
 * ����ν�ʱ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface HierarchyPredicateExpression extends ConditionalExpression {

	/**
	 * ����ν��
	 */
	public HierarchyPredicate getPredicate();

	/**
	 * ����Դ������
	 */
	public QuRelationRefDefine getSource();

	/**
	 * ����Ŀ�������
	 */
	public QuRelationRefDefine getTarget();

	/**
	 * ����ν�ʵļ���ֵ
	 */
	public ValueExpression getLevel();

}
