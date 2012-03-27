package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jt.dbdrv.db2.DB2Driver9;


final class DB2JdbcDriver implements JdbcDriver {

	static final DB2JdbcDriver instance = new DB2JdbcDriver();

	private DB2JdbcDriver() {
	}

	public final Driver getDriver() {
		return DB2Driver9.driver;
	}

	public final DBLang getAdjustedLang(Connection conn) {
		return DB2Lang.lang;
	}

	public final void initDefualtProperties(Properties props) {
	}

	public final boolean supported(Connection conn) throws SQLException {
		return conn.getMetaData().getDatabaseProductName().toLowerCase()
				.indexOf("db2") >= 0;
	}

}
