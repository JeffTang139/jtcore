package org.eclipse.jt.core.impl;

import java.util.List;

final class DB2OrderExprBuffer extends DB2ExprBuffer {

	final boolean desc;

	DB2OrderExprBuffer(boolean desc) {
		this.desc = desc;
	}

	DB2OrderExprBuffer(String column, boolean desc) {
		this.push(quote(column));
		this.desc = desc;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		super.writeTo(sql, args);
		if (this.desc) {
			sql.append(" desc");
		}
	}
}