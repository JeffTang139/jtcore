package org.eclipse.jt.core.impl;

public interface ISqlDeleteBuffer extends ISqlBuffer, ISqlCommandBuffer {
	public ISqlTableRefBuffer target();

	public ISqlExprBuffer where();

	public void whereCurrentOf(String cursor);
}
