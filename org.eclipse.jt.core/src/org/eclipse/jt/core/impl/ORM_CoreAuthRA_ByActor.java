package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.query.ORMDeclarator;

/**
 * 根据访问者字段查找角色分配项的语句定义
 * 
 * @author Jeff Tang 2009-12
 */
final class ORM_CoreAuthRA_ByActor extends ORMDeclarator<CoreAuthRAEntity> {

	public static final String NAME = "ORM_CoreAuthRA_ByActor";

	public ORM_CoreAuthRA_ByActor(TD_CoreAuthRA td_CoreAuthRA) {
		super(NAME);
		this.orm.newReference(td_CoreAuthRA);
		final ArgumentDeclare arg = this.orm
				.newArgument(td_CoreAuthRA.f_actorID);
		this.orm.setCondition(this.orm.expOf(td_CoreAuthRA.f_actorID).xEq(arg));
		this.orm.newOrderBy(td_CoreAuthRA.f_priority);
	}

}
