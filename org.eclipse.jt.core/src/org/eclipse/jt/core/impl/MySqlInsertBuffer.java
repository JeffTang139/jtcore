package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class MySqlInsertBuffer extends MySqlCommandBuffer implements ISqlInsertBuffer {

	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	MySqlSelectBuffer select;
	ArrayList<MySqlExprBuffer> values;

	MySqlInsertBuffer(ISqlSegmentBuffer scope, String table) {
		super(scope);
		this.table = MySqlExprBuffer.quote(table);
	}

	public final void newField(String name) {
		this.fields.add(MySqlExprBuffer.quote(name));
	}

	public final MySqlExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<MySqlExprBuffer>();
		}
		MySqlExprBuffer e = new MySqlExprBuffer(this);
		this.values.add(e);
		return e;
	}

	public final MySqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new MySqlSelectBuffer(this);
		}
		return this.select;
	}

	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("insert into ").append(this.table).append('(');
		for (int i = 0, c = this.fields.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.fields.get(i));
		}
		sql.append(')').append(' ');
		if (this.values != null) {
			sql.append("values (");
			for (int i = 0, c = this.values.size(); i < c; i++) {
				if (i > 0) {
					sql.append(",");
				}
				this.values.get(i).writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
	}

}
