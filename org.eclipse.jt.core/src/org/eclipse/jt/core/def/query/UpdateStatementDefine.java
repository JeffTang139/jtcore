package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * ������䶨��
 * 
 * @author Jeff Tang
 */
public interface UpdateStatementDefine extends ModifyStatementDefine {

	/**
	 * ��ȡ������������
	 * 
	 * @return
	 */
	ConditionalExpression getCondition();
}
