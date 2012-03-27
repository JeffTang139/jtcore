package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerAssignBuffer extends SQLServerExprBuffer {
	final String var;

	public SQLServerAssignBuffer(String var) {
		this.var = var;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("set ").append('@').append(this.var).append('=');
		super.writeTo(sql, args);
		sql.append(';');
	}
}
