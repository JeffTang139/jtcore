/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RcRefStorageHandle.java
 * Date 2009-2-3
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ResourceReferenceEntry.State;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class ResRefStorageHandle extends
		ResRefStorageAcquirer<ResRefStorageHandle> {

	ResRefStorageHandle(
			ResRefStorageAcquirerHolder<ResRefStorageHandle> holder,
			ResourceReferenceStorage<?> rcRefStorage) {
		super(holder);
		this.exclusive(rcRefStorage, 0); // Ŀǰֻ֧���ų���
		holder.transaction.tryLockReferenceInCluster(this, rcRefStorage);
	}

	/*----------------------------------------------------------------------*/

	/*
	 * �����Դ�����б���ܻ�ܳ����ɿ�������ResourceGroupHandle�Ĵ���ʽ��ʹ��һ����ֵ�б�
	 * �����ύ��ع�ʱ����������б��е�ֵ���ɡ�
	 */

	/**
	 * �ύ����������Ĵ洢��������صĶ����õ��޸ġ�
	 */
	@Override
	@SuppressWarnings("unchecked")
	final void commit() {
		try {
			if (!this.res.isDisposed()) {
				// LocalCluster localCluster = context.localCluster;
				// ArrayList<ClusterResInfo_RefEntry> toCs = null;
				synchronized (this.res) {
					ResourceReferenceEntry<?, ?, ?> refEntry = this.res.first;
					// if (localCluster != null && refEntry != null
					// && refEntry.resourceItem.isGlobalResource()) {
					// toCs = new ArrayList<ClusterResInfo_RefEntry>();
					// }
					while (refEntry != null) {
						if (refEntry.state == State.NEW) {
							refEntry.state = State.RESOLVED;
							if (this.res.reference.isAuthorityReference()) {
								this.res.owner.authReference = this.res;
							}
							// if (toCs != null) {
							// ResourceItem item = refEntry.resourceItem;
							// ResourceItem holder = ((ResourceReferenceStorage)
							// refEntry.holder).owner;
							// toCs.add(new ClusterResInfo_RefEntry(item
							// .getCategory(), item.getFacadeClass(),
							// item.id, holder.getCategory(), holder
							// .getFacadeClass(), holder.id,
							// Action.ADD));
							// }
						} else if (refEntry.state == State.REMOVED) {
							// if (toCs != null) {
							// ResourceItem item = refEntry.resourceItem;
							// ResourceItem holder = ((ResourceReferenceStorage)
							// refEntry.holder).owner;
							// toCs.add(new ClusterResInfo_RefEntry(item
							// .getCategory(), item.getFacadeClass(),
							// item.id, holder.getCategory(), holder
							// .getFacadeClass(), holder.id,
							// Action.DELETE));
							// }
							refEntry.resourceItem
									.removeEntry((ResourceEntry) refEntry);
							refEntry.remove();
							refEntry.state = State.DISPOSED;
							if (this.res == this.res.owner.authReference) {
								this.res.owner.authReference = null;
							}
						} else {
							Assertion.ASSERT(refEntry.state == State.RESOLVED,
									"�������޸���״̬�࣬������δ��֮�仯��");
						}

						refEntry = refEntry.next;
					}
				}
				// if (toCs != null) {
				// localCluster.broadcastRefEntryInfos(toCs);
				// }
			}
		} finally {
			this.release();
		}
	}

	/**
	 * �ع�����������Ĵ洢��������صĶ����õ��޸ġ�
	 */
	@Override
	@SuppressWarnings("unchecked")
	final void rollback() {
		try {
			if (!this.res.isDisposed()) {

				// LocalCluster localCluster = context.localCluster;
				// ArrayList<ResRefEntry_Info> toCs = null;
				synchronized (this.res) {
					ResourceReferenceEntry refEntry = this.res.first;
					// if (localCluster != null && refEntry != null
					// && refEntry.resourceItem.isGlobalResource()) {
					// toCs = new ArrayList<ResRefEntry_Info>();
					// }
					while (refEntry != null) {
						if (refEntry.state == State.NEW) {
							refEntry.resourceItem.removeEntry(refEntry);
							refEntry.remove();
							refEntry.state = State.DISPOSED;
							if (this.res.reference.isAuthorityReference()
									&& this.res.owner.authReference == this.res) {
								this.res.owner.authReference = null;
							}
						} else if (refEntry.state == State.REMOVED) {
							refEntry.state = State.RESOLVED;
						} else {
							Assertion.ASSERT(refEntry.state == State.RESOLVED,
									"�������޸���״̬�࣬������δ��֮�仯��");
						}

						refEntry = refEntry.next;
					}
				}
				// if (toCs != null) {
				// localCluster.broadcastRefEntryInfos(toCs);
				// }
			}
		} finally {
			this.release();
		}
	}

	// ---------------------------------���¼�Ⱥ���----------------------------------

	@Override
	final void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task) {
		if (!this.res.isDisposed()) {
			synchronized (this.res) {
				ResourceReferenceEntry<?, ?, ?> refEntry = this.res.first;
				while (refEntry != null) {
					if (refEntry.state == State.NEW) {
						task.addCreateReferenceAction(refEntry
								.getResourceItemId(), this.res.owner.id);
					} else if (refEntry.state == State.REMOVED) {
						task.addDeleteReferenceAction(refEntry
								.getResourceItemId(), this.res.owner.id);
					} else {
						Assertion.ASSERT(refEntry.state == State.RESOLVED,
								"�������޸���״̬�࣬������δ��֮�仯��");
					}
					refEntry = refEntry.next;
				}
			}
		}
	}

	// ---------------------------------���ϼ�Ⱥ���----------------------------------

}
