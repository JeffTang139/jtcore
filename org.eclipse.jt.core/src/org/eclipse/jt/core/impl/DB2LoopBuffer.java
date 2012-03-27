package org.eclipse.jt.core.impl;

import java.util.List;

public class DB2LoopBuffer extends DB2SegmentBuffer implements ISqlLoopBuffer {

	DB2LoopBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}

	public final ISqlExprBuffer when() {
		return this.when;
	}

	final DB2ExprBuffer when = new DB2ExprBuffer();

	@Override
	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("while ");
		this.when.writeTo(sql, args);
		sql.append(" do ");
		this.writeStatement(sql, args);
		sql.append("end while");
	}

}
