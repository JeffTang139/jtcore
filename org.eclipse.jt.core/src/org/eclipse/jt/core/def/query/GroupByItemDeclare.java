package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * ��ѯ���������
 * 
 * @see org.eclipse.jt.core.def.query.GroupByItemDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface GroupByItemDeclare extends GroupByItemDefine {

	/**
	 * ���÷������ı��ʽ
	 * 
	 * @param expression
	 */
	public void setExpression(ValueExpression expression);
}
