package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

final class DB2InsertBuffer extends SqlCommandBuffer implements
		ISqlInsertBuffer {

	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	DB2SelectBuffer select;
	ArrayList<DB2ExprBuffer> values;

	DB2InsertBuffer(ISqlSegmentBuffer scope, String table) {
		super(scope);
		this.table = DB2ExprBuffer.quote(table);
	}

	public final void newField(String name) {
		this.fields.add(DB2ExprBuffer.quote(name));
	}

	public final DB2ExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<DB2ExprBuffer>();
		}
		DB2ExprBuffer val = new DB2ExprBuffer();
		this.values.add(val);
		return val;
	}

	public final DB2SelectBuffer select() {
		if (this.select == null) {
			this.select = new DB2SelectBuffer();
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
