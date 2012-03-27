package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * ���ݷ����ߺ���֯�����ֶ�ɾ��ACL�����䶨��
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthACL_ByActorAndOrg extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthACL_ByActorAndOrg";

	public DD_CoreAuthACL_ByActorAndOrg(TD_CoreAuthACL td_CoreAuthACL) {
		super(NAME, td_CoreAuthACL);
		final ArgumentDeclare arg1 = this.statement
				.newArgument(td_CoreAuthACL.f_actorID);
		final ArgumentDeclare arg2 = this.statement
				.newArgument(td_CoreAuthACL.f_orgID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthACL.f_actorID).xEq(arg1).and(
				this.statement.expOf(td_CoreAuthACL.f_orgID).xEq(arg2)));
	}

}
