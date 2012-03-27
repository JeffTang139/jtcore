/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceAcquirer.java
 * Date 2009-5-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ResourceAcquirer<TFacade, TImpl extends TFacade, TKeysHolder, TResourceAcquirer extends ResourceAcquirer<TFacade, TImpl, TKeysHolder, TResourceAcquirer>>
		extends
		Acquirer<ResourceItem<TFacade, TImpl, TKeysHolder>, TResourceAcquirer> {

	@SuppressWarnings("unchecked")
	private final ResourceAcquirerHolder holder;

	ResourceAcquirer(
			ResourceAcquirerHolder<TResourceAcquirer> resourceAcquirerHolder) {
		if (resourceAcquirerHolder == null) {
			throw new NullArgumentException("resourceAcquirerHolder");
		}
		this.holder = resourceAcquirerHolder;
	}

	@SuppressWarnings("unchecked")
	@Override
	final ResourceAcquirerHolder<TResourceAcquirer> getHolder() {
		return this.holder;
	}

	@SuppressWarnings("unchecked")
	final void removeSelfFromHolderAndRelease() {
		this.holder.removeAcquirer(this);
		this.release();
	}
}
