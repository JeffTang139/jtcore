/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResRefStorageAcquirer.java
 * Date May 12, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ResRefStorageAcquirer<TResRefStorageAcquirer extends ResRefStorageAcquirer<TResRefStorageAcquirer>>
		extends Acquirer<ResourceReferenceStorage<?>, TResRefStorageAcquirer> {

	private final ResRefStorageAcquirerHolder<TResRefStorageAcquirer> holder;

	ResRefStorageAcquirer(
			ResRefStorageAcquirerHolder<TResRefStorageAcquirer> resRefStorageAcquirerHolder) {
		if (resRefStorageAcquirerHolder == null) {
			throw new NullArgumentException("resRefStorageAcquirerHolder");
		}
		this.holder = resRefStorageAcquirerHolder;
	}

	@Override
	final ResRefStorageAcquirerHolder<TResRefStorageAcquirer> getHolder() {
		return this.holder;
	}

	abstract void commit();

	abstract void rollback();

	// ---------------------------------以下集群相关----------------------------------

	abstract void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task);

	// ---------------------------------以上集群相关----------------------------------

}
