package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 用户实体表定义
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthUserEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthUser extends EntityTableDeclarator<CoreAuthUserEntity> {

	/**
	 * 用户名称字段定义
	 */
	public final TableFieldDefine f_name;
	
	/**
	 * 用户标题字段定义
	 */
	public final TableFieldDefine f_title;
	
	/**
	 * 用户状态字段定义
	 */
	public final TableFieldDefine f_state;
	
	/**
	 * 用户描述信息字段定义
	 */
	public final TableFieldDefine f_description;
	
	/**
	 * 用户登陆密码字段定义
	 */
	public final TableFieldDefine f_password;
	
	/**
	 * 用户优先级信息字段定义
	 */
	public final TableFieldDefine f_priorityInfo;
	
	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthUser";
	
	public TD_CoreAuthUser() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table.getFields();
		this.f_name = fields.get("name");
		this.f_title = fields.get("title");
		this.f_state = fields.get("state");
		this.f_description = fields.get("description");
		this.f_password = fields.get("password");
		this.f_priorityInfo = fields.get("priorityInfo");
		this.table.newIndex("IDX_CoreAuthUser_Name", this.f_name);
	}

}
