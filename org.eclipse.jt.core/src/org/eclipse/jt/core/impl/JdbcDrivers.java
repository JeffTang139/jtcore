package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.exception.NullArgumentException;


final class JdbcDrivers {

	static final JdbcDriver find(String url) {
		for (int i = 0, c = drvs.size(); i < c; i++) {
			JdbcDriver drv = drvs.get(i);
			try {
				if (drv.getDriver().acceptsURL(url)) {
					return drv;
				}
			} catch (SQLException e) {
				continue;
			}
		}
		return null;
	}

	static final JdbcDriver find(Connection conn) throws SQLException {
		if (conn == null) {
			throw new NullArgumentException("Á¬½Ó");
		}
		for (int i = 0, c = drvs.size(); i < c; i++) {
			JdbcDriver drv = drvs.get(i);
			if (drv.supported(conn)) {
				return drv;
			}
		}
		return null;
	}

	private static final ArrayList<JdbcDriver> drvs = new ArrayList<JdbcDriver>();

	static final void reg(JdbcDriver connector) {
		if (connector != null && !drvs.contains(connector)) {
			drvs.add(connector);
		}
	}

	static {
		drvs.add(OracleJdbcDriver.instance);
		drvs.add(SQLServerJdbcDriver.instance);
		drvs.add(MySQLJdbcDriver.instance);
		drvs.add(DB2JdbcDriver.instance);
	}

	private JdbcDrivers() {
	}

}
