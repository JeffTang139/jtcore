package org.eclipse.jt.core.impl;

import java.util.List;

class OracleDeleteBuffer extends SqlCommandBuffer implements ISqlDeleteBuffer {
	final OracleTableRefBuffer table;
	OracleExprBuffer where;
	String cursor;

	public OracleDeleteBuffer(OracleSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = new OracleTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new OracleExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("delete from ");
		if (this.table.joins != null) {
			// 多表
			String alias = "\"$T\"";
			sql.append(this.table.name).append(' ').append(alias)
					.append(" where exists(select 1 from ");
			this.table.writeTo(sql, args);
			sql.append(" where (");
			this.where.writeTo(sql, args);
			sql.append(") and ").append(this.table.alias).append(".recid=")
					.append(alias).append(".recid)");
		} else if (this.cursor != null) {
			// 游标
			this.table.writeTo(sql, args);
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			// 单表
			this.table.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			// 没有where
			sql.append(this.table.name);
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}
