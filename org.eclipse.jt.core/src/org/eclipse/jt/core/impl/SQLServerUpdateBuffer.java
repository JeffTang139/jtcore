package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SQLServerUpdateBuffer extends SqlCommandBuffer implements
		ISqlUpdateBuffer {
	static class SQLServerUpdateValueBuffer extends SQLServerExprBuffer {
		final String field;

		public SQLServerUpdateValueBuffer(String field) {
			this.field = field;
		}
	}

	final SQLServerTableRefBuffer table;
	final ArrayList<SQLServerUpdateValueBuffer> values = new ArrayList<SQLServerUpdateValueBuffer>();
	SQLServerExprBuffer where;
	String cursor;

	public SQLServerUpdateBuffer(SQLServerSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = new SQLServerTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer newValue(String field) {
		SQLServerUpdateValueBuffer val = new SQLServerUpdateValueBuffer(
				SQLServerExprBuffer.quote(field));
		this.values.add(val);
		return val;
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
		sql.append("update ").append(this.table.table).append(" set ");
		Iterator<SQLServerUpdateValueBuffer> iter = this.values.iterator();
		SQLServerUpdateValueBuffer val = iter.next();
		sql.append(val.field).append('=');
		val.writeTo(sql, args);
		while (iter.hasNext()) {
			val = iter.next();
			sql.append(',').append(val.field).append('=');
			val.writeTo(sql, args);
		}
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
