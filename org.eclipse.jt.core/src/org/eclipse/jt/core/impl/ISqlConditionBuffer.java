package org.eclipse.jt.core.impl;

public interface ISqlConditionBuffer extends ISqlBuffer {
	public ISqlExprBuffer newWhen();

	public ISqlSegmentBuffer newThen();

	public ISqlSegmentBuffer elseThen();
}
