package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jt.core.impl.SQLServerSelectBuffer.SQLServerOrderExprBuffer;


class SQLServerQueryBuffer extends SqlCommandBuffer implements ISqlQueryBuffer {
	ArrayList<SQLServerPredefinedSubQueryBuffer> with;
	final SQLServerSelectBuffer select = new SQLServerSelectBuffer();
	ArrayList<SQLServerOrderExprBuffer> order;
	SQLServerExprBuffer limit;
	SQLServerExprBuffer offset;

	public SQLServerQueryBuffer(SQLServerSegmentBuffer scope) {
		super(scope);
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	public ISqlSelectBuffer newWith(String alias) {
		if (this.with == null) {
			this.with = new ArrayList<SQLServerPredefinedSubQueryBuffer>();
		}
		SQLServerPredefinedSubQueryBuffer w = new SQLServerPredefinedSubQueryBuffer(
				SQLServerExprBuffer.quote(alias));
		this.with.add(w);
		return w;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<SQLServerOrderExprBuffer>();
		}
		SQLServerOrderExprBuffer expr = new SQLServerOrderExprBuffer(desc);
		this.order.add(expr);
		return expr;
	}

	public void newOrder(String column, boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<SQLServerOrderExprBuffer>();
		}
		SQLServerOrderExprBuffer expr = new SQLServerOrderExprBuffer(column,
				desc);
		this.order.add(expr);
	}

	public ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new SQLServerExprBuffer();
		}
		return this.limit;
	}

	public ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new SQLServerExprBuffer();
		}
		return this.offset;
	}

	private void writeOrder(SqlStringBuffer sql, List<ParameterReserver> args) {
		Iterator<SQLServerOrderExprBuffer> it = this.order.iterator();
		SQLServerOrderExprBuffer e = it.next();
		e.writeTo(sql, args);
		if (e.desc) {
			sql.append(" desc");
		}
		while (it.hasNext()) {
			e = it.next();
			sql.append(',');
			e.writeTo(sql, args);
			if (e.desc) {
				sql.append(" desc");
			}
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.with != null) {
			sql.append("with ");
			Iterator<SQLServerPredefinedSubQueryBuffer> iter = this.with
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
				if (this.select.union != null) {
					sql.append("select * from (select top ((");
					this.limit.writeTo(sql, args);
					sql.append(")+(");
					this.offset.writeTo(sql, args);
					sql.append(")-1) *,ROW_NUMBER() over "
							+ "(order by [$FC]) [$FR] from (");
					this.select.counting = true;
					this.select.writeTo(sql, args);
					sql.append(") [$T0]) [$T0] where [$FR]>=");
					this.offset.writeTo(sql, args);
					if (this.order != null) {
						sql.append(" order by ");
						this.writeOrder(sql, args);
					}
				} else {
					sql.append("select * from (select *,ROW_NUMBER() over "
							+ "(order by [$FC]) [$FR] from (");
					this.select.top = new ISqlBuffer() {
						public void writeTo(SqlStringBuffer sql,
								List<ParameterReserver> args) {
							sql.append("(");
							SQLServerQueryBuffer.this.limit.writeTo(sql, args);
							sql.append(")+(");
							SQLServerQueryBuffer.this.offset.writeTo(sql, args);
							sql.append(")-1");
						}
					};
					this.select.counting = true;
					this.select.writeTo(sql, args);
					if (this.order != null) {
						sql.append(" order by ");
						this.writeOrder(sql, args);
					}
					sql.append(") [$T0]) [$T0] where [$FR]>=");
					this.offset.writeTo(sql, args);
				}
			} else {
				if (this.select.union != null) {
					sql.append("select top (");
					this.limit.writeTo(sql, args);
					sql.append(") * from (");
					this.select.writeTo(sql, args);
					sql.append(") [$T0]");
					if (this.order != null) {
						sql.append(" order by ");
						this.writeOrder(sql, args);
					}
				} else {
					this.select.top = new ISqlBuffer() {
						public void writeTo(SqlStringBuffer sql,
								List<ParameterReserver> args) {
							SQLServerQueryBuffer.this.limit.writeTo(sql, args);
						}
					};
					this.select.writeTo(sql, args);
					if (this.order != null) {
						sql.append(" order by ");
						this.writeOrder(sql, args);
					}
				}
			}
		} else {
			this.select.writeTo(sql, args);
			if (this.order != null) {
				sql.append(" order by ");
				this.writeOrder(sql, args);
			}
		}
		sql.append(';');
	}
}
