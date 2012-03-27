package org.eclipse.jt.core.impl;

public interface ISqlSelectBuffer extends ISqlBuffer {
	public ISqlTableRefBuffer newTableRef(String table, String alias);

	public ISqlQueryRefBuffer newQueryRef(String alias);
	
	public ISqlWithRefBuffer newWithRef(String target, String alias);

	public void fromDummy();

	public ISqlExprBuffer newColumn(String alias);

	public ISqlExprBuffer where();

	public ISqlExprBuffer newGroup();

	public void distinct();

	public void rollup();

	public void cube();

	public ISqlExprBuffer having();

	public ISqlExprBuffer newOrder(boolean desc);

	public ISqlSelectBuffer newUnion(boolean all);
}
