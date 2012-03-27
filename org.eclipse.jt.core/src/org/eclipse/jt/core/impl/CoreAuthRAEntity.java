package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.table.AsTable;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.type.GUID;


/**
 * 角色分配项实体定义<br>
 * 角色分配项实体定义对应于角色分配物理表的存储结构。
 * 
 * <pre>
 * 列名                类型      空否
 * RECID            GUID     否
 * actorID          GUID     否
 * roleID           GUID     否
 * priority         int      否
 * </pre>
 * 
 * @author Jeff Tang 2009-12
 */
@AsTable
final class CoreAuthRAEntity {

	/**
	 * 记录ID
	 */
	@AsTableField(isRecid = true)
	public GUID RECID;
	
	/**
	 * 操作者ID，被分配角色者
	 */
	@AsTableField(isRequired = true)
	public GUID actorID;
	
	/**
	 * 角色ID
	 */
	@AsTableField(isRequired = true)
	public GUID roleID;
	
	/**
	 * 优先级
	 */
	@AsTableField(isRequired = true)
	public int priority;
	 
}
