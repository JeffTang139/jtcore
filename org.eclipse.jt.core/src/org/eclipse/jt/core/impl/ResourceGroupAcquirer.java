/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceGroupAcquirer.java
 * Date May 12, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ResourceGroupAcquirer<TResourceGroupAcquirer extends ResourceGroupAcquirer<TResourceGroupAcquirer>>
		extends Acquirer<ResourceGroup<?, ?, ?>, TResourceGroupAcquirer> {

	private final ResourceGroupAcquirerHolder<TResourceGroupAcquirer> holder;

	ResourceGroupAcquirer(
			ResourceGroupAcquirerHolder<TResourceGroupAcquirer> resourceGroupAcquirerHolder) {
		if (resourceGroupAcquirerHolder == null) {
			throw new NullArgumentException("resourceGroupAcquirerHolder");
		}
		this.holder = resourceGroupAcquirerHolder;
	}

	@Override
	final ResourceGroupAcquirerHolder<TResourceGroupAcquirer> getHolder() {
		return this.holder;
	}

	abstract void commit();

	abstract void rollback();

	// ---------------------------------以下集群相关----------------------------------

	abstract void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task);

	// ---------------------------------以上集群相关----------------------------------

}
