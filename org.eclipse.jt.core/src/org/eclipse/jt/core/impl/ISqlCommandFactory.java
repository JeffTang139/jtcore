package org.eclipse.jt.core.impl;

public interface ISqlCommandFactory {
	public ISqlQueryBuffer query();

	public ISqlInsertBuffer insert(String table);

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	public ISqlDeleteBuffer delete(String table, String alias);

	public ISqlSegmentBuffer segment();

	public <T> T getFeature(Class<T> clazz);
}
