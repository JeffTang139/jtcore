package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据资源类别字段删除ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthAuthACL_ByResCategory extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthAuthACL_ByResCategory";

	public DD_CoreAuthAuthACL_ByResCategory(TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(NAME, td_CoreAuthAuthACL);
		final ArgumentDeclare arg = this.statement
				.newArgument(td_CoreAuthAuthACL.f_resCategoryID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthAuthACL.f_resCategoryID).xEq(arg));
	}
	
}