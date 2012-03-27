package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.ORMDeclarator;

/**
 * 根据访问者字段、组织机构字段查找ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class ORM_CoreAuthACL_ByActorAndOrg extends
		ORMDeclarator<CoreAuthACLEntity> {

	public static final String NAME = "ORM_CoreAuthACL_ByActorAndOrg";

	public ORM_CoreAuthACL_ByActorAndOrg(TD_CoreAuthACL td_CoreAuthACL) {
		super(NAME);
		this.orm.newReference(td_CoreAuthACL);
		final ArgumentDeclare arg1 = this.orm
				.newArgument(td_CoreAuthACL.f_actorID);
		final ArgumentDeclare arg2 = this.orm
				.newArgument(td_CoreAuthACL.f_orgID);
		this.orm.setCondition(this.orm.expOf(td_CoreAuthACL.f_actorID)
				.xEq(arg1)
				.and(this.orm.expOf(td_CoreAuthACL.f_orgID).xEq(arg2)));
	}

}
