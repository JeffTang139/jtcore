package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jt.core.type.DataType;


class SQLServerLang extends DBLang {

	static final SQLServerLang lang = new SQLServerLang();

	private SQLServerLang() {
		super(CHK_CONN, "sqlserver");
	}

	private static final String CHK_CONN = "select 1";

	@Override
	final String getDefaultSchema(DataSourceImpl source) {
		return "dbo";
	}

	@Override
	final int getMaxTablePartCount() {
		return 1000;
	}

	@Override
	final int getDefaultPartSuggestion() {
		return 65536;
	}

	@Override
	final int getMaxColumnNameLength() {
		return 120;
	}

	@Override
	final int getMaxIndexNameLength() {
		return 120;
	}

	@Override
	final int getMaxTableNameLength() {
		return 120;
	}

	@Override
	final void format(Appendable str, DataType type) {
		type.detect(SQLServerTypeFormatter.instance, str);
	}

	@Override
	final SQLServerTableSynchronizer newSynchronizer(DBAdapterImpl dbAdapter)
			throws SQLException {
		return new SQLServerTableSynchronizer(dbAdapter, this);
	}

	@Override
	final TablePartitioner newPartitioner() {
		// HCL
		return null;
	}

	@Override
	final void formatId(Appendable str, String name) {
		try {
			str.append('[').append(name).append(']');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final ISqlCommandFactory sqlbuffers() {
		return SQLServerCommandFactory.INSTANCE;
	}

	@Override
	final void setupPackage(Connection conn, String ds) {
		execSqls(conn, this.getClass(), DNA_PACKAGE_SETUP + "." + this.postfix,
				false);
	}

}
