package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * ɾ����䶨��
 * 
 * @author Jeff Tang
 * 
 */
public interface DeleteStatementDefine extends ModifyStatementDefine {

	/**
	 * ��ȡɾ����������
	 * 
	 * @return
	 */
	public ConditionalExpression getCondition();
}
