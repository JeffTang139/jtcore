package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

public interface ISqlSegmentBuffer extends ISqlBuffer, ISqlCommandBuffer {
	public void declare(String name, DataType type);

	public ISqlInsertBuffer insert(String table);

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	public ISqlDeleteBuffer delete(String table, String alias);

	public ISqlExprBuffer assign(String var);

	public ISqlSelectIntoBuffer selectInto();

	public ISqlConditionBuffer ifThenElse();

	public ISqlLoopBuffer loop();

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate);

	public void breakLoop();

	public ISqlExprBuffer print();

	public void exit();

	public ISqlExprBuffer returnValue();

	public <T> T getFeature(Class<T> clazz);
}
