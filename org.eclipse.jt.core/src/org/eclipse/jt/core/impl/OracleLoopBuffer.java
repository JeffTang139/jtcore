package org.eclipse.jt.core.impl;

import java.util.List;

class OracleLoopBuffer extends OracleSegmentBuffer implements ISqlLoopBuffer {
	OracleExprBuffer when;

	public OracleLoopBuffer(OracleSegmentBuffer scope) {
		super(scope);
	}

	public ISqlExprBuffer when() {
		if (this.when == null) {
			this.when = new OracleExprBuffer();
		}
		return this.when;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.vars != null) {
			writeDeclare(sql);
			sql.append("begin ");
		}
		if (this.when != null) {
			sql.append("while ");
			this.when.writeTo(sql, args);
			sql.append(' ');
		}
		sql.append("loop ");
		writeStmts(sql, args);
		sql.append(" end loop;");
		if (this.vars != null) {
			sql.append("end;");
		}
	}
}
