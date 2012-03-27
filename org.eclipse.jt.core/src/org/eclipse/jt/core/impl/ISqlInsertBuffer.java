package org.eclipse.jt.core.impl;

public interface ISqlInsertBuffer extends ISqlBuffer, ISqlCommandBuffer {

	public void newField(String name);

	public ISqlExprBuffer newValue();

	public ISqlSelectBuffer select();
}
