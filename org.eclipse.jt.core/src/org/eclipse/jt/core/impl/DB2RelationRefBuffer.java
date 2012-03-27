package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.def.table.TableJoinType;


abstract class DB2RelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final String alias;
	DB2ExprBuffer condition;
	ArrayList<DB2RelationRefBuffer> joins;
	TableJoinType joinType;

	DB2RelationRefBuffer(String alias) {
		this.alias = DB2ExprBuffer.quote(alias);
	}

	DB2RelationRefBuffer(String alias, TableJoinType type) {
		this.alias = DB2ExprBuffer.quote(alias);
		this.joinType = type;
	}

	public final DB2TableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		DB2TableRefBuffer j = new DB2TableRefBuffer(table, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final DB2QueryRefBuffer joinQuery(String alias, TableJoinType type) {
		DB2QueryRefBuffer j = new DB2QueryRefBuffer(alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final DB2WithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		DB2WithRefBuffer j = new DB2WithRefBuffer(target, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	private final ArrayList<DB2RelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<DB2RelationRefBuffer>();
		}
		return this.joins;
	}

	public final DB2ExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new DB2ExprBuffer();
		}
		return this.condition;
	}

	protected abstract void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterReserver> args);

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
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
			for (DB2RelationRefBuffer j : this.joins) {
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
