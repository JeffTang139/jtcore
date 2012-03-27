/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.def.table.AsTableField.DBType;
import org.eclipse.jt.core.type.GUID;


/**
 * վ����Ϣ��
 * 
 * @author Jeff Tang
 * 
 */
@AsTable
final class CoreSiteInfo {

	@AsTableField(isRecid = true)
	public GUID RECID;

	@AsTableField(dbType = DBType.Date)
	public long createTime;

	@AsTableField(dbType = DBType.Text)
	public String xml;
}
