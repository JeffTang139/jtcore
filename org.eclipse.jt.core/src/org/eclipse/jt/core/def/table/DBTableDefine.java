package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * �������
 * 
 * @author Jeff Tang
 * 
 */
public interface DBTableDefine extends NamedDefine {

	/**
	 * ��������
	 */
	public TableDefine getOwner();

	/**
	 * ������ڸ��������ֶθ���
	 */
	public int getFieldCount();

}
