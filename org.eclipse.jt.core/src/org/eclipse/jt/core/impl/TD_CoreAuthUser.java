package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * �û�ʵ�����
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthUserEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthUser extends EntityTableDeclarator<CoreAuthUserEntity> {

	/**
	 * �û������ֶζ���
	 */
	public final TableFieldDefine f_name;
	
	/**
	 * �û������ֶζ���
	 */
	public final TableFieldDefine f_title;
	
	/**
	 * �û�״̬�ֶζ���
	 */
	public final TableFieldDefine f_state;
	
	/**
	 * �û�������Ϣ�ֶζ���
	 */
	public final TableFieldDefine f_description;
	
	/**
	 * �û���½�����ֶζ���
	 */
	public final TableFieldDefine f_password;
	
	/**
	 * �û����ȼ���Ϣ�ֶζ���
	 */
	public final TableFieldDefine f_priorityInfo;
	
	/**
	 * ��������
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
