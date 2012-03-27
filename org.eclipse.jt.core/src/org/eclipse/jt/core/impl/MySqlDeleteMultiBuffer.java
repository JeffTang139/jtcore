package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

class MySqlDeleteMultiBuffer extends MySqlCommandBuffer implements
		ISqlDeleteMultiBuffer {

	final MySqlTableRefBuffer table;
	final ArrayList<String> froms;
	MySqlExprBuffer where;

	MySqlDeleteMultiBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.table = new MySqlTableRefBuffer(this, table, alias);
		this.froms = new ArrayList<String>();
		this.froms.add(MySqlExprBuffer.quote(alias));
	}

	public MySqlTableRefBuffer target() {
		return this.table;
	}

	public MySqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MySqlExprBuffer(this);
		}
		return this.where;
	}

	public void from(String alias) {
		this.froms.add(MySqlExprBuffer.quote(alias));
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		sql.append("delete from ");
		for (int i = 0, c = this.froms.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.froms.get(i));
		}
		sql.append(" using ");
		this.table.writeTo(sql, args);
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}
