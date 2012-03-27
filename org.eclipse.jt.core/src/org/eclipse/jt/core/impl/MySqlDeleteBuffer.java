package org.eclipse.jt.core.impl;

import java.util.List;

class MySqlDeleteBuffer extends MySqlCommandBuffer implements ISqlDeleteBuffer {

	final MySqlTableRefBuffer table;
	MySqlExprBuffer where;

	MySqlDeleteBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.table = new MySqlTableRefBuffer(this, table, alias);
	}

	public final MySqlTableRefBuffer target() {
		return this.table;
	}

	public final ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MySqlExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("delete from ");
		sql.append(this.table.alias);
		sql.append(" using ");
		this.table.writeTo(sql, args);
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}
