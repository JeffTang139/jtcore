package org.eclipse.jt.core.impl;

import java.util.List;

class OraclePrintBuffer extends OracleExprBuffer {
	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("DBMS_OUTPUT.PUT_LINE(");
		super.writeTo(sql, args);
		sql.append(')').append(';');
	}
}
