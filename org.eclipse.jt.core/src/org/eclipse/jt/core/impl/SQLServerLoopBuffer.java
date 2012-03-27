package org.eclipse.jt.core.impl;

import java.util.List;

class SQLServerLoopBuffer extends SQLServerSegmentBuffer implements
		ISqlLoopBuffer {
	SQLServerExprBuffer when;

	public SQLServerLoopBuffer(SQLServerSegmentBuffer scope) {
		super(scope);
	}

	public ISqlExprBuffer when() {
		if (this.when == null) {
			this.when = new SQLServerExprBuffer();
		}
		return this.when;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.vars != null) {
			writeDeclare(sql);
		}
		if (this.when != null) {
			sql.append("while ");
			this.when.writeTo(sql, args);
			sql.append(' ');
		} else {
			sql.append("while 1=1 ");
		}
		if (this.stmts.size() > 1) {
			sql.append("begin ");
			writeStmts(sql, args);
			sql.append(" end ");
		} else {
			writeStmts(sql, args);
		}
	}
}
