package org.eclipse.jt.core.def.query;

/**
 * ������䶨��
 * 
 * @author Jeff Tang
 * 
 */
public interface InsertStatementDefine extends ModifyStatementDefine {

	/**
	 * ���ض������ֵ�Ĳ�ѯ���
	 * 
	 * @return
	 */
	public DerivedQueryDefine getInsertValues();
}
