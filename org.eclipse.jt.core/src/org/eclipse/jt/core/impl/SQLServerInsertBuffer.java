package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SQLServerInsertBuffer extends SqlCommandBuffer implements
		ISqlInsertBuffer {
	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	SQLServerSelectBuffer select;
	ArrayList<SQLServerExprBuffer> values;

	public SQLServerInsertBuffer(SQLServerSegmentBuffer scope, String table) {
		super(scope);
		this.table = table;
	}

	public void newField(String name) {
		this.fields.add(SQLServerExprBuffer.quote(name));
	}

	public ISqlExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<SQLServerExprBuffer>();
		}
		SQLServerExprBuffer e = new SQLServerExprBuffer();
		this.values.add(e);
		return e;
	}

	public ISqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new SQLServerSelectBuffer();
		}
		return this.select;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("insert into ").append(this.table).append('(');
		Iterator<String> iter = this.fields.iterator();
		sql.append(iter.next());
		while (iter.hasNext()) {
			sql.append(',').append(iter.next());
		}
		sql.append(')').append(' ');
		if (this.values != null) {
			sql.append("values(");
			Iterator<SQLServerExprBuffer> itval = this.values.iterator();
			itval.next().writeTo(sql, args);
			while (itval.hasNext()) {
				sql.append(',');
				itval.next().writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
		sql.append(';');
	}
}
