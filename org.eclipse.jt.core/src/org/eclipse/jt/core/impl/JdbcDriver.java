package org.eclipse.jt.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

interface JdbcDriver {

	Driver getDriver();

	DBLang getAdjustedLang(Connection conn);

	boolean supported(Connection conn) throws SQLException;

	void initDefualtProperties(Properties props);
}
