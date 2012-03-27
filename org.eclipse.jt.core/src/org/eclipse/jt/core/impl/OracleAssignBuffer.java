package org.eclipse.jt.core.impl;

import java.util.List;

class OracleAssignBuffer extends OracleExprBuffer {
	final String var;

	public OracleAssignBuffer(String var) {
		this.var = var;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.var).append(':').append('=');
		super.writeTo(sql, args);
		sql.append(';');
	}
}
