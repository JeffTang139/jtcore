package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerDeleteBuffer extends SqlCommandBuffer implements
		ISqlDeleteBuffer {
	final SQLServerTableRefBuffer table;
	SQLServerExprBuffer where;
	String cursor;

	public SQLServerDeleteBuffer(SQLServerSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = new SQLServerTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new SQLServerExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("delete from ");
		sql.append(this.table.table);
		if (this.table.joins != null || this.table.alias != null
				&& !this.table.table.equals(this.table.alias)) {
			sql.append(" from ");
			this.table.writeTo(sql, args);
		}
		if (this.cursor != null) {
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		sql.append(';');
	}
}
