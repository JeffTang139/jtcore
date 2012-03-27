package org.eclipse.jt.core.impl;

final class MySqlCommandFactory implements ISqlCommandFactory,
		ISqlReplaceCommandFactory, ISqlUpdateMultiCommandFactory,
		ISqlDeleteMultiCommandFactory {

	static final MySqlCommandFactory INSTANCE = new MySqlCommandFactory();

	private MySqlCommandFactory() {
	}

	public final MySqlQueryBuffer query() {
		return new MySqlQueryBuffer();
	}

	public final MySqlInsertBuffer insert(String table) {
		return new MySqlInsertBuffer(null, table);
	}

	public final MySqlDeleteBuffer delete(String table, String alias) {
		return new MySqlDeleteBuffer(null, table, alias);
	}

	public final MySqlDeleteMultiBuffer deleteMulti(String table, String alias) {
		return new MySqlDeleteMultiBuffer(null, table, alias);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new MySqlUpdateBuffer(null, table, alias, assignFromSlaveTable);
	}

	public final MySqlUpdateMultiBuffer updateMulti(String table, String alias) {
		return new MySqlUpdateMultiBuffer(null, table, alias);
	}

	public final MySqlReplaceBuffer replace(String table) {
		return new MySqlReplaceBuffer(null, table);
	}

	public final ISqlSegmentBuffer segment() {
		return new MySqlSegmentBuffer(null);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlReplaceCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlUpdateMultiCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlDeleteMultiCommandFactory.class) {
			return (T) this;
		}
		return null;
	}

}
