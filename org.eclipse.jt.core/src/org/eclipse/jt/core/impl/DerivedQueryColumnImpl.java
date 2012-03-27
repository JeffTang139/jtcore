package org.eclipse.jt.core.impl;

final class DerivedQueryColumnImpl extends
		SelectColumnImpl<DerivedQueryImpl, DerivedQueryColumnImpl> {

	DerivedQueryColumnImpl(DerivedQueryImpl owner, String name, ValueExpr value) {
		super(owner, name, value);
	}

}
