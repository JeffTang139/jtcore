package org.eclipse.jt.core.impl;

final class DB2CommandFactory implements ISqlCommandFactory {

	static final DB2CommandFactory INSTANCE = new DB2CommandFactory();

	private DB2CommandFactory() {
	}

	public final DB2QueryBuffer query() {
		return new DB2QueryBuffer();
	}

	public final DB2InsertBuffer insert(String table) {
		return new DB2InsertBuffer(null, table);
	}

	public final DB2DeleteBuffer delete(String table, String alias) {
		return new DB2DeleteBuffer(null, table, alias);
	}

	public final DB2UpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new DB2UpdateBuffer(null, table, alias, assignFromSlaveTable);
	}

	public final DB2SegmentBuffer segment() {
		return new DB2SegmentBuffer();
	}

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}

}
