package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * �Ӳ�ѯ���ʽ
 * 
 * @author Jeff Tang
 */
public interface SubQueryExpression extends ValueExpression {

	/**
	 * ����Ӳ�ѯ����
	 * 
	 * @return �����Ӳ�ѯ����
	 */
	public SubQueryDefine getSubQuery();
}
