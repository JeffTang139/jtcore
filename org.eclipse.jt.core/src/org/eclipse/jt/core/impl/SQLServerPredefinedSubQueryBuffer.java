package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerPredefinedSubQueryBuffer extends SQLServerSelectBuffer {
	final String alias;

	public SQLServerPredefinedSubQueryBuffer(String alias) {
		this.alias = alias;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.alias);
		sql.append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}
}
