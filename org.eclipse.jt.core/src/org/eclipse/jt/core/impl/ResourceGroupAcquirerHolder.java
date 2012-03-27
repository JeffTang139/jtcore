/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceGroupAcquirerHolder.java
 * Date May 12, 2009
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceGroupAcquirerHolder<TResourceGroupAcquirer extends ResourceGroupAcquirer<TResourceGroupAcquirer>>
		extends AcquirerHolder<TResourceGroupAcquirer> {

	ResourceGroupAcquirerHolder(TransactionImpl transaction) {
		super(transaction);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final TResourceGroupAcquirer[] newArray(int length) {
		return (TResourceGroupAcquirer[]) new ResourceGroupAcquirer[length];
	}

	final void commit() {
		if (this.size > 0) {
			this.size = 0;
			final TResourceGroupAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResourceGroupAcquirer a, b;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; b = a, a = a.nextInHolder, b.nextInHolder = null) {
					a.commit();
				}
				acquirers[i] = null;
			}
		}
	}

	final void rollback() {
		if (this.size > 0) {
			this.size = 0;
			final TResourceGroupAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResourceGroupAcquirer a, b;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; b = a, a = a.nextInHolder, b.nextInHolder = null) {
					a.rollback();
				}
				acquirers[i] = null;
			}
		}
	}

	// ---------------------------------以下集群相关----------------------------------

	final void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task) {
		if (this.size > 0) {
			final TResourceGroupAcquirer[] acquirers = this.acquirers;
			TResourceGroupAcquirer acquirer;
			for (int index = 0, len = acquirers.length; index < len; index++) {
				for (acquirer = acquirers[index]; acquirer != null; acquirer = acquirer.nextInHolder) {
					if (acquirer.res.inCluster) {
						acquirer.buildClusterResourceUpdateTask(task);
					}
				}
			}
		}
	}
	// ---------------------------------以上集群相关----------------------------------

}
