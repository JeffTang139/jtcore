package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.TableFieldDeclare;
import org.eclipse.jt.core.def.table.TableFieldDefine;

public class TD_CoreSiteInfo extends EntityTableDeclarator<CoreSiteInfo> {
	/**
	 * 原来的XML文本，已经废弃，可能为空
	 */
	public final TableFieldDefine f_xml;

	public TD_CoreSiteInfo() {
		super("core_siteinfo");
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table
		        .getFields();
		this.f_xml = fields.get("xml");
	}

}
