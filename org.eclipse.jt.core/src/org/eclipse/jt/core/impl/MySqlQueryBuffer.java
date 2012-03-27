package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class MySqlQueryBuffer extends MySqlCommandBuffer implements
		ISqlQueryBuffer {

	MySqlQueryBuffer() {
		super(null);
	}

	public final MySqlWithBuffer newWith(String alias) {
		if (this.withs == null) {
			this.withs = new ArrayList<MySqlWithBuffer>();
		}
		MySqlWithBuffer w = new MySqlWithBuffer(this,
				MySqlExprBuffer.quote(alias));
		this.withs.add(w);
		return w;
	}

	final MySqlWithBuffer getWith(String name) {
		if (this.withs == null) {
			throw new UnsupportedOperationException();
		}
		final String e = MySqlExprBuffer.quote(name);
		for (int i = 0, c = this.withs.size(); i < c; i++) {
			MySqlWithBuffer with = this.withs.get(i);
			if (with.name.equals(e)) {
				return with;
			}
		}
		throw new UnsupportedOperationException();
	}

	public final MySqlSelectBuffer select() {
		return this.select;
	}

	public final MySqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new MySqlExprBuffer(this);
		}
		return this.limit;
	}

	public final MySqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new MySqlExprBuffer(this);
		}
		return this.offset;
	}

	public final MySqlOrderExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MySqlOrderExprBuffer>();
		}
		MySqlOrderExprBuffer o = new MySqlOrderExprBuffer(this, desc);
		this.orders.add(o);
		return o;
	}

	public final void newOrder(String column, boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MySqlOrderExprBuffer>();
		}
		MySqlOrderExprBuffer o = new MySqlOrderExprBuffer(this, column, desc);
		this.orders.add(o);
	}

	ArrayList<MySqlWithBuffer> withs;
	final MySqlSelectBuffer select = new MySqlSelectBuffer(this);
	ArrayList<MySqlOrderExprBuffer> orders;
	MySqlExprBuffer limit;
	MySqlExprBuffer offset;

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.select.unions == null || this.limit == null) {
			this.select.writeTo(sql, args);
			MySqlSelectBuffer.writeOrderbyLimit(sql, args, this.orders,
					this.limit, this.offset);
		} else {
			sql.append("select * from (");
			this.select.writeTo(sql, args);
			sql.append(") `N`");
			MySqlSelectBuffer.writeOrderbyLimit(sql, args, this.orders,
					this.limit, this.offset);
		}
	}
}
