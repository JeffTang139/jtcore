package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class DB2UpdateBuffer extends SqlCommandBuffer implements
		ISqlUpdateBuffer {

	final DB2TableRefBuffer target;
	final boolean assignFromSlaveTable;
	final ArrayList<DB2UpdateAssignBuffer> values = new ArrayList<DB2UpdateAssignBuffer>();
	DB2ExprBuffer where;
	String cursor;

	DB2UpdateBuffer(ISqlSegmentBuffer scope, String table, String alias,
			boolean assignFromSlaveTable) {
		super(scope);
		this.target = new DB2TableRefBuffer(table, alias);
		this.assignFromSlaveTable = assignFromSlaveTable;
	}

	public final DB2TableRefBuffer target() {
		return this.target;
	}

	public final DB2UpdateAssignBuffer newValue(String field) {
		DB2UpdateAssignBuffer a = new DB2UpdateAssignBuffer(field);
		this.values.add(a);
		return a;
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

	static class DB2UpdateAssignBuffer extends DB2ExprBuffer {

		final String field;

		public DB2UpdateAssignBuffer(String field) {
			this.field = quote(field);
		}
	}

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		if (this.target.joins == null) {
			sql.append("update ");
			this.target.writeTo(sql, args);
			sql.append(" set ");
			for (int i = 0, c = this.values.size(); i < c; i++) {
				DB2UpdateAssignBuffer val = this.values.get(i);
				if (i > 0) {
					sql.append(',');
				}
				sql.append(val.field).append('=');
				val.writeTo(sql, args);
			}
			if (this.cursor != null) {
				sql.append(" where current of ").append(this.cursor);
			} else if (this.where != null) {
				sql.append(" where ");
				this.where.writeTo(sql, args);
			}
		} else {
			final String T = "\"$T\"";
			sql.append("update ").append(this.target.table).append(' ')
					.append(T).append(" set ");
			if (this.assignFromSlaveTable) {
				final String N = "\"$N\"";
				sql.append('(');
				for (int i = 0, c = this.values.size(); i < c; i++) {
					if (i > 0) {
						sql.append(',');
					}
					sql.append(this.values.get(i).field);
				}
				sql.append(")=(select ");
				for (int i = 0, c = this.values.size(); i < c; i++) {
					if (i > 0) {
						sql.append(',');
					}
					sql.append(N).append('.');
					sql.append(this.values.get(i).field);
				}
				sql.append(" from (select ");
				for (int i = 0, c = this.values.size(); i < c; i++) {
					if (i > 0) {
						sql.append(',');
					}
					DB2UpdateAssignBuffer val = this.values.get(i);
					val.writeTo(sql, args);
					sql.append(' ');
					sql.append(this.values.get(i).field);
				}
				final String ID = "\"$RECID\"";
				final String RN = "\"$RN\"";
				sql.append(",").append(this.target.alias).append(".\"RECID\" ")
						.append(ID);
				sql.append(",row_number() over () ").append(RN);
				sql.append(" from ");
				this.target.writeTo(sql, args);
				if (this.where != null) {
					sql.append(" where ");
					this.where.writeTo(sql, args);
				}
				sql.append(") ").append(N).append(" where ").append(N)
						.append('.').append(ID).append("=").append(T)
						.append(".\"RECID\" and ").append(N).append('.')
						.append(RN).append("<=1)");
			} else {
				for (int i = 0, c = this.values.size(); i < c; i++) {
					if (i > 0) {
						sql.append(',');
					}
					DB2UpdateAssignBuffer val = this.values.get(i);
					sql.append(val.field).append('=');
					val.writeTo(sql, args);
				}
			}
			sql.append(" where exists (select 1 from ");
			this.target.writeTo(sql, args);
			sql.append(" where ");
			if (this.where != null) {
				sql.append('(');
				this.where.writeTo(sql, args);
				sql.append(") and ");
			}
			sql.append(this.target.alias).append(".\"RECID\"=").append(T)
					.append(".\"RECID\")");
		}
	}
}
