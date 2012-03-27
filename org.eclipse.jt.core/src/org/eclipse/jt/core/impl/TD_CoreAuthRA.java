package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ��ɫ����ʵ�����<br>
 * ���ΰ�������ID�����ȼ�������
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthRAEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthRA extends EntityTableDeclarator<CoreAuthRAEntity> {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;
	
	/**
	 * ��ɫID�ֶζ���
	 */
	public final TableFieldDefine f_roleID;
	
	/**
	 * ���ȼ��ֶζ���
	 */
	public final TableFieldDefine f_priority;
	
	/**
	 * ��������
	 */
	public static final String NAME = "Core_AuthRA";
	
	public TD_CoreAuthRA() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table.getFields();
		this.f_actorID = fields.get("actorID");
		this.f_roleID = fields.get("roleID");
		this.f_priority = fields.get("priority");
		this.table.newIndex("IDX_CoreAuthRA_ActorID", this.f_actorID);
		this.table.newIndex("IDX_CoreAuthRA_Priority", this.f_priority);
	}

}
