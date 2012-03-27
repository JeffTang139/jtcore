package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.ORMDeclarator;

final class ORM_CoreAuthUOM_OrderByActor extends ORMDeclarator<CoreAuthUOMEntity> {

	public static final String NAME = "ORM_CoreAuthUOM_OrderByActor";

	public ORM_CoreAuthUOM_OrderByActor(TD_CoreAuthUOM td_CoreAuthUOM) {
		super(NAME);
		this.orm.newReference(td_CoreAuthUOM);
		this.orm.newOrderBy(td_CoreAuthUOM.f_actorID);
	}
	
}
