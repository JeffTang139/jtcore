package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 角色分配实体表定义<br>
 * 依次按操作者ID和优先级索引。
 * 
 * @see org.eclipse.jt.core.impl.CoreAuthRAEntity
 * @author Jeff Tang 2009-12
 */
final class TD_CoreAuthRA extends EntityTableDeclarator<CoreAuthRAEntity> {

	/**
	 * 操作者ID字段定义
	 */
	public final TableFieldDefine f_actorID;
	
	/**
	 * 角色ID字段定义
	 */
	public final TableFieldDefine f_roleID;
	
	/**
	 * 优先级字段定义
	 */
	public final TableFieldDefine f_priority;
	
	/**
	 * 表名定义
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
