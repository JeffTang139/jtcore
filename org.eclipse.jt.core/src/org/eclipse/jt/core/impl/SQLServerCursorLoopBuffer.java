package org.eclipse.jt.core.impl;

import java.util.Iterator;
import java.util.List;

class SQLServerCursorLoopBuffer extends SQLServerSegmentBuffer implements
		ISqlCursorLoopBuffer {
	SQLServerQueryBuffer query;
	final String cursor;

	public SQLServerCursorLoopBuffer(SQLServerSegmentBuffer scope, String cursor) {
		super(scope);
		this.cursor = cursor;
	}

	public ISqlQueryBuffer query() {
		if (this.query == null) {
			this.query = new SQLServerQueryBuffer(this);
		}
		return this.query;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("declare ").append(this.cursor).append(" cursor for ");
		this.query.writeTo(sql, args);
		if (this.vars != null) {
			writeDeclare(sql);
		}
		sql.append("open ").append(this.cursor).append(";fetch from ")
				.append(this.cursor);
		if (this.vars != null) {
			sql.append(" into ");
			Iterator<Variable> iter = this.vars.iterator();
			iter.next().writeRefTo(sql);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeRefTo(sql);
			}
		}
		sql.append(";while @@FETCH_STATUS=0 begin ");
		writeStmts(sql, args);
		sql.append("fetch from ").append(this.cursor);
		if (this.vars != null) {
			sql.append(" into ");
			Iterator<Variable> iter = this.vars.iterator();
			iter.next().writeRefTo(sql);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeRefTo(sql);
			}
		}
		sql.append(";end;close ").append(this.cursor).append(";deallocate ")
				.append(this.cursor).append(';');
	}
}
