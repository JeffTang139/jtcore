package org.eclipse.jt.core.impl;

import java.util.List;

class OraclePredefinedSubQueryBuffer extends OracleSelectBuffer {
	final String alias;

	public OraclePredefinedSubQueryBuffer(String alias) {
		this.alias = alias;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append(this.alias).append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}
}
