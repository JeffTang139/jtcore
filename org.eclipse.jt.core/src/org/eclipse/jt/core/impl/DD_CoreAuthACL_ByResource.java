package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据资源项字段删除ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthACL_ByResource extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthACL_ByResource";

	public DD_CoreAuthACL_ByResource(TD_CoreAuthACL td_CoreAuthACL) {
		super(NAME, td_CoreAuthACL);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthACL.f_resCategoryID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthACL.f_resourceID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthACL.f_resCategoryID).xEq(arg1).and(
				this.statement.expOf(td_CoreAuthACL.f_resourceID).xEq(arg2)));
	}

}