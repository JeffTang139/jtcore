package org.eclipse.jt.core.impl;

import java.util.List;

final class DB2WithBuffer extends DB2SelectBuffer {

	final String name;

	DB2WithBuffer(String name) {
		this.name = DB2ExprBuffer.quote(name);
	}

	@Override
	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.name).append(" as (");
		super.writeFullSelectTo(sql, args);
		sql.append(')');
	}
}
