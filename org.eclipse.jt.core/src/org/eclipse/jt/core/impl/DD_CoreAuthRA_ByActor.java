package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * 根据访问者字段删除角色分配项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthRA_ByActor extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthRA_ByActor";

	public DD_CoreAuthRA_ByActor(TD_CoreAuthRA td_CoreAuthRA) {
		super(NAME, td_CoreAuthRA);
		final ArgumentDeclare arg = this.statement
				.newArgument(td_CoreAuthRA.f_actorID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthRA.f_actorID).xEq(arg));
	}

}
