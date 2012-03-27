/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteResGroupHandle.java
 * Date 2009-5-18
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteResGroupHandle extends
		ResourceGroupAcquirer<RemoteResGroupHandle> {

	final int clusterIndex;

	RemoteResGroupHandle(int clusterIndex,
			ResourceGroupAcquirerHolder<RemoteResGroupHandle> holder) {
		super(holder);
		this.clusterIndex = clusterIndex;
	}

	final LockerInfo acquireExclusiveLock(ResourceGroup<?, ?, ?> group) {
		// TODO lock unblockly
		return null;
	}

	@Override
	void commit() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	void rollback() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	// ---------------------------------以下集群相关----------------------------------

	@Override
	final void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task) {
		throw new UnsupportedOperationException();
	}

	// ---------------------------------以上集群相关----------------------------------

}
