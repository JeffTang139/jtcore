package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerPrintBuffer extends SQLServerExprBuffer {
	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("print ");
		super.writeTo(sql, args);
		sql.append(';');
	}
}
