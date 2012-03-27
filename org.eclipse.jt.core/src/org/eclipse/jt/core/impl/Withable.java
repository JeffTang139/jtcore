package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.WithableDeclare;

interface Withable extends WithableDeclare {

	public NamedDefineContainerImpl<DerivedQueryImpl> getWiths();

	public DerivedQueryImpl newWith(String name);

}
