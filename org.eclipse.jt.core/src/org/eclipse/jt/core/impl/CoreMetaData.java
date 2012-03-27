/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.def.table.AsTableField.DBType;
import org.eclipse.jt.core.type.GUID;


/**
 * 核心元数据实体
 * 
 * @author Jeff Tang
 * 
 */
@AsTable
final class CoreMetaData {

	@AsTableField(isRecid = true)
	public GUID RECID;

	@AsTableField(dbType = DBType.Varchar, length = 16, pkOrdinal = 0)
	public MetaElementType kind;

	@AsTableField(dbType = DBType.Varchar, length = 64, pkOrdinal = 1)
	public String name;

	@AsTableField(dbType = DBType.Varchar, length = 256)
	public String space;

	@AsTableField(dbType = DBType.Text)
	public String xml;
}
