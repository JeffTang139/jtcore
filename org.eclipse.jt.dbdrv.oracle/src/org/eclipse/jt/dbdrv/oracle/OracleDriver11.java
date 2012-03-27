package org.eclipse.jt.dbdrv.oracle;

import java.sql.Driver;

public class OracleDriver11 {
	public static Driver driver = null;
	static {
		driver = new oracle.jdbc.driver.OracleDriver();
	}
}
