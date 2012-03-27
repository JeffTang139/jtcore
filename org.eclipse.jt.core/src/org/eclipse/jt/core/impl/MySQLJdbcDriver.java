package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jt.dbdrv.mysql.MySQLDriver5;


public final class MySQLJdbcDriver implements JdbcDriver {

	private MySQLJdbcDriver() {
	}

	public static final MySQLJdbcDriver instance = new MySQLJdbcDriver();

	public final Driver getDriver() {
		return MySQLDriver5.driver;
	}

	public final DBLang getAdjustedLang(Connection conn) {
		return MySQLLang.lang;
	}

	public final void initDefualtProperties(Properties props) {
		props.put("allowMultiQueries", "true");
	}

	public final boolean supported(Connection conn) throws SQLException {
		return conn.getMetaData().getDatabaseProductName().toLowerCase()
				.indexOf("mysql") >= 0;
	}

}
