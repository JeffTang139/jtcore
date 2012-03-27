package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ����������֯����ӳ���ϵʵ���<br>
 * ��������ID������
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthUOMEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthUOM extends EntityTableDeclarator<CoreAuthUOMEntity> {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * ��������
	 */
	public static final String NAME = "Core_AuthUOM";

	public TD_CoreAuthUOM() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table
				.getFields();
		this.f_actorID = fields.get("actorID");
		this.f_orgID = fields.get("orgID");
		this.table.newIndex("IDX_CoreAuthUOM_ActorID", this.f_actorID);
	}

}
