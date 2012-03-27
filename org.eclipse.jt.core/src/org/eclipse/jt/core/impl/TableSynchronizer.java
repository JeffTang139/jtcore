package org.eclipse.jt.core.impl;

import java.sql.SQLException;

interface TableSynchronizer {

	boolean sync(TableDefineImpl table) throws SQLException;

	boolean post(TableDefineImpl post, TableDefineImpl runtime)
			throws SQLException;

	void drop(TableDefineImpl table) throws SQLException;

	void unuse();

}