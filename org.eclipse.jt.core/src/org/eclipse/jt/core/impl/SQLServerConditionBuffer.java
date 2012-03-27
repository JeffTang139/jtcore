package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class SQLServerConditionBuffer extends SqlBuffer implements ISqlConditionBuffer {
	final SQLServerSegmentBuffer scope;
	ArrayList<SQLServerExprBuffer> when = new ArrayList<SQLServerExprBuffer>();
	ArrayList<SQLServerSegmentBuffer> then = new ArrayList<SQLServerSegmentBuffer>();
	SQLServerSegmentBuffer elseThen;

	public SQLServerConditionBuffer(SQLServerSegmentBuffer scope) {
		this.scope = scope;
	}

	public ISqlExprBuffer newWhen() {
		SQLServerExprBuffer e = new SQLServerExprBuffer();
		this.when.add(e);
		return e;
	}

	public ISqlSegmentBuffer newThen() {
		SQLServerSegmentBuffer s = new SQLServerSegmentBuffer(this.scope);
		this.then.add(s);
		return s;
	}

	public ISqlSegmentBuffer elseThen() {
		if (this.elseThen == null) {
			this.elseThen = new SQLServerSegmentBuffer(this.scope);
		}
		return this.elseThen;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("if ");
		this.when.get(0).writeTo(sql, args);
		sql.append(' ');
		SQLServerSegmentBuffer seg = this.then.get(0);
		if (seg.stmts.size() > 1) {
			sql.append("begin ");
			seg.writeTo(sql, args);
			sql.append(" end;");
		} else {
			this.then.get(0).writeTo(sql, args);
		}
		for (int i = 1, c = this.when.size(); i < c; i++) {
			sql.append(" else if ");
			this.when.get(i).writeTo(sql, args);
			sql.append(' ');
			seg = this.then.get(i);
			if (seg.stmts.size() > 1) {
				sql.append("begin ");
				this.then.get(i).writeTo(sql, args);
				sql.append(" end;");
			} else {
				this.then.get(i).writeTo(sql, args);
			}
		}
		if (this.elseThen != null) {
			sql.append(" else ");
			if (this.elseThen.stmts.size() > 1) {
				sql.append("begin ");
				this.elseThen.writeTo(sql, args);
				sql.append(" end;");
			} else {
				this.elseThen.writeTo(sql, args);
			}
		}
	}
}
