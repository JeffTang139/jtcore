package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.type.DataType;

/**
 * �������
 * 
 * @author Jeff Tang
 * 
 */
public interface DBTableDeclare extends DBTableDefine, NamedDeclare {

	/**
	 * ��������
	 */
	public TableDeclare getOwner();

	/**
	 * �����洢�ڸ�������е��ֶ�
	 */
	public TableFieldDeclare newField(String name, DataType type);

}
