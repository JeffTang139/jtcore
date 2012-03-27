package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class OracleSelectBuffer implements ISqlSelectBuffer {
	enum GroupMethod {
		ROLLUP, CUBE, NONE
	}

	static class OracleOrderExprBuffer extends OracleExprBuffer {
		final boolean desc;

		public OracleOrderExprBuffer(boolean desc) {
			this.desc = desc;
		}

		public OracleOrderExprBuffer(String column, boolean desc) {
			this.push(quote(column));
			this.desc = desc;
		}
	}

	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<OracleSelectColumnBuffer> columns = new ArrayList<OracleSelectColumnBuffer>();
	OracleExprBuffer where;
	ArrayList<OracleExprBuffer> group;
	OracleExprBuffer having;
	ArrayList<OracleUnionedSelectBuffer> union;
	GroupMethod groupMethod = GroupMethod.NONE;
	boolean distinct;
	ArrayList<OracleOrderExprBuffer> order;
	private boolean dummy;
	private String targetAlias;
	private String alternateAlias;

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	public ISqlTableRefBuffer newTableRef(String table, String alias) {
		OracleTableRefBuffer t = new OracleTableRefBuffer(table, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newQueryRef(String alias) {
		OracleSubQueryRefBuffer q = new OracleSubQueryRefBuffer(alias);
		q.replace(this.targetAlias, this.alternateAlias);
		this.source.add(q);
		return q;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		OracleTableRefBuffer t = new OracleTableRefBuffer(target, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public ISqlExprBuffer newColumn(String alias) {
		OracleSelectColumnBuffer expr = new OracleSelectColumnBuffer(alias);
		expr.replace(this.targetAlias, this.alternateAlias);
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new OracleExprBuffer();
			this.where.replace(this.targetAlias, this.alternateAlias);
		}
		return this.where;
	}

	public ISqlExprBuffer newGroup() {
		if (this.group == null) {
			this.group = new ArrayList<OracleExprBuffer>();
		}
		OracleExprBuffer expr = new OracleExprBuffer();
		expr.replace(this.targetAlias, this.alternateAlias);
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
			this.having = new OracleExprBuffer();
			this.having.replace(this.targetAlias, this.alternateAlias);
		}
		return this.having;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<OracleOrderExprBuffer>();
		}
		OracleOrderExprBuffer expr = new OracleOrderExprBuffer(desc);
		expr.replace(this.targetAlias, this.alternateAlias);
		this.order.add(expr);
		return expr;
	}

	public ISqlSelectBuffer newUnion(boolean all) {
		if (this.union == null) {
			this.union = new ArrayList<OracleUnionedSelectBuffer>();
		}
		OracleUnionedSelectBuffer q = new OracleUnionedSelectBuffer(all);
		q.replace(this.targetAlias, this.alternateAlias);
		this.union.add(q);
		return q;
	}

	private void writeSelect(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		Iterator<OracleSelectColumnBuffer> iter = this.columns.iterator();
		OracleSelectColumnBuffer c = iter.next();
		c.writeTo(sql, args);
		sql.append(' ').append(c.alias);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',');
			c.writeTo(sql, args);
			sql.append(' ').append(c.alias);
		}
		if (this.dummy) {
			sql.append(" from dual");
		} else {
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
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" group by rollup(");
				break;
			case CUBE:
				sql.append(" group by cube(");
				break;
			default:
				sql.append(" group by ");
				break;
			}
			Iterator<OracleExprBuffer> it = this.group.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
			case CUBE:
				sql.append(')');
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
			Iterator<OracleOrderExprBuffer> it = this.order.iterator();
			OracleOrderExprBuffer e = it.next();
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
			for (OracleUnionedSelectBuffer u : this.union) {
				u.writeTo(sql, args);
			}
		}
	}
}
