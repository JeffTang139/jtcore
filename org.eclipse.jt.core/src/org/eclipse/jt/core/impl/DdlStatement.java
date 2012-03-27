package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.sql.Statement;

final class DdlStatement {

	final DBAdapterImpl adapter;

	private Statement stmt;

	DdlStatement(DBAdapterImpl dbAdapter) {
		this.adapter = dbAdapter;
	}

	final void execute(String sql) throws SQLException {
		if (this.stmt == null) {
			this.stmt = this.adapter.createStatement();
			this.adapter.updateTrans(true);
		}
		this.adapter.executeDdl(this.stmt, sql);
	}

	final void execute(SqlBuilder sql) throws SQLException {
		this.execute(sql.toSql());
	}

	final void unuse() {
		if (this.stmt != null) {
			final Statement stmt = this.stmt;
			this.stmt = null;
			this.adapter.freeStatement(stmt);
		}
	}
}