package org.eclipse.jt.core.impl;

public interface EFilter<TItem, TContext> {

	public boolean accept(TItem item, TContext context);
}
