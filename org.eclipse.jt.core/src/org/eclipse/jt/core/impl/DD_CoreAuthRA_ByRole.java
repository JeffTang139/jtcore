package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据角色字段删除角色分配项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthRA_ByRole extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthRA_ByRole";

	public DD_CoreAuthRA_ByRole(TD_CoreAuthRA td_CoreAuthRA) {
		super(NAME, td_CoreAuthRA);
		final ArgumentDeclare arg = this.statement
				.newArgument(td_CoreAuthRA.f_roleID);
		this.statement.setCondition(this.statement
				.expOf(td_CoreAuthRA.f_roleID).xEq(arg));
	}

}
