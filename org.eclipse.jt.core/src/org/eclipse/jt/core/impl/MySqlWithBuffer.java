package org.eclipse.jt.core.impl;

import java.util.List;

final class MySqlWithBuffer extends MySqlSelectBuffer {

	final String name;

	MySqlWithBuffer(MySqlCommandBuffer command, String name) {
		super(command);
		this.name = name;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		super.writeSelectTo(sql, args);
	}
}
