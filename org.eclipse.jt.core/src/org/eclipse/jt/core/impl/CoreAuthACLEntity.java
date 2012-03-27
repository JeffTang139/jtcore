package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * ACL实体定义<br>
 * ACL项描述的是某操作者在某组织机构下对某类资源中的某个资源拥有什么权限。<br>
 * ACL实体定义对应于ACL物理表的存储结构。
 * 
 * <pre>
 * 列名                类型      空否
 * RECID            GUID     否
 * actorID          GUID     否
 * orgID            GUID     否
 * resCategoryID    GUID     否
 * resourceID       GUID     否
 * authorityCode    int      否
 * </pre>
 * 
 * @author Jeff Tang 2009-12
 */
@AsTable
final class CoreAuthACLEntity {

	/**
	 * 记录ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;

	/**
	 * 操作者ID
	 */
	@AsTableField(isRequired = true)
	public GUID actorID;

	/**
	 * 组织机构ID
	 */
	@AsTableField(isRequired = true)
	public GUID orgID;

	/**
	 * 资源类别ID
	 */
	@AsTableField(isRequired = true)
	public GUID resCategoryID;

	/**
	 * 资源ID
	 */
	@AsTableField(isRequired = true)
	public GUID resourceID;

	/**
	 * 授权信息编码
	 */
	@AsTableField(isRequired = true)
	public int authorityCode;
	
	/**
	 * 根资源项GUID
	 */
	static final GUID ROOT_RESOURCE_GUID = GUID.valueOf(0xAAAAAAAAAAAAAAAAL,
			0xAAAAAAAAAAAAAAAAL);

}
