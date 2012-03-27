package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.FieldDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDeclare;
import org.eclipse.jt.core.type.DataType;

/**
 * �߼��ֶζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldDeclare extends TableFieldDefine, FieldDeclare,
		RelationColumnDeclare {

	/**
	 * ����
	 */
	public TableDeclare getOwner();

	/**
	 * ���ݿ��
	 */
	public DBTableDeclare getDBTable();

	/**
	 * �����Ƿ��������ֶΣ��߼�������
	 */
	public void setPrimaryKey(boolean value);

	/**
	 * ����Ĭ��ֵ
	 * 
	 * @param value
	 */
	public void setDefault(Object value);

	/**
	 * ���Ըı��ֶεľ��ȣ�ֻ����ַ����Ͷ����ƴ��ж���С����Ч��
	 */
	public boolean adjustType(DataType newType);

	/**
	 * �������ݿ���������
	 */
	public void setNameInDB(String nameInDB);

	/**
	 * �����Ƿ���Ϊģ���ֶ�
	 * 
	 * @param templated
	 */
	public void setTemplated(boolean templated);
}
