package org.eclipse.jt.core.impl;

public interface ISqlMergeBuffer extends ISqlBuffer, ISqlCommandBuffer {
	public String getTarget();

	public void usingTable(String table, String alias);

	public void usingDummy();

	public ISqlSelectBuffer usingSubQuery(String alias);

	public ISqlExprBuffer onCondition();

	public ISqlExprBuffer newValue(String field);

	public ISqlExprBuffer setValue(String field);
}
