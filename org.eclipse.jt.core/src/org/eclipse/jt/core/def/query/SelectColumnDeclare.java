package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * ��ѯ���ѡ���ж���
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnDeclare extends SelectColumnDefine,
		RelationColumnDeclare {

	public SelectDeclare getOwner();

	/**
	 * �����ж���ı��ʽ
	 */
	public void setExpression(ValueExpression value);
}
