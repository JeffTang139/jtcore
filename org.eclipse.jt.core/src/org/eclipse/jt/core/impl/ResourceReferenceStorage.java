/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File ResourceReferenceBase.java
 * Date 2008-8-20
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceReference;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceReferenceStorage<TRefFacade> extends Acquirable implements
        ResourceEntryHolder {
	final ResourceReference<TRefFacade, ?> reference;
	final ResourceItem<?, ?, ?> owner;

	final boolean isDisposed() {
		if (this.owner.state == State.DISPOSED) {
			return true;
		}
		return false;
	}

	//
	// (1) (2) (3)
	//
	// The references (1), (2) and (3) are stored in this storage.
	// The numbers 1, 2, ... are orders that the references were added into the
	// storage.
	//
	// this.first = (1)
	// (1).next = (2)
	// (2).next = (3)
	// (3).next = null
	// (1).prev = (3)
	// (2).prev = (1)
	// (3).prev = (2)
	//
	ResourceReferenceEntry<TRefFacade, ?, ?> first;

	ResourceReferenceStorage<?> next;

	@SuppressWarnings("unchecked")
	static final <TRefFacade> void moveReferences(
	        ResourceReferenceStorage<TRefFacade> from,
	        ResourceReferenceStorage<TRefFacade> to) {
		if (from != null && to != null && from.first != null) {
			synchronized (to) { // FIXME Lock
				if (to.first == null) {
					to.first = from.first;
				} else {
					ResourceReferenceEntry temp = to.first.prev;
					temp.next = from.first;
					to.first.prev = (ResourceReferenceEntry) from.first.prev;
					from.first.prev = temp;
				}
				from.first = null;
			}
		}
	}

	ResourceReferenceStorage(ResourceItem<?, ?, ?> owner,
	        ResourceReference<TRefFacade, ?> reference) {
		if (owner == null || reference == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		this.reference = reference;
	}

	@SuppressWarnings("unchecked")
	public synchronized final void removeEntryCommitly(
	        ResourceEntry<?, ?, ?> entry) {
		if (entry.holder != this) {
			throw new InternalError();
		}
		final ResourceReferenceEntry toDel = (ResourceReferenceEntry) entry;
		if (this.first == toDel) {
			this.first = toDel.next;
			if (this.first != null) {
				this.first.prev = toDel.prev;
			}
		} else {
			toDel.prev.next = toDel.next;
			if (toDel.next != null) {
				toDel.next.prev = toDel.prev;
			} else {
				this.first.prev = toDel.prev;
			}
		}
		toDel.holder = null;
		toDel.resourceItem = null;
	}
}
