package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


public abstract class SQLServerRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {
	final String alias;
	SQLServerExprBuffer condition;
	ArrayList<SQLServerRelationRefBuffer> joins;
	TableJoinType joinType;
	
	public SQLServerRelationRefBuffer(String alias) {
		this.alias = alias;
	}

	public SQLServerRelationRefBuffer(String alias, TableJoinType type) {
		this.alias = alias;
		this.joinType = type;
	}

	private final ArrayList<SQLServerRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<SQLServerRelationRefBuffer>();
		}
		return this.joins;
	}

	public boolean findAlias(String name) {
		if (name.equals(this.alias)) {
			return true;
		}
		if (this.joins != null) {
			for (ISqlRelationRefBuffer j : this.joins) {
				if (j instanceof SQLServerTableRefBuffer) {
					if (((SQLServerTableRefBuffer) j).findAlias(name)) {
						return true;
					} else {
						((SQLServerSubQueryRefBuffer) j).findAlias(name);
					}
				}
			}
		}
		return false;
	}

	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		SQLServerTableRefBuffer j = new SQLServerTableRefBuffer(
				OracleExprBuffer.quote(table),
				SQLServerExprBuffer.quote(alias), type);
		ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		SQLServerSubQueryRefBuffer j = new SQLServerSubQueryRefBuffer(
				SQLServerExprBuffer.quote(alias), type);
		ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		SQLServerTableRefBuffer j = new SQLServerTableRefBuffer(
				OracleExprBuffer.quote(target), SQLServerExprBuffer
						.quote(alias), type);
		ensureJoins().add(j);
		return j;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new SQLServerExprBuffer();
		}
		return this.condition;
	}

	protected abstract void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args);

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.condition != null) {
			if (this.joinType == TableJoinType.INNER) {
				sql.append("inner join ");
			} else if (this.joinType == TableJoinType.FULL) {
				sql.append("full join ");
			} else if (this.joinType == TableJoinType.RIGHT) {
				sql.append("right join ");
			} else {
				sql.append("left join ");
			}
			if (this.joins != null) {
				sql.append('(');
			}
		}
		writeRefTextTo(sql, args);
		sql.append(' ').append(this.alias);
		if (this.joins != null) {
			for (SQLServerRelationRefBuffer j : this.joins) {
				sql.append(' ');
				j.writeTo(sql, args);
			}
		}
		if (this.condition != null) {
			if (this.joins != null) {
				sql.append(')');
			}
			sql.append(" on ");
			this.condition.writeTo(sql, args);
		}
	}
}
