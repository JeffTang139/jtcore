package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 访问者与组织机构映射关系实体表<br>
 * 按访问者ID索引。
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthUOMEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthUOM extends EntityTableDeclarator<CoreAuthUOMEntity> {

	/**
	 * 访问者ID字段定义
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * 访问者ID字段定义
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * 表名定义
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
