package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class DB2QueryBuffer extends SqlCommandBuffer implements ISqlQueryBuffer {

	DB2QueryBuffer() {
		super(null);
	}

	public final DB2WithBuffer newWith(String name) {
		if (this.withs == null) {
			this.withs = new ArrayList<DB2WithBuffer>();
		}
		DB2WithBuffer w = new DB2WithBuffer(name);
		this.withs.add(w);
		return w;
	}

	public final DB2SelectBuffer select() {
		return this.select;
	}

	public final DB2ExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new DB2ExprBuffer();
		}
		return this.limit;
	}

	public final DB2ExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new DB2ExprBuffer();
		}
		return this.offset;
	}

	public final DB2OrderExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<DB2OrderExprBuffer>();
		}
		DB2OrderExprBuffer o = new DB2OrderExprBuffer(desc);
		this.orders.add(o);
		return o;
	}

	public final void newOrder(String column, boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<DB2OrderExprBuffer>();
		}
		DB2OrderExprBuffer o = new DB2OrderExprBuffer(column, desc);
		this.orders.add(o);
	}

	ArrayList<DB2WithBuffer> withs;
	final DB2SelectBuffer select = new DB2SelectBuffer();
	DB2ExprBuffer limit;
	DB2ExprBuffer offset;
	ArrayList<DB2OrderExprBuffer> orders;

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.withs != null) {
			sql.append("with ");
			for (int i = 0, c = this.withs.size(); i < c; i++) {
				if (i > 0) {
					sql.append(',');
				}
				this.withs.get(i).writeTo(sql, args);
			}
			sql.append(' ');
		}
		if (this.limit != null) {
			if (this.select.unions != null || this.select.distinct) {
				// 3层嵌套,最内query-statement本身全部输出
				sql.append("select \"$O\".* from (select \"$N\".*,row_number() over() \"$RN\" from (");
				this.query(sql, args);
				sql.append(") \"$N\" ) \"$O\" where ");
				sql.append("\"$O\".\"$RN\"");
				this.limit(sql, args);
			} else {
				// row_number()加到select本身,两层嵌套
				// 当前不在select层面支持limit的情况下可以这么做
				sql.append("select * from (");
				this.select.select(sql, args);
				sql.append(",row_number() over(");
				if (this.orders != null) {
					this.orders(sql, args);
				}
				sql.append(") \"$RN\"");
				this.select.from2union(sql, args);
				sql.append(") \"$N\" where \"$N\".\"$RN\"");
				this.limit(sql, args);
			}
		} else {
			this.query(sql, args);
		}
	}

	private final void limit(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.offset != null) {
			sql.append(" between (cast(");
			this.offset.writeTo(sql, args);
			sql.append(" as int))+1 and (cast(");
			this.offset.writeTo(sql, args);
			sql.append(" as int))+(");
			this.limit.writeTo(sql, args);
			sql.append(")");
		} else {
			sql.append("<=");
			this.limit.writeTo(sql, args);
		}
	}

	private final void query(SqlStringBuffer sql, List<ParameterReserver> args) {
		this.select.writeTo(sql, args);
		if (this.orders != null) {
			sql.append(" order by ");
			this.orders(sql, args);
		}
	}

	private final void orders(SqlStringBuffer sql, List<ParameterReserver> args) {
		for (int i = 0, c = this.orders.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			DB2OrderExprBuffer o = this.orders.get(i);
			o.writeTo(sql, args);
			if (o.desc) {
				sql.append(" desc");
			}
		}
	}

}
