package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.exp.ConstExpression;
import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * �߼��ֶζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface TableFieldDefine extends FieldDefine, RelationColumnDefine {

	/**
	 * ��ȡ�������߼�����
	 */
	public TableDefine getOwner();

	/**
	 * ��ȡ�ֶ�ʵ�ʴ洢�����ݿ����
	 * 
	 * @return
	 */
	public DBTableDefine getDBTable();

	/**
	 * ��ȡ�Ƿ��������ֶΣ��߼�������
	 * 
	 * @return
	 */
	public boolean isPrimaryKey();

	/**
	 * ��ȡ�Ƿ��Ǽ�¼�б�ʶ�ֶ�(RECID);
	 * 
	 * @return
	 */
	public boolean isRECID();

	/**
	 * ��ȡ�Ƿ����а汾�ֶ�(RECVER)
	 * 
	 * @return
	 */
	public boolean isRECVER();

	public ConstExpression getDefault();

	/**
	 * ���ݿ��е�ʵ������������ͨ���Ƕ����������п��ܻ������ݿⲻͬ���в���
	 * 
	 * @return ����ʵ����
	 */
	public String getNameInDB();

	/**
	 * �Ƿ�Ϊģ���ֶ�
	 * 
	 * @return
	 */
	public boolean isTemplated();

}
