/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResRefStorageAcquirerHolder.java
 * Date May 12, 2009
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResRefStorageAcquirerHolder<TResRefStorageAcquirer extends ResRefStorageAcquirer<TResRefStorageAcquirer>>
		extends AcquirerHolder<TResRefStorageAcquirer> {

	ResRefStorageAcquirerHolder(TransactionImpl tansaction) {
		super(tansaction);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final TResRefStorageAcquirer[] newArray(int length) {
		return (TResRefStorageAcquirer[]) new ResRefStorageAcquirer[length];
	}

	final void commit() {
		if (this.size > 0) {
			this.size = 0;
			final TResRefStorageAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResRefStorageAcquirer a, b;
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
			final TResRefStorageAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResRefStorageAcquirer a, b;
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
			final TResRefStorageAcquirer[] acquirers = this.acquirers;
			TResRefStorageAcquirer acquirer;
			for (int index = 0, len = acquirers.length; index < len; index++) {
				for (acquirer = acquirers[index]; acquirer != null; acquirer = acquirer.nextInHolder) {
					if (acquirer.res.owner.group.inCluster) {
						acquirer.buildClusterResourceUpdateTask(task);
					}
				}
			}
		}
	}

	// ---------------------------------以上集群相关----------------------------------

}
