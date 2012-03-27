package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ACL实体表定义<br>
 * 依次按操作者ID、组织机构ID、资源类别ID和资源ID索引。
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthACLEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthACL extends EntityTableDeclarator<CoreAuthACLEntity> {

	/**
	 * 操作者ID字段定义
	 */
	public final TableFieldDefine f_actorID;
	
	/**
	 * 组织机构ID字段定义
	 */
	public final TableFieldDefine f_orgID;
		
	/**
	 * 资源类别ID字段定义
	 */
	public final TableFieldDefine f_resCategoryID;
		
	/**
	 * 资源ID字段定义
	 */
	public final TableFieldDefine f_resourceID;
		
	/**
	 * 授权编码字段定义
	 */
	public final TableFieldDefine f_authorityCode;
		
	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthACL";
	
	public TD_CoreAuthACL() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table.getFields();
		this.f_actorID = fields.get("actorID");
		this.f_orgID = fields.get("orgID");
		this.f_resCategoryID = fields.get("resCategoryID");
		this.f_resourceID = fields.get("resourceID");
		this.f_authorityCode = fields.get("authorityCode");
		this.table.newIndex("IDX_CoreAuthACL_ActorID", this.f_actorID);
		this.table.newIndex("IDX_CoreAuthACL_OrgID", this.f_orgID);
		this.table.newIndex("IDX_CoreAuthACL_ResCategoryID", this.f_resCategoryID);
		this.table.newIndex("IDX_CoreAuthACL_ResourceID", this.f_resourceID);
	}

}
