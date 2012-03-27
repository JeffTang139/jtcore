package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.TableFieldRefExpr;

/**
 * ������䶨��
 * 
 * @see org.eclipse.jt.core.def.query.ModifyStatementDefine
 * 
 * @author Jeff Tang
 */
public interface ModifyStatementDeclare extends ModifyStatementDefine,
		StatementDeclare, RelationRefDomainDeclare {

	/**
	 * �����Ӳ�ѯ����
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * ���쵼����ѯ����,����from�Ӿ�ʹ��
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * @param field
	 * @return
	 */
	public TableFieldRefExpr expOf(RelationColumnDefine column);

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * @param columnName
	 * @return
	 */
	public TableFieldRefExpr expOf(String columnName);
}
