package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据操作者，组织机构，资源类别，资源字段删除ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthAuthACL_OneRecord extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthAuthACL_OneRecord";

	public DD_CoreAuthAuthACL_OneRecord(TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(NAME, td_CoreAuthAuthACL);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_actorID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_orgID);
		final ArgumentDeclare arg3 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_resCategoryID);
		final ArgumentDeclare arg4 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_resourceID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthAuthACL.f_actorID).xEq(arg1).and(
				this.statement.expOf(td_CoreAuthAuthACL.f_orgID).xEq(arg2),
				this.statement.expOf(td_CoreAuthAuthACL.f_resCategoryID).xEq(arg3),
				this.statement.expOf(td_CoreAuthAuthACL.f_resourceID).xEq(arg4)));
	}

}
