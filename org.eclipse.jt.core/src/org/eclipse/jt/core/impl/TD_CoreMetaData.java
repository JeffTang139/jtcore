package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * 核心框架元数据存储
 * 
 * @author Jeff Tang
 * 
 */
final class TD_CoreMetaData extends EntityTableDeclarator<CoreMetaData> {
	/**
	 * 类别<br>
	 * 
	 * @see MetaElementType
	 */
	public final TableFieldDefine f_kind;
	/**
	 * 名称
	 */
	public final TableFieldDefine f_name;
	/**
	 * space
	 */
	public final TableFieldDefine f_space;
	/**
	 * 原来的XML文本，已经废弃，可能为空
	 */
	public final TableFieldDefine f_xml;

	private TD_CoreMetaData() {
		super("core_metadata");
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table
		        .getFields();
		this.f_kind = fields.get("kind");
		this.f_name = fields.get("name");
		this.f_space = fields.get("space");
		this.f_xml = fields.get("xml");
		this.orm.newOrderBy(this.f_kind);
	}
}
