package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DefineBase;

/**
 * �����ֶζ���
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexItemDefine extends DefineBase {
	/**
	 * �����ֶ�
	 * 
	 * @return ���������ֶ�
	 */
	public TableFieldDefine getField();

	/**
	 * �Ƿ�������
	 * 
	 * @return ����
	 */
	public boolean isDesc();
}
