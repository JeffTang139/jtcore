package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.impl.OracleSelectBuffer.GroupMethod;


class DB2SelectBuffer implements ISqlSelectBuffer {

	public final DB2TableRefBuffer newTableRef(String table, String alias) {
		DB2TableRefBuffer r = new DB2TableRefBuffer(table, alias);
		this.sources.add(r);
		return r;
	}

	public final DB2QueryRefBuffer newQueryRef(String alias) {
		DB2QueryRefBuffer r = new DB2QueryRefBuffer(alias);
		this.sources.add(r);
		return r;
	}

	public final DB2WithRefBuffer newWithRef(String target, String alias) {
		DB2WithRefBuffer r = new DB2WithRefBuffer(target, alias);
		this.sources.add(r);
		return r;
	}

	public final void fromDummy() {
		this.dummy = true;
	}

	public final DB2SelectColumnBuffer newColumn(String alias) {
		DB2SelectColumnBuffer c = new DB2SelectColumnBuffer(alias);
		this.columns.add(c);
		return c;
	}

	public final DB2ExprBuffer where() {
		if (this.where == null) {
			this.where = new DB2ExprBuffer();
		}
		return this.where;
	}

	public final DB2ExprBuffer newGroup() {
		if (this.groups == null) {
			this.groups = new ArrayList<DB2ExprBuffer>();
		}
		DB2ExprBuffer g = new DB2ExprBuffer();
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

	public final DB2ExprBuffer having() {
		if (this.having == null) {
			this.having = new DB2ExprBuffer();
		}
		return this.having;
	}

	public final DB2OrderExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<DB2OrderExprBuffer>();
		}
		DB2OrderExprBuffer o = new DB2OrderExprBuffer(desc);
		this.orders.add(o);
		return o;
	}

	public final DB2UnionedSelectBuffer newUnion(boolean all) {
		if (this.unions == null) {
			this.unions = new ArrayList<DB2UnionedSelectBuffer>();
		}
		DB2UnionedSelectBuffer u = new DB2UnionedSelectBuffer(all);
		this.unions.add(u);
		return u;
	}

	private boolean dummy;
	ArrayList<DB2RelationRefBuffer> sources = new ArrayList<DB2RelationRefBuffer>();
	DB2ExprBuffer where;
	ArrayList<DB2ExprBuffer> groups;
	GroupMethod groupMethod = GroupMethod.NONE;
	DB2ExprBuffer having;
	boolean distinct;
	ArrayList<DB2SelectColumnBuffer> columns = new ArrayList<DB2SelectColumnBuffer>();
	ArrayList<DB2OrderExprBuffer> orders;
	DB2ExprBuffer limit;
	DB2ExprBuffer offset;
	ArrayList<DB2UnionedSelectBuffer> unions;
	boolean outputRownumber;

	static final class DB2SelectColumnBuffer extends DB2ExprBuffer {

		final String alias;

		DB2SelectColumnBuffer(String alias) {
			this.alias = quote(alias);
		}

	}

	static final class DB2UnionedSelectBuffer extends DB2SelectBuffer {

		final boolean unionAll;

		DB2UnionedSelectBuffer(boolean unionAll) {
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
				super.writeFullSelectTo(sql, args);
				sql.append(')');
			} else {
				super.writeTo(sql, args);
			}
		}

	}

	final void writeFullSelectTo(SqlStringBuffer sql,
			List<ParameterReserver> args) {
		this.select(sql, args);
		this.from2union(sql, args);
	}

	final void select(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			DB2SelectColumnBuffer col = this.columns.get(i);
			col.writeTo(sql, args);
			sql.append(' ').append(col.alias);
		}
	}

	final void from2union(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.dummy) {
			sql.append(" from sysibm.dual");
		} else {
			sql.append(" from ");
			for (int i = 0, c = this.sources.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				DB2RelationRefBuffer s = this.sources.get(i);
				s.writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.groups != null) {
			sql.append(" group by ");
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append("rollup (");
				this.groups(sql, args);
				sql.append(')');
				break;
			case CUBE:
				sql.append("cube (");
				this.groups(sql, args);
				sql.append(')');
				break;
			default:
				this.groups(sql, args);
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		if (this.unions != null) {
			for (int i = 0, c = this.unions.size(); i < c; i++) {
				this.unions.get(i).writeTo(sql, args);
			}
		}
		// HCL Ö§³Ölimit
	}

	private final void groups(SqlStringBuffer sql, List<ParameterReserver> args) {
		for (int i = 0, c = this.groups.size(); i < c; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			DB2ExprBuffer g = this.groups.get(i);
			g.writeTo(sql, args);
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		this.writeFullSelectTo(sql, args);
	}

}
