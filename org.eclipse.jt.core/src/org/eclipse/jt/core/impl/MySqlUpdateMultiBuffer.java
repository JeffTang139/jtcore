package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class MySqlUpdateMultiBuffer extends MySqlCommandBuffer implements
		ISqlUpdateMultiBuffer {

	final MySqlTableRefBuffer target;

	static class MySqlUpdateMultiAssignBuffer extends MySqlExprBuffer {

		final String alias;
		final String field;

		public MySqlUpdateMultiAssignBuffer(MySqlUpdateMultiBuffer command,
				String alias, String field) {
			super(command);
			this.alias = quote(alias);
			this.field = quote(field);
		}

	}

	final ArrayList<MySqlUpdateMultiAssignBuffer> values = new ArrayList<MySqlUpdateMultiAssignBuffer>();
	MySqlExprBuffer where;

	MySqlUpdateMultiBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.target = new MySqlTableRefBuffer(this, table, alias);
	}

	public final MySqlTableRefBuffer target() {
		return this.target;
	}

	public final MySqlExprBuffer newValue(String alias, String field) {
		MySqlUpdateMultiAssignBuffer value = new MySqlUpdateMultiAssignBuffer(
				this, alias, field);
		this.values.add(value);
		return value;
	}

	public final MySqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MySqlExprBuffer(this);
		}
		return this.where;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("update ");
		this.target.writeTo(sql, args);
		sql.append(" set ");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			MySqlUpdateMultiAssignBuffer value = this.values.get(i);
			sql.append(value.alias);
			sql.append('.');
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
