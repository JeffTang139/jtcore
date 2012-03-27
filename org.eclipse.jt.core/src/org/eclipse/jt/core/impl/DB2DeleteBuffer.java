package org.eclipse.jt.core.impl;

import java.util.List;

final class DB2DeleteBuffer extends SqlCommandBuffer implements
		ISqlDeleteBuffer {

	final DB2TableRefBuffer target;
	DB2ExprBuffer where;
	String cursor;

	DB2DeleteBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.target = new DB2TableRefBuffer(table, alias);
	}

	public final DB2TableRefBuffer target() {
		return this.target;
	}

	public final DB2ExprBuffer where() {
		if (this.where == null) {
			this.where = new DB2ExprBuffer();
		}
		return this.where;
	}

	public final void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("delete from ");
		if (this.target.joins != null) {
			String alias = "\"$T\"";
			sql.append(this.target.table).append(' ').append(alias)
					.append(" where exists(select 1 from ");
			this.target.writeTo(sql, args);
			sql.append(" where (");
			this.where.writeTo(sql, args);
			sql.append(") and ").append(this.target.alias).append(".recid=")
					.append(alias).append(".recid)");
		} else if (this.cursor != null) {
			this.target.writeTo(sql, args);
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			this.target.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			sql.append(this.target.table);
		}
	}

}
