package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class OracleSelectIntoBuffer implements ISqlSelectIntoBuffer {
	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<OracleSelectColumnBuffer> columns = new ArrayList<OracleSelectColumnBuffer>();
	OracleExprBuffer where;

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	public ISqlTableRefBuffer newTable(String table, String alias) {
		ISqlTableRefBuffer t = new OracleTableRefBuffer(table, alias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newSubQuery(String alias) {
		ISqlQueryRefBuffer q = new OracleSubQueryRefBuffer(alias);
		this.source.add(q);
		return q;
	}

	public ISqlExprBuffer newColumn(String var) {
		OracleSelectColumnBuffer expr = new OracleSelectColumnBuffer(var);
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
		Iterator<OracleSelectColumnBuffer> iter = this.columns.iterator();
		iter.next().writeTo(sql, args);
		while (iter.hasNext()) {
			sql.append(',');
			iter.next().writeTo(sql, args);
		}
		sql.append(" into ");
		iter = this.columns.iterator();
		sql.append(iter.next().alias);
		while (iter.hasNext()) {
			sql.append(',');
			sql.append(iter.next().alias);
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
