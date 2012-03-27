package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jt.core.impl.OracleSelectBuffer.OracleOrderExprBuffer;


class OracleQueryBuffer extends SqlCommandBuffer implements ISqlQueryBuffer {
	ArrayList<OraclePredefinedSubQueryBuffer> with;
	final OracleSelectBuffer select = new OracleSelectBuffer();
	ArrayList<OracleOrderExprBuffer> order;
	OracleExprBuffer limit;
	OracleExprBuffer offset;

	public OracleQueryBuffer() {
		super(null);
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	public ISqlSelectBuffer newWith(String alias) {
		if (this.with == null) {
			this.with = new ArrayList<OraclePredefinedSubQueryBuffer>();
		}
		OraclePredefinedSubQueryBuffer w = new OraclePredefinedSubQueryBuffer(
				OracleExprBuffer.quote(alias));
		this.with.add(w);
		return w;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<OracleOrderExprBuffer>();
		}
		OracleOrderExprBuffer expr = new OracleOrderExprBuffer(desc);
		this.order.add(expr);
		return expr;
	}

	public void newOrder(String column, boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<OracleOrderExprBuffer>();
		}
		OracleOrderExprBuffer expr = new OracleOrderExprBuffer(column, desc);
		this.order.add(expr);
	}

	public ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new OracleExprBuffer();
		}
		return this.limit;
	}

	public ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new OracleExprBuffer();
		}
		return this.offset;
	}

	private void writeSelect(SqlStringBuffer sql, List<ParameterReserver> args) {
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<OracleOrderExprBuffer> it = this.order.iterator();
			OracleOrderExprBuffer e = it.next();
			e.writeTo(sql, args);
			if (e.desc) {
				sql.append(" desc nulls last");
			} else {
				sql.append(" asc nulls first");
			}
			while (it.hasNext()) {
				e = it.next();
				sql.append(',');
				e.writeTo(sql, args);
				if (e.desc) {
					sql.append(" desc nulls last");
				} else {
					sql.append(" asc nulls first");
				}
			}
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.with != null) {
			sql.append("with ");
			Iterator<OraclePredefinedSubQueryBuffer> iter = this.with
					.iterator();
			iter.next().writeTo(sql, args);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeTo(sql, args);
			}
			sql.append(' ');
		}
		if (this.limit != null) {
			if (this.offset != null) {
				sql.append("select * from (select \"$T0\".*,rownum \"$F\""
						+ " from (");
				this.writeSelect(sql, args);
				sql.append(") \"$T0\") where \"$F\">");
				this.offset.writeTo(sql, args);
				sql.append(" and rownum<=");
				this.limit.writeTo(sql, args);
			} else {
				sql.append("select * from (");
				this.writeSelect(sql, args);
				sql.append(") where rownum<=");
				this.limit.writeTo(sql, args);
			}
		} else {
			this.writeSelect(sql, args);
		}
	}
}
