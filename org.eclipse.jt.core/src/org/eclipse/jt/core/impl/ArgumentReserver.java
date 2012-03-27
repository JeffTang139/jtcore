package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

final class ArgumentReserver extends StrongRefParameter {

	final StructFieldDefineImpl arg;
	final DataType type;

	ArgumentReserver(StructFieldDefineImpl arg, DataType type) {
		this.arg = arg;
		this.type = type;
	}

}