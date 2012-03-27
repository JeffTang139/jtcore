package org.eclipse.jt.core.impl;

interface ISqlReplaceBuffer extends ISqlBuffer, ISqlCommandBuffer {

	void newField(String name);

	ISqlExprBuffer newValue();
}
