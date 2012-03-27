/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File ResourceTreeEntry.java
 * Date 2008-8-22
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.resource.ResourceTokenLink;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
class ResourceTreeEntry<TFacade, TImpl extends TFacade, TKeysHolder> extends
		ResourceEntry<TFacade, TImpl, TKeysHolder> implements
		ResourceTokenLink<TFacade> {

	ResourceTreeEntry<TFacade, TImpl, TKeysHolder> prev;
	ResourceTreeEntry<TFacade, TImpl, TKeysHolder> next;

	ResourceTreeEntry<TFacade, TImpl, TKeysHolder> parent;
	ResourceTreeEntry<TFacade, TImpl, TKeysHolder> child;

	ResourceTreeEntry(ResourceGroup<TFacade, TImpl, TKeysHolder> holder) {
		super(holder);
	}

	private ResourceTreeEntry(ResourceEntryHolder holder) {
		super(holder);
	}

	@Override
	final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> asTreeEntry() {
		return this;
	}

	public final ResourceItem<TFacade, TImpl, TKeysHolder> getToken() {
		return this.resourceItem;
	}

	@Override
	final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> internalNext(
			TransactionImpl transaction) {
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> parent = this.parent;
		if (parent == null) {
			return null;
		}
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> entry = this.next;
		if (entry == null) {
			return null;
		}

		Acquirer<?, ?> acq = ((ResourceGroup<?, ?, ?>) this.holder).acquirer;
		boolean groupHandled = acq != null
				&& acq.similarTransaction(transaction);

		do {
			if (groupHandled) {
				while (entry != null && entry.newPlace != null) {
					entry = entry.next;
				}
				if (entry == null && parent.newPlace != null) {
					entry = parent.newPlace.child;
					while (entry != null && entry.newPlace != null) {
						entry = entry.next;
					}
				}
			} else {
				while (entry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
					entry = entry.next;
				}
			}

			if (entry != null) {
				ResourceItem<?, ?, ?> item = entry.resourceItem;
				acq = item.acquirer;
				boolean itemHandled = acq != null
						&& acq.similarTransaction(transaction);
				if (itemHandled ? item.state == ResourceItem.State.REMOVED
						: item.state == ResourceItem.State.FILLED) {
					entry = entry.next;
				} else {
					break;
				}
			}
		} while (entry != null);

		return entry;
	}

	public final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> next() {
		return this.internalNext(null);
	}

	// Transaction Lock //

	volatile State state = State.NEW;
	volatile RTEPlaceHolder newPlace;

	final class RTEPlaceHolder extends
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> {

		RTEPlaceHolder() {
			super(ResourceTreeEntry.this.holder);
			this.resourceItem = ResourceTreeEntry.this.resourceItem;
			this.state = State.TEMP_MOVED;
		}

		final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> original() {
			return ResourceTreeEntry.this;
		}
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
		 * 临时的，具有该状态的Entry表示对应的Entry要移动到的位置
		 */
		TEMP_MOVED,

		//
		// 暂时不需要
		//
		// /**
		// * 已标记为删除的
		// */
		// REMOVED,

		/**
		 * 已销毁的
		 */
		DISPOSED
	}
}
