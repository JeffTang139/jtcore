package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class MySqlUpdateBuffer extends MySqlCommandBuffer implements ISqlUpdateBuffer {

	static class MySqlUpdateAssignBuffer extends MySqlExprBuffer {

		final String field;

		public MySqlUpdateAssignBuffer(MySqlCommandBuffer command, String field) {
			super(command);
			this.field = quote(field);
		}
	}

	final MySqlTableRefBuffer target;

	final ArrayList<MySqlUpdateAssignBuffer> values = new ArrayList<MySqlUpdateAssignBuffer>();

	MySqlExprBuffer where;

	MySqlUpdateBuffer(ISqlSegmentBuffer scope, String table, String alias,
			boolean assignFromSlaveTable) {
		super(scope);
		this.target = new MySqlTableRefBuffer(this, table, alias);
	}

	public final MySqlTableRefBuffer target() {
		return this.target;
	}

	public MySqlExprBuffer newValue(String field) {
		MySqlUpdateAssignBuffer value = new MySqlUpdateAssignBuffer(this, field);
		this.values.add(value);
		return value;
	}

	public MySqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MySqlExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("update ");
		this.target.writeTo(sql, args);
		sql.append(" set ");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.target.alias);
			sql.append('.');
			MySqlUpdateAssignBuffer value = this.values.get(i);
			sql.append(value.field);
			sql.append('=');
			value.writeTo(sql, args);
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}
