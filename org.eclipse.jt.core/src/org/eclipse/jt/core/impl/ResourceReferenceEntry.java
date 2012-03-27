/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File ResourceReferenceEntry.java
 * Date 2008-8-20
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.resource.ResourceTokenLink;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceReferenceEntry<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourceEntry<TFacade, TImpl, TKeysHolder> implements
		ResourceTokenLink<TFacade> {
	ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> prev;
	ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> next;

	volatile State state = State.NEW;

	ResourceReferenceEntry(ResourceReferenceStorage<TFacade> holder,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		super(holder, resourceItem);
	}

	@Override
	final ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> asReferenceEntry() {
		return this;
	}

	public final ResourceItem<TFacade, TImpl, TKeysHolder> getToken() {
		return this.resourceItem;
	}

	@Override
	public final ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> internalNext(
			TransactionImpl transaction) {
		ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> entry = this.next;
		Acquirer<?, ?> acq = ((Acquirable) this.holder).acquirer;
		boolean handled = acq != null && acq.similarTransaction(transaction);
		while (entry != null
				&& (handled ? entry.state == State.REMOVED
						: entry.state == State.NEW)) {
			entry = entry.next;
		}
		return entry;
	}

	public final ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> next() {
		return this.internalNext(null);
	}

	static enum State {
		/**
		 * 新添加的
		 */
		NEW,

		/**
		 * 处于就绪状态，可正常使用的
		 */
		RESOLVED,

		/**
		 * 已标记为删除的
		 */
		REMOVED,

		/**
		 * 已销毁的
		 */
		DISPOSED
	}
}
