package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.DeleteStatementDeclarator;

/**
 * ���ݷ������ֶ�ɾ���û�����֯����ӳ�������䶨��
 * 
 * @author Jeff Tang 2009-12
 */
final class DD_CoreAuthUOM_ByActor extends DeleteStatementDeclarator {

	public static final String NAME = "DD_CoreAuthUOM_ByActor";

	public DD_CoreAuthUOM_ByActor(TD_CoreAuthUOM td_CoreAuthUOM) {
		super(NAME, td_CoreAuthUOM);
		final ArgumentDeclare arg = this.statement
				.newArgument(td_CoreAuthUOM.f_actorID);
		this.statement.setCondition(this.statement.expOf(
				td_CoreAuthUOM.f_actorID).xEq(arg));
	}
	
}