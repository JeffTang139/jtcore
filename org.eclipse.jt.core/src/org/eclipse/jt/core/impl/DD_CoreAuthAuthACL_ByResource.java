package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据资源项字段删除ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthAuthACL_ByResource extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthAuthACL_ByResource";

	public DD_CoreAuthAuthACL_ByResource(TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(NAME, td_CoreAuthAuthACL);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_resCategoryID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_resourceID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthAuthACL.f_resCategoryID).xEq(arg1)
				.and(
						this.statement.expOf(td_CoreAuthAuthACL.f_resourceID)
								.xEq(arg2)));
	}
	
}