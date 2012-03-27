package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class OracleUpdateBuffer extends SqlCommandBuffer implements ISqlUpdateBuffer {
	static class OracleUpdateValueBuffer extends OracleExprBuffer {
		final String field;

		public OracleUpdateValueBuffer(String field) {
			this.field = OracleExprBuffer.quote(field);
		}
	}

	final OracleTableRefBuffer table;
	final ArrayList<OracleUpdateValueBuffer> values = new ArrayList<OracleUpdateValueBuffer>();
	final boolean assignFromSlaveTable;
	OracleExprBuffer where;
	String cursor;
	private static final String alias = "\"$T\"";

	public OracleUpdateBuffer(OracleSegmentBuffer scope, String table,
			String alias, boolean assignFromSlaveTable) {
		super(scope);
		this.assignFromSlaveTable = assignFromSlaveTable;
		this.table = new OracleTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer newValue(String field) {
		OracleUpdateValueBuffer val = new OracleUpdateValueBuffer(field);
		if (this.table.joins != null) {
			val.replace(this.table.alias, alias);
		}
		this.values.add(val);
		return val;
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
		if (this.table.joins != null) {
			sql.append("update ").append(this.table.name).append(' ')
					.append(alias).append(" set ");
			if (this.assignFromSlaveTable) {
				sql.append('(');
				Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
				OracleUpdateValueBuffer val = iter.next();
				sql.append(val.field);
				while (iter.hasNext()) {
					sql.append(',').append(iter.next().field);
				}
				sql.append(")=(select ");
				iter = this.values.iterator();
				iter.next().writeTo(sql, args);
				while (iter.hasNext()) {
					sql.append(',');
					iter.next().writeTo(sql, args);
				}
				sql.append(" from ");
				this.table.writeTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and ");
				}
				sql.append(this.table.alias).append(".recid=").append(alias)
						.append(".recid and rownum<=1)");
			} else {
				Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
				OracleUpdateValueBuffer val = iter.next();
				sql.append(val.field).append('=');
				val.writeTo(sql, args);
				while (iter.hasNext()) {
					val = iter.next();
					sql.append(',').append(val.field).append('=');
					val.writeTo(sql, args);
				}
			}
			sql.append(" where exists(select 1 from ");
			this.table.writeTo(sql, args);
			sql.append(" where ");
			if (this.where != null) {
				sql.append('(');
				this.where.writeTo(sql, args);
				sql.append(") and ");
			}
			sql.append(this.table.alias).append(".recid=").append(alias)
					.append(".recid)");
		} else {
			sql.append("update ");
			this.table.writeTo(sql, args);
			sql.append(" set ");
			Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
			OracleUpdateValueBuffer val = iter.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(val.field).append('=');
				val.writeTo(sql, args);
			}
			if (this.cursor != null) {
				sql.append(" where current of ").append(this.cursor);
			} else if (this.where != null) {
				sql.append(" where ");
				this.where.writeTo(sql, args);
			}
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}
