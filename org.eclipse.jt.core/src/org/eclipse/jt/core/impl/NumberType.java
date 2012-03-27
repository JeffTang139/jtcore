package org.eclipse.jt.core.impl;

abstract class NumberType extends DataTypeBase {

	NumberType(Class<?> javaClass) {
		super(javaClass);
	}

	@Override
	public final boolean isNumber() {
		return true;
	}

	@Override
	public boolean isDBType() {
		return true;
	}
}
