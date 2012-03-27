package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jt.core.type.DataType;


final class MySQLLang extends DBLang {

	static final MySQLLang lang = new MySQLLang();

	private MySQLLang() {
		super(CHK_CONN, "mysql");
	}

	private static final String CHK_CONN = "select 1 from dual";

	@Override
	final int getMaxTablePartCount() {
		return 1024;
	}

	@Override
	final int getDefaultPartSuggestion() {
		// Partition Type : Range
		// Partition by month or year, here choose by month
		return 30;
	}

	@Override
	final int getMaxTableNameLength() {
		return 64;
	}

	@Override
	final int getMaxColumnNameLength() {
		return 64;
	}

	@Override
	final int getMaxIndexNameLength() {
		return 64;
	}

	@Override
	final String getDefaultSchema(DataSourceImpl source) {
		return "mydb";
	}

	@Override
	final void format(Appendable str, DataType type) {
		type.detect(MySQLTypeFormat.INSTANCE, str);
	}

	@Override
	final TablePartitioner newPartitioner() {
		return null;
	}

	@Override
	final TableSynchronizer newSynchronizer(DBAdapterImpl dbAdapter)
			throws SQLException {
		return new MySQLTableSynchronizer(dbAdapter, this);
	}

	@Override
	final void formatId(Appendable str, String name) {
		try {
			str.append('`').append(name).append('`');
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final MySqlCommandFactory sqlbuffers() {
		return MySqlCommandFactory.INSTANCE;
	}

	@Override
	final void setupPackage(Connection conn, String ds) {
		execSqls(conn, this.getClass(), DNA_PACKAGE_SETUP + "." + this.postfix,
				false);
	}

}
