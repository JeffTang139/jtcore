package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据访问者和组织机构字段删除ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthAuthACL_ByActorAndOrg extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthAuthACL_ByActorAndOrg";

	public DD_CoreAuthAuthACL_ByActorAndOrg(TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(NAME, td_CoreAuthAuthACL);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_actorID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthAuthACL.f_orgID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthAuthACL.f_actorID).xEq(arg1).and(
				this.statement.expOf(td_CoreAuthAuthACL.f_orgID).xEq(arg2)));
	}

}
