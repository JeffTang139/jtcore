package org.eclipse.jt.core.impl;

public interface ISqlUpdateBuffer extends ISqlBuffer, ISqlCommandBuffer {
	public ISqlTableRefBuffer target();

	public ISqlExprBuffer newValue(String field);

	public ISqlExprBuffer where();

	public void whereCurrentOf(String cursor);
}
