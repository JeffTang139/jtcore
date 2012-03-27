package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 角色实体表定义
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthRoleEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthRole extends EntityTableDeclarator<CoreAuthRoleEntity> {

	/**
	 * 角色名称字段定义
	 */
	public final TableFieldDefine f_name;

	/**
	 * 角色标题字段定义
	 */
	public final TableFieldDefine f_title;

	/**
	 * 角色状态字段定义
	 */
	public final TableFieldDefine f_state;

	/**
	 * 角色描述信息段定义
	 */
	public final TableFieldDefine f_description;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthRole";

	public TD_CoreAuthRole() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table.getFields();
		this.f_name = fields.get("name");
		this.f_title = fields.get("title");
		this.f_state = fields.get("state");
		this.f_description = fields.get("description");
		this.table.newIndex("IDX_CoreAuthRole_Name", this.f_name);
	}

}
