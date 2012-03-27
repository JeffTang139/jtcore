package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.ORMDeclarator;

/**
 * 根据访问者字段、组织机构字段查找ACL项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class ORM_CoreAuthAuthACL_ByActorAndOrg extends
		ORMDeclarator<CoreAuthACLEntity> {

	public static final String NAME = "ORM_CoreAuthAuthACL_ByActorAndOrg";

	public ORM_CoreAuthAuthACL_ByActorAndOrg(TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(NAME);
		this.orm.newReference(td_CoreAuthAuthACL);
		final ArgumentDeclare arg1 = this.orm
				.newArgument(td_CoreAuthAuthACL.f_actorID);
		final ArgumentDeclare arg2 = this.orm
				.newArgument(td_CoreAuthAuthACL.f_orgID);
		this.orm.setCondition(this.orm.expOf(td_CoreAuthAuthACL.f_actorID)
				.xEq(arg1)
				.and(this.orm.expOf(td_CoreAuthAuthACL.f_orgID).xEq(arg2)));
	}

}
