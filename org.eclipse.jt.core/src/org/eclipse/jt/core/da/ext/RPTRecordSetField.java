package org.eclipse.jt.core.da.ext;

import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * �ֶ�
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetField extends RPTRecordSetColumn {
	/**
	 * ��Ӧ�ֶ�
	 */
	public TableFieldDefine getTableField();

	/**
	 * ���ظ��ֶε�Լ��
	 */
	public RPTRecordSetRestriction getRestriction();
}
