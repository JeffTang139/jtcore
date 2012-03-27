package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MetaElementType;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ���Ŀ��Ԫ���ݴ洢
 * 
 * @author Jeff Tang
 * 
 */
final class TD_CoreMetaData extends EntityTableDeclarator<CoreMetaData> {
	/**
	 * ���<br>
	 * 
	 * @see MetaElementType
	 */
	public final TableFieldDefine f_kind;
	/**
	 * ����
	 */
	public final TableFieldDefine f_name;
	/**
	 * space
	 */
	public final TableFieldDefine f_space;
	/**
	 * ԭ����XML�ı����Ѿ�����������Ϊ��
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
