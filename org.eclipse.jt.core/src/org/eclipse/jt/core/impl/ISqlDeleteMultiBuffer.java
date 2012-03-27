package org.eclipse.jt.core.impl;

public interface ISqlDeleteMultiBuffer extends ISqlCommandBuffer {

	public ISqlTableRefBuffer target();

	public ISqlExprBuffer where();

	public void from(String alias);
}
