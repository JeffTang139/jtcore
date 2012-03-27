package org.eclipse.jt.core.impl;

public interface ISqlSelectIntoBuffer extends ISqlBuffer {
	public ISqlTableRefBuffer newTable(String table, String alias);

	public ISqlQueryRefBuffer newSubQuery(String alias);

	public ISqlExprBuffer newColumn(String var);

	public ISqlExprBuffer where();
}
