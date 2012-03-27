package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class DB2ConditionBuffer extends SqlBuffer implements ISqlConditionBuffer {

	final ISqlSegmentBuffer scope;
	ArrayList<DB2ExprBuffer> when = new ArrayList<DB2ExprBuffer>();
	ArrayList<DB2SegmentBuffer> then = new ArrayList<DB2SegmentBuffer>();
	DB2SegmentBuffer els;

	DB2ConditionBuffer(ISqlSegmentBuffer scope) {
		this.scope = scope;
	}

	public final DB2ExprBuffer newWhen() {
		DB2ExprBuffer w = new DB2ExprBuffer();
		this.when.add(w);
		return w;
	}

	public final DB2SegmentBuffer newThen() {
		DB2SegmentBuffer t = new DB2SegmentBuffer(this.scope);
		this.then.add(t);
		return t;
	}

	public final DB2SegmentBuffer elseThen() {
		return this.els = new DB2SegmentBuffer(this.scope);
	}

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		for (int i = 0, c = this.when.size(); i < c; i++) {
			if ((this.when.get(i).useRowcount)) {
				sql.append("get diagnostics \"").append(
						DB2ExprBuffer.ROWCOUNT_VAR);
				sql.append("\"=row_count;");
				break;
			}
		}
		sql.append("if ");
		this.when.get(0).writeTo(sql, args);
		sql.append(" then ");
		this.then.get(0).writeTo(sql, args);
		for (int i = 1, c = this.when.size(); i < c; i++) {
			sql.append(" elseif ");
			this.when.get(i).writeTo(sql, args);
			sql.append(" then ");
			this.then.get(i).writeTo(sql, args);
		}
		if (this.els != null) {
			sql.append(" else ");
			this.els.writeTo(sql, args);
		}
		sql.append(" end if");
	}

}
