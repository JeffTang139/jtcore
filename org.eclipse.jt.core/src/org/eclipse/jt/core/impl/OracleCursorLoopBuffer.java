package org.eclipse.jt.core.impl;

import java.util.Iterator;
import java.util.List;

class OracleCursorLoopBuffer extends OracleSegmentBuffer implements
		ISqlCursorLoopBuffer {
	OracleQueryBuffer query;
	final String cursor;
	final boolean forUpdate;

	public OracleCursorLoopBuffer(OracleSegmentBuffer scope, String cursor,
			boolean forUpdate) {
		super(scope);
		this.cursor = cursor;
		this.forUpdate = forUpdate;
	}

	public ISqlQueryBuffer query() {
		if (this.query == null) {
			this.query = new OracleQueryBuffer();
		}
		return this.query;
	}

	private void writeFetch(SqlStringBuffer sql) {
		sql.append("fetch ").append(this.cursor);
		if (this.vars != null) {
			Iterator<Variable> iter = this.vars.iterator();
			sql.append(" into ").append(iter.next().name);
			while (iter.hasNext()) {
				sql.append(',').append(iter.next().name);
			}
		}
		sql.append(';');
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("declare cursor ").append(this.cursor).append(" is ");
		this.query.writeTo(sql, args);
		if (this.forUpdate) {
			sql.append(" for update");
		}
		sql.append(';');
		if (this.vars != null) {
			for (Variable var : this.vars) {
				var.writeTo(sql);
				sql.append(';');
			}
		}
		sql.append("begin ");
		sql.append("open ").append(this.cursor).append(";loop ");
		writeFetch(sql);
		sql.append("exit when ").append(this.cursor).append("%NOTFOUND;");
		writeStmts(sql, args);
		sql.append(" end loop;close ").append(this.cursor).append(";end;");
	}
}
