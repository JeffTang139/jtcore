package org.eclipse.jt.core.impl;

class SQLServerCommandFactory implements ISqlCommandFactory {
	static final SQLServerCommandFactory INSTANCE = new SQLServerCommandFactory();

	public ISqlQueryBuffer query() {
		return new SQLServerQueryBuffer(null);
	}

	public ISqlInsertBuffer insert(String table) {
		return new SQLServerInsertBuffer(null, table);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new SQLServerUpdateBuffer(null, table, alias);
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		return new SQLServerDeleteBuffer(null, table, alias);
	}

	public ISqlSegmentBuffer segment() {
		return new SQLServerSegmentBuffer(null);
	}

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}
}
