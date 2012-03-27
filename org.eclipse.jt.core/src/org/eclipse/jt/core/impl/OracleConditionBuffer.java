package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class OracleConditionBuffer extends SqlBuffer implements ISqlConditionBuffer {
	final OracleSegmentBuffer scope;
	ArrayList<OracleExprBuffer> when = new ArrayList<OracleExprBuffer>();
	ArrayList<OracleSegmentBuffer> then = new ArrayList<OracleSegmentBuffer>();
	OracleSegmentBuffer elseThen;

	public OracleConditionBuffer(OracleSegmentBuffer scope) {
		this.scope = scope;
	}

	public ISqlExprBuffer newWhen() {
		OracleExprBuffer e = new OracleExprBuffer();
		this.when.add(e);
		return e;
	}

	public ISqlSegmentBuffer newThen() {
		OracleSegmentBuffer s = new OracleSegmentBuffer(this.scope);
		this.then.add(s);
		return s;
	}

	public ISqlSegmentBuffer elseThen() {
		if (this.elseThen == null) {
			this.elseThen = new OracleSegmentBuffer(this.scope);
		}
		return this.elseThen;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("if ");
		this.when.get(0).writeTo(sql, args);
		sql.append(" then ");
		this.then.get(0).writeTo(sql, args);
		for (int i = 1, c = this.when.size(); i < c; i++) {
			sql.append(" elsif ");
			this.when.get(i).writeTo(sql, args);
			sql.append(" then ");
			this.then.get(i).writeTo(sql, args);
		}
		if (this.elseThen != null) {
			sql.append(" else ");
			this.elseThen.writeTo(sql, args);
		}
		sql.append(" end if;");
	}
}
