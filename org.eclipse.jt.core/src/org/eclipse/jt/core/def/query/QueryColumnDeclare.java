package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.obja.StructFieldDefine;

/**
 * ��ѯ��䶨�������ж���
 * 
 * @see org.eclipse.jt.core.def.query.QueryColumnDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryColumnDeclare extends QueryColumnDefine,
		SelectColumnDeclare {

	public QueryStatementDeclare getOwner();

	/**
	 * �����ж���ı��ʽ
	 */
	public void setExpression(ValueExpression value);

	/**
	 * ����ӳ�䵽��ģ�͵��ֶ�
	 * 
	 * @param field
	 *            javaʵ�����ԵĽṹ�ֶζ���
	 */
	public void setMapingField(StructFieldDefine field);

	/**
	 * ����ӳ�䵽��ģ�͵��ֶ�
	 * 
	 * @param structFieldName
	 *            javaʵ�����Ե�����(���ִ�Сд)
	 */
	public void setMapingField(String structFieldName);
}
