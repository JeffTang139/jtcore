package org.eclipse.jt.core;

public interface HeavyTreeNodeFilter<TItem> extends TreeNodeFilter<TItem> {
	public Acception accept(Context context, TItem item, int absoluteLevel,
			int relativeLevel);
}
