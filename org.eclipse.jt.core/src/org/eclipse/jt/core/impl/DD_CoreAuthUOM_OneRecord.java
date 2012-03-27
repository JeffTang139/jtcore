package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * ���ݲ������ֶΡ���֯�����ֶ�ɾ���û�����֯����ӳ�������䶨��
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
