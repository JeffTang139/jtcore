package org.eclipse.jt.core.da.ext;

import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 字段
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetField extends RPTRecordSetColumn {
	/**
	 * 对应字段
	 */
	public TableFieldDefine getTableField();

	/**
	 * 返回该字段的约束
	 */
	public RPTRecordSetRestriction getRestriction();
}
