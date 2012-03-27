package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.PredicateExpression;

/**
 * �Ӳ�ѯ����
 * 
 * @see org.eclipse.jt.core.def.query.SubQueryDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface SubQueryDeclare extends SubQueryDefine, SelectDeclare {

	/**
	 * ����Exitsν�ʱ��ʽ
	 * 
	 * @return
	 */
	public PredicateExpression exists();

	/**
	 * ����Not Existsν�ʱ��ʽ
	 * 
	 * @return
	 */
	public PredicateExpression notExists();

}
