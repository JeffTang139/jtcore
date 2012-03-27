package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SQLServerSelectIntoBuffer implements ISqlSelectIntoBuffer {
	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<SQLServerSelectColumnBuffer> columns = new ArrayList<SQLServerSelectColumnBuffer>();
	OracleExprBuffer where;

	static final String quote(String name) {
		return "[" + name + "]";
	}

	public ISqlTableRefBuffer newTable(String table, String alias) {
		ISqlTableRefBuffer t = new SQLServerTableRefBuffer(table, quote(alias));
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newSubQuery(String alias) {
		ISqlQueryRefBuffer q = new OracleSubQueryRefBuffer(alias);
		this.source.add(q);
		return q;
	}

	public ISqlExprBuffer newColumn(String var) {
		SQLServerSelectColumnBuffer expr = new SQLServerSelectColumnBuffer("@"
				+ var);
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new OracleExprBuffer();
		}
		return this.where;
	}

	private void writeColumns(SqlStringBuffer sql, List<ParameterReserver> args) {
		Iterator<SQLServerSelectColumnBuffer> iter = this.columns.iterator();
		SQLServerSelectColumnBuffer c = iter.next();
		sql.append(c.alias).append('=');
		c.writeTo(sql, args);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',').append(c.alias).append('=');
			c.writeTo(sql, args);
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("select ");
		this.writeColumns(sql, args);
		sql.append(" from ");
		Iterator<ISqlRelationRefBuffer> iter = this.source.iterator();
		iter.next().writeTo(sql, args);
		while (iter.hasNext()) {
			sql.append(',');
			iter.next().writeTo(sql, args);
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}
}
