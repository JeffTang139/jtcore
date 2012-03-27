package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationRefDomainDeclare;

interface RelationRefDomain extends RelationRefDomainDeclare {

	RelationRefDomain getDomain();

	RelationRef findRelationRef(String name);

	RelationRef getRelationRef(String name);

	RelationRef findRelationRefRecursively(String name);

	RelationRef getRelationRefRecursively(String name);

	DerivedQueryImpl getWith(String name);

}
