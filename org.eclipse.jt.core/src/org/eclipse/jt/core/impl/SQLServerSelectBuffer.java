package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SQLServerSelectBuffer implements ISqlSelectBuffer {
	enum GroupMethod {
		ROLLUP, CUBE, NONE
	}

	static class SQLServerOrderExprBuffer extends SQLServerExprBuffer {
		final boolean desc;

		public SQLServerOrderExprBuffer(boolean desc) {
			this.desc = desc;
		}

		public SQLServerOrderExprBuffer(String column, boolean desc) {
			this.push(quote(column));
			this.desc = desc;
		}
	}

	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<SQLServerSelectColumnBuffer> columns = new ArrayList<SQLServerSelectColumnBuffer>();
	SQLServerExprBuffer where;
	ArrayList<SQLServerExprBuffer> group;
	SQLServerExprBuffer having;
	ArrayList<SQLServerUnionedSelectBuffer> union;
	GroupMethod groupMethod = GroupMethod.NONE;
	boolean distinct;
	ArrayList<SQLServerOrderExprBuffer> order;
	ISqlBuffer top;
	boolean counting;
	private boolean dummy;

	public ISqlTableRefBuffer newTableRef(String table, String alias) {
		ISqlTableRefBuffer t = new SQLServerTableRefBuffer(
				OracleExprBuffer.quote(table), SQLServerExprBuffer.quote(alias));
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newQueryRef(String alias) {
		ISqlQueryRefBuffer q = new SQLServerSubQueryRefBuffer(
				SQLServerExprBuffer.quote(alias));
		this.source.add(q);
		return q;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		ISqlWithRefBuffer t = new SQLServerTableRefBuffer(
				OracleExprBuffer.quote(target),
				SQLServerExprBuffer.quote(alias));
		this.source.add(t);
		return t;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public ISqlExprBuffer newColumn(String alias) {
		SQLServerSelectColumnBuffer expr = new SQLServerSelectColumnBuffer(
				SQLServerExprBuffer.quote(alias));
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new SQLServerExprBuffer();
		}
		return this.where;
	}

	public ISqlExprBuffer newGroup() {
		if (this.group == null) {
			this.group = new ArrayList<SQLServerExprBuffer>();
		}
		SQLServerExprBuffer expr = new SQLServerExprBuffer();
		this.group.add(expr);
		return expr;
	}

	public void distinct() {
		this.distinct = true;
	}

	public void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public void cube() {
		this.groupMethod = GroupMethod.CUBE;
	}

	public ISqlExprBuffer having() {
		if (this.having == null) {
			this.having = new SQLServerExprBuffer();
		}
		return this.having;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<SQLServerOrderExprBuffer>();
		}
		SQLServerOrderExprBuffer expr = new SQLServerOrderExprBuffer(desc);
		this.order.add(expr);
		return expr;
	}

	public ISqlSelectBuffer newUnion(boolean all) {
		if (this.union == null) {
			this.union = new ArrayList<SQLServerUnionedSelectBuffer>();
		}
		SQLServerUnionedSelectBuffer q = new SQLServerUnionedSelectBuffer(all);
		this.union.add(q);
		return q;
	}

	private void writeSelect(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		if (this.top != null) {
			sql.append("top (");
			this.top.writeTo(sql, args);
			sql.append(')');
			sql.append(' ');
		}
		Iterator<SQLServerSelectColumnBuffer> iter = this.columns.iterator();
		SQLServerSelectColumnBuffer c = iter.next();
		c.writeTo(sql, args);
		sql.append(' ');
		sql.append(c.alias);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',');
			c.writeTo(sql, args);
			sql.append(' ').append(c.alias);
		}
		if (this.counting) {
			sql.append(",1 [$FC]");
		}
		if (!this.dummy) {
			sql.append(" from ");
			Iterator<ISqlRelationRefBuffer> it = this.source.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.group != null) {
			sql.append(" group by ");
			Iterator<SQLServerExprBuffer> it = this.group.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" with rollup");
				break;
			case CUBE:
				sql.append(" with cube");
				break;
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		if (this.order != null) {
			sql.append(" order by ");
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
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		this.writeSelect(sql, args);
		if (this.union != null) {
			if (this.counting) {
				for (SQLServerUnionedSelectBuffer u : this.union) {
					u.counting = true;
					u.writeTo(sql, args);
				}
			} else {
				for (SQLServerUnionedSelectBuffer u : this.union) {
					u.writeTo(sql, args);
				}
			}
		}
	}
}
