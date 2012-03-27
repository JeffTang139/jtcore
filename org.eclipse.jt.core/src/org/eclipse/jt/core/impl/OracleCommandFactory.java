package org.eclipse.jt.core.impl;

import java.util.List;

class OracleCommandFactory implements ISqlCommandFactory,
		ISqlMergeCommandFactory {
	static class OracleRootSegmentBuffer extends OracleSegmentBuffer {
		public OracleRootSegmentBuffer() {
			super(null);
		}

		@Override
		public void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
			sql.append("begin ");
			super.writeTo(sql, args);
			sql.append(" end;");
		}
	}

	static final OracleCommandFactory INSTANCE = new OracleCommandFactory();

	public ISqlQueryBuffer query() {
		return new OracleQueryBuffer();
	}

	public ISqlInsertBuffer insert(String table) {
		return new OracleInsertBuffer(null, table);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new OracleUpdateBuffer(null, table, alias, assignFromSlaveTable);
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		return new OracleDeleteBuffer(null, table, alias);
	}

	public ISqlSegmentBuffer segment() {
		return new OracleRootSegmentBuffer();
	}

	public ISqlMergeBuffer merge(String table, String alias) {
		return new OracleMergeBuffer(null, table, alias);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlMergeCommandFactory.class) {
			return (T) this;
		}
		return null;
	}
}
