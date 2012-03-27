package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class MySqlReplaceBuffer extends MySqlCommandBuffer implements
		ISqlReplaceBuffer {

	final String table;

	final ArrayList<String> fields = new ArrayList<String>();
	final ArrayList<MySqlExprBuffer> values = new ArrayList<MySqlExprBuffer>();

	MySqlReplaceBuffer(ISqlSegmentBuffer scope, String table) {
		super(scope);
		this.table = MySqlExprBuffer.quote(table);
	}

	public final void newField(String name) {
		this.fields.add(MySqlExprBuffer.quote(name));
	}

	public final ISqlExprBuffer newValue() {
		MySqlExprBuffer expr = new MySqlExprBuffer(this);
		this.values.add(expr);
		return expr;
	}

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("replace into ").append(this.table);
		sql.append(" (");
		for (int i = 0, c = this.fields.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.fields.get(i));
		}
		sql.append(") values (");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			this.values.get(i).writeTo(sql, args);
		}
		sql.append(')');
	}

}
