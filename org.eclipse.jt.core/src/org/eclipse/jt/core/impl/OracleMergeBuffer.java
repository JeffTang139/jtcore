package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OracleMergeBuffer extends SqlCommandBuffer implements
		ISqlMergeBuffer {
	static class OracleMergeValueBuffer extends OracleExprBuffer {
		final String field;

		public OracleMergeValueBuffer(String field) {
			this.field = field;
		}
	}

	final String table;
	final String alias;
	String srcTable;
	OracleSelectBuffer srcQuery;
	String srcAlias;
	ArrayList<OracleMergeValueBuffer> newValues;
	ArrayList<OracleMergeValueBuffer> setValues;
	OracleExprBuffer condition;

	public OracleMergeBuffer(OracleSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = table;
		this.alias = alias;
	}

	public String getTarget() {
		return this.table;
	}

	public void usingDummy() {
		this.srcTable = "dual";
		this.srcAlias = "dual";
	}

	public void usingTable(String table, String alias) {
		this.srcTable = OracleExprBuffer.quote(table);
		this.srcAlias = OracleExprBuffer.quote(alias);
	}

	public ISqlSelectBuffer usingSubQuery(String alias) {
		this.srcQuery = new OracleSelectBuffer();
		this.srcAlias = OracleExprBuffer.quote(alias);
		return this.srcQuery;
	}

	public ISqlExprBuffer newValue(String field) {
		if (this.newValues == null) {
			this.newValues = new ArrayList<OracleMergeValueBuffer>();
		}
		OracleMergeValueBuffer e = new OracleMergeValueBuffer(field);
		this.newValues.add(e);
		return e;
	}

	public ISqlExprBuffer setValue(String field) {
		if (this.setValues == null) {
			this.setValues = new ArrayList<OracleMergeValueBuffer>();
		}
		OracleMergeValueBuffer e = new OracleMergeValueBuffer(field);
		this.setValues.add(e);
		return e;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new OracleExprBuffer();
		}
		return this.condition;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("merge into ").append(this.table);
		if (this.alias != null) {
			sql.append(' ').append(this.alias);
		}
		sql.append(" using ");
		if (this.srcTable != null) {
			sql.append(this.srcTable);
		} else {
			sql.append('(');
			this.srcQuery.writeTo(sql, args);
			sql.append(')');
		}
		if (this.srcAlias != null) {
			sql.append(' ').append(this.srcAlias);
		}
		sql.append(" on (");
		this.condition.writeTo(sql, args);
		sql.append(')');
		if (this.setValues != null) {
			sql.append(" when matched then update set ");
			Iterator<OracleMergeValueBuffer> iter = this.setValues.iterator();
			OracleMergeValueBuffer val = iter.next();
			sql.append(OracleExprBuffer.quote(val.field)).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(OracleExprBuffer.quote(val.field))
						.append('=');
				val.writeTo(sql, args);
			}
		}
		if (this.newValues != null) {
			sql.append(" when not matched then insert (");
			Iterator<OracleMergeValueBuffer> iter = this.newValues.iterator();
			sql.append(OracleExprBuffer.quote(iter.next().field));
			while (iter.hasNext()) {
				sql.append(',').append(iter.next().field);
			}
			sql.append(") values(");
			iter = this.newValues.iterator();
			iter.next().writeTo(sql, args);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeTo(sql, args);
			}
			sql.append(')');
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}
