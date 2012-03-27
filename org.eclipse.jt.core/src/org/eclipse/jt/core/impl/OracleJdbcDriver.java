package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jt.dbdrv.oracle.OracleDriver11;


final class OracleJdbcDriver implements JdbcDriver {

	private OracleJdbcDriver() {
	}

	static final OracleJdbcDriver instance = new OracleJdbcDriver();

	public final Driver getDriver() {
		return OracleDriver11.driver;
	}

	public final DBLang getAdjustedLang(Connection conn) {
		return new OracleLang(conn);
	}

	public final boolean supported(Connection conn) throws SQLException {
		return conn.getMetaData().getDatabaseProductName().toLowerCase()
				.indexOf("oracle") >= 0;
	}

	public final void initDefualtProperties(Properties props) {
	}

}
