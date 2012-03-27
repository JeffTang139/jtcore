package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


public abstract class MySqlRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final String alias;
	MySqlExprBuffer condition;
	ArrayList<MySqlRelationRefBuffer> joins;
	TableJoinType joinType;

	final MySqlCommandBuffer command;

	MySqlRelationRefBuffer(MySqlCommandBuffer command, String alias) {
		this.command = command;
		this.alias = MySqlExprBuffer.quote(alias);
	}

	MySqlRelationRefBuffer(MySqlCommandBuffer command, String alias,
			TableJoinType type) {
		this.command = command;
		this.alias = MySqlExprBuffer.quote(alias);
		this.joinType = type;
	}

	private final ArrayList<MySqlRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<MySqlRelationRefBuffer>();
		}
		return this.joins;
	}

	public final ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		MySqlTableRefBuffer j = new MySqlTableRefBuffer(this.command, table,
				alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final MySqlQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		MySqlQueryRefBuffer j = new MySqlQueryRefBuffer(this.command, alias,
				type);
		this.ensureJoins().add(j);
		return j;
	}

	public final MySqlWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		MySqlWithRefBuffer j = new MySqlWithRefBuffer(this.command, target,
				alias);
		this.ensureJoins().add(j);
		return j;
	}

	public final ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new MySqlExprBuffer(this.command);
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
		this.writeRefTextTo(sql, args);
		sql.append(' ').append(this.alias);
		if (this.joins != null) {
			for (MySqlRelationRefBuffer j : this.joins) {
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
