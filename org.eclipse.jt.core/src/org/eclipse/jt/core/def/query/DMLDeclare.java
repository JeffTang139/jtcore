package org.eclipse.jt.core.def.query;

/**
 * DML��䶨��
 * 
 * @see org.eclipse.jt.core.def.query.DMLDefine
 * 
 * @author Jeff Tang
 */
public interface DMLDeclare extends DMLDefine {

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

}
