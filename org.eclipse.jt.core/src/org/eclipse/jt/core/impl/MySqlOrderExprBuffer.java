package org.eclipse.jt.core.impl;

import java.util.List;

final class MySqlOrderExprBuffer extends MySqlExprBuffer {

	final boolean desc;

	MySqlOrderExprBuffer(MySqlCommandBuffer command, boolean desc) {
		super(command);
		this.desc = desc;
	}

	MySqlOrderExprBuffer(MySqlCommandBuffer command, String column, boolean desc) {
		super(command);
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
