package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.impl.OracleSelectBuffer.GroupMethod;


class MySqlSelectBuffer implements ISqlSelectBuffer {

	public final MySqlTableRefBuffer newTableRef(String table, String alias) {
		MySqlTableRefBuffer t = new MySqlTableRefBuffer(this.command, table,
				alias);
		this.sources.add(t);
		return t;
	}

	public final MySqlQueryRefBuffer newQueryRef(String alias) {
		MySqlQueryRefBuffer t = new MySqlQueryRefBuffer(this.command, alias);
		this.sources.add(t);
		return t;
	}

	public final MySqlWithRefBuffer newWithRef(String target, String alias) {
		MySqlWithRefBuffer r = new MySqlWithRefBuffer(this.command, target,
				alias);
		this.sources.add(r);
		return r;
	}

	public final void fromDummy() {
		this.dummy = true;
	}

	public final MySqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MySqlExprBuffer(this.command);
		}
		return this.where;
	}

	public final MySqlExprBuffer newGroup() {
		if (this.groups == null) {
			this.groups = new ArrayList<MySqlExprBuffer>();
		}
		MySqlExprBuffer g = new MySqlExprBuffer(this.command);
		this.groups.add(g);
		return g;
	}

	public final void distinct() {
		this.distinct = true;
	}

	public final void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public final void cube() {
		this.groupMethod = GroupMethod.CUBE;
	}

	public final MySqlExprBuffer having() {
		if (this.having == null) {
			this.having = new MySqlExprBuffer(this.command);
		}
		return this.having;
	}

	public final MySqlSelectColumnBuffer newColumn(String alias) {
		MySqlSelectColumnBuffer column = new MySqlSelectColumnBuffer(
				this.command, alias);
		this.columns.add(column);
		return column;
	}

	public final ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new MySqlExprBuffer(this.command);
		}
		return this.limit;
	}

	public final ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new MySqlExprBuffer(this.command);
		}
		return this.offset;
	}

	public final MySqlOrderExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MySqlOrderExprBuffer>();
		}
		MySqlOrderExprBuffer o = new MySqlOrderExprBuffer(this.command, desc);
		this.orders.add(o);
		return o;
	}

	public final MySqlUnionedSelectBuffer newUnion(boolean all) {
		if (this.unions == null) {
			this.unions = new ArrayList<MySqlUnionedSelectBuffer>();
		}
		MySqlUnionedSelectBuffer u = new MySqlUnionedSelectBuffer(this.command,
				all);
		this.unions.add(u);
		return u;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		this.writeSelectTo(sql, args);
	}

	final void writeSelectTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			MySqlSelectColumnBuffer col = this.columns.get(i);
			col.writeTo(sql, args);
			sql.append(' ').append(col.alias);
		}
		if (this.dummy) {
			sql.append(" from dual");
		} else {
			sql.append(" from ");
			for (int i = 0, c = this.sources.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MySqlRelationRefBuffer s = this.sources.get(i);
				s.writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.groups != null) {
			sql.append("  group by ");
			for (int i = 0, c = this.groups.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MySqlExprBuffer g = this.groups.get(i);
				g.writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" with rollup");
				break;
			case CUBE:
				throw new UnsupportedOperationException();
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		if (this.unions != null) {
			for (int i = 0, c = this.unions.size(); i < c; i++) {
				this.unions.get(i).writeTo(sql, args);
			}
		}
	}

	static final void writeOrderbyLimit(SqlStringBuffer sql,
			List<ParameterReserver> args,
			ArrayList<MySqlOrderExprBuffer> orders, MySqlExprBuffer limit,
			MySqlExprBuffer offset) {
		if (orders != null) {
			sql.append(" order by ");
			for (int i = 0, c = orders.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MySqlOrderExprBuffer o = orders.get(i);
				o.writeTo(sql, args);
			}
		}
		if (limit != null) {
			sql.append(" limit ");
			limit.writeTo(sql, args);
			if (offset != null) {
				sql.append(" offset ");
				offset.writeTo(sql, args);
			}
		}
	}

	private boolean dummy;
	ArrayList<MySqlRelationRefBuffer> sources = new ArrayList<MySqlRelationRefBuffer>();
	MySqlExprBuffer where;
	ArrayList<MySqlExprBuffer> groups;
	GroupMethod groupMethod = GroupMethod.NONE;
	MySqlExprBuffer having;
	boolean distinct;
	ArrayList<MySqlSelectColumnBuffer> columns = new ArrayList<MySqlSelectColumnBuffer>();
	ArrayList<MySqlOrderExprBuffer> orders;
	MySqlExprBuffer limit;
	MySqlExprBuffer offset;
	ArrayList<MySqlUnionedSelectBuffer> unions;

	final MySqlCommandBuffer command;

	MySqlSelectBuffer(MySqlCommandBuffer command) {
		this.command = command;
	}

	static final class MySqlSelectColumnBuffer extends MySqlExprBuffer {

		final String alias;

		MySqlSelectColumnBuffer(MySqlCommandBuffer command, String alias) {
			super(command);
			this.alias = MySqlExprBuffer.quote(alias);
		}
	}

	static final class MySqlUnionedSelectBuffer extends MySqlSelectBuffer {

		final boolean unionAll;

		MySqlUnionedSelectBuffer(MySqlCommandBuffer command, boolean unionAll) {
			super(command);
			this.unionAll = unionAll;
		}

		@Override
		public final void writeTo(SqlStringBuffer sql,
				List<ParameterReserver> args) {
			if (this.unionAll) {
				sql.append(" union ");
			} else {
				sql.append(" union all ");
			}
			if (this.unions != null) {
				sql.append('(');
				super.writeSelectTo(sql, args);
				sql.append(')');
			} else {
				super.writeTo(sql, args);
			}
		}

	}

}
