/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File Entry.java
 * Date 2008-8-20
 */
package org.eclipse.jt.core.impl;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ResourceEntry<TFacade, TImpl extends TFacade, TKeysHolder> {
	volatile ResourceEntryHolder holder;
	volatile ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem;
	ResourceEntry<TFacade, TImpl, TKeysHolder> nextSibling;

	ResourceEntry(ResourceEntryHolder holder) {
		if (holder == null) {
			throw new NullPointerException();
		}
		this.holder = holder;
	}

	ResourceEntry(ResourceEntryHolder holder,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		if (holder == null) {
			throw new NullPointerException();
		}
		this.holder = holder;
		this.resourceItem = resourceItem;
	}

	abstract ResourceEntry<TFacade, TImpl, TKeysHolder> internalNext(
			TransactionImpl transaction);

	final long getResourceItemId() {
		return this.resourceItem == null ? 0 : this.resourceItem.id;
	}

	final void remove() {
		ResourceEntryHolder h = this.holder;
		if (h != null) {
			h.removeEntryCommitly(this);
		}
	}

	ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> asIndexEntry() {
		return null;
	}

	ResourceReferenceEntry<TFacade, TImpl, TKeysHolder> asReferenceEntry() {
		return null;
	}

	ResourceTreeEntry<TFacade, TImpl, TKeysHolder> asTreeEntry() {
		return null;
	}
}
