package org.eclipse.jt.core.impl;

public interface ISqlUpdateMultiBuffer extends ISqlCommandBuffer {

	public ISqlTableRefBuffer target();

	public ISqlExprBuffer newValue(String alias, String field);

	public ISqlExprBuffer where();

}
