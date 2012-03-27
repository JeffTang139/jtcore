package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ACLʵ�����<br>
 * ���ΰ�������ID����֯����ID����Դ���ID����ԴID������
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthACLEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthACL extends EntityTableDeclarator<CoreAuthACLEntity> {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;
	
	/**
	 * ��֯����ID�ֶζ���
	 */
	public final TableFieldDefine f_orgID;
		
	/**
	 * ��Դ���ID�ֶζ���
	 */
	public final TableFieldDefine f_resCategoryID;
		
	/**
	 * ��ԴID�ֶζ���
	 */
	public final TableFieldDefine f_resourceID;
		
	/**
	 * ��Ȩ�����ֶζ���
	 */
	public final TableFieldDefine f_authorityCode;
		
	/**
	 * ��������
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
