package org.eclipse.jt.core.da;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.jt.dbdrv.db2.DB2Driver9;
import org.eclipse.jt.dbdrv.mysql.MySQLDriver5;
import org.eclipse.jt.dbdrv.oracle.OracleDriver11;
import org.eclipse.jt.dbdrv.sqlserver.jdbc3.SQLServerDriverJDBC3;
import org.eclipse.jt.dbdrv.sqlserver.jdbc4.SQLServerDriverJDBC4;


public final class ConnectionTester {

	private static final Driver[] drvs;

	static {
		ArrayList<Driver> l = new ArrayList<Driver>();
		l.add(OracleDriver11.driver);
		if (SQLServerDriverJDBC3.driver != null) {
			l.add(SQLServerDriverJDBC3.driver);
		}
		if (SQLServerDriverJDBC4.driver != null) {
			l.add(SQLServerDriverJDBC4.driver);
		}
		l.add(DB2Driver9.driver);
		l.add(MySQLDriver5.driver);
		drvs = l.toArray(new Driver[l.size()]);
	}

	public static boolean test(String url, String user, String password) {
		for (Driver drv : drvs) {
			try {
				if (drv.acceptsURL(url)) {
					try {
						Properties props = new Properties();
						props.put("user", user);
						props.put("password", password);
						Connection conn = drv.connect(url, props);
						try {
							return true;
						} finally {
							conn.close();
						}
					} catch (SQLException e) {
						return false;
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return false;
	}
}
