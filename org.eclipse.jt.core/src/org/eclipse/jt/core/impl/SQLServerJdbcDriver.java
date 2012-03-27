package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jt.dbdrv.sqlserver.jdbc3.SQLServerDriverJDBC3;
import org.eclipse.jt.dbdrv.sqlserver.jdbc4.SQLServerDriverJDBC4;


final class SQLServerJdbcDriver implements JdbcDriver {

	private SQLServerJdbcDriver() {
	}

	static final SQLServerJdbcDriver instance = new SQLServerJdbcDriver();

	static final Driver driver;

	static {
		if (SQLServerDriverJDBC4.driver != null) {
			driver = SQLServerDriverJDBC4.driver;
		} else {
			driver = SQLServerDriverJDBC3.driver;
		}
	}

	public final Driver getDriver() {
		return driver;
	}

	public DBLang getAdjustedLang(Connection conn) {
		return SQLServerLang.lang;
	}

	public final void initDefualtProperties(Properties props) {
	}

	public final boolean supported(Connection conn) throws SQLException {
		String dbname = conn.getMetaData().getDatabaseProductName()
				.toLowerCase();
		return dbname.indexOf("sql server") >= 0
				|| dbname.indexOf("sqlserver") >= 0;
	}
}
