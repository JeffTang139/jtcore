package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * 用户与组织机构映射实体 用户与组织机构映射实体定义对应于用户与组织机构映射物理表的存储结构。
 * 
 * <pre>
 * 列名                类型        空否
 * RECID            GUID       否
 * actorID          GUID       否
 * orgID            GUID       否
 * </pre>
 * 
 * @author Jeff Tang 2010-01
 */
@AsTable
final class CoreAuthUOMEntity {

	/**
	 * 记录ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;

	/**
	 * 访问者ID
	 */
	@AsTableField(isRequired = true)
	public GUID actorID;

	/**
	 * 访问者ID
	 */
	@AsTableField(isRequired = true)
	public GUID orgID;

}
