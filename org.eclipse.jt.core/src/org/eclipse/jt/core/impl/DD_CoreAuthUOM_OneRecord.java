package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据操作者字段、组织机构字段删除用户与组织机构映射项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthUOM_OneRecord extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthUOM_OneRecord";

	public DD_CoreAuthUOM_OneRecord(TD_CoreAuthUOM td_CoreAuthUOM) {
		super(NAME, td_CoreAuthUOM);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthUOM.f_actorID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthUOM.f_orgID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthUOM.f_actorID).xEq(arg1).and(
				this.statement.expOf(td_CoreAuthUOM.f_orgID).xEq(arg2)));
	}

}
