package org.eclipse.jt.core.impl;

import java.util.List;

class OracleReturnBuffer extends OracleExprBuffer {
	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("return ");
		super.writeTo(sql, args);
		sql.append(';');
	}
}
