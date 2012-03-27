/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceGroupHandle.java
 * Date 2009-1-14
 */
package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.impl.ResourceTreeEntry.RTEPlaceHolder;
import org.eclipse.jt.core.impl.ResourceTreeEntry.State;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceGroupHandle extends
		ResourceGroupAcquirer<ResourceGroupHandle> {
	/*
	 * ��ʵ�ϣ�����Ӧ�������������������в�֧�ֶ��������������Group���档
	 */
	final Object treeId = None.NONE;

	ResourceGroupHandle(
			ResourceGroupAcquirerHolder<ResourceGroupHandle> holder,
			ResourceGroup<?, ?, ?> group) {
		super(holder);
		this.exclusive(group, 0); // Ŀǰֻ֧���ų���
		holder.transaction.tryLockGroupInCluster(this, group);
	}

	/*----------------------------------------------------------------------*/

	private List<ResourceTreeEntry<?, ?, ?>> dirtyEntries;

	final void putDirtyEntry(ResourceTreeEntry<?, ?, ?> dirtyEntry) {
		if (dirtyEntry != null) {
			if (this.dirtyEntries == null) {
				this.dirtyEntries = new ArrayList<ResourceTreeEntry<?, ?, ?>>();
			}
			this.dirtyEntries.add(dirtyEntry);
		}
	}

	/**
	 * �ύ������м�¼�����и��ġ�
	 * 
	 * @param context
	 */
	@Override
	@SuppressWarnings("unchecked")
	final void commit() {
		try {
			if (this.dirtyEntries != null) {
				int len = this.dirtyEntries.size();
				if (len > 0) {
					// LocalCluster localCluster = context.localCluster;
					// ArrayList<ClusterResInfo_TreeEntry> toCs = (localCluster
					// ==
					// null || !this.res.isGlobalResource) ? null
					// : new ArrayList<ClusterResInfo_TreeEntry>();

					this.res.beginModify();
					try {
						ResourceTreeEntry entry;
						RTEPlaceHolder ph;
						for (int i = len - 1; i >= 0; i--) {
							entry = this.dirtyEntries.set(i, null);
							// entry�Ѿ���ɾ����
							if (entry.resourceItem == null
									|| entry.state == State.DISPOSED) {
								continue;
							}

							if (entry.newPlace != null) {
								Assertion.ASSERT(entry.state != State.NEW);
								ph = entry.newPlace;
								entry.newPlace = null;

								// merge children
								if (ph.child != null) {
									ResourceTreeEntry temp;
									for (temp = ph.child; temp != null; temp = temp.next) {
										temp.parent = entry;
									}
									if (entry.child != null) {
										temp = entry.child.prev;
										temp.next = ph.child;
										entry.child.prev = ph.child.prev;
										ph.child.prev = temp;
									} else {
										entry.child = ph.child;
									}
								}
								this.res.takeOutEntry(entry);
								entry.parent = ph.parent;

								entry.next = ph.next;
								entry.prev = ph.prev;

								//
								// ����� (ph.parent.child == ph) �� (ph.next ==
								// null)
								// �����жϴ�����˳���ܵߵ���
								// ��Ϊ�����еĴ�����̻�������ǰ�ߵĴ�������
								//
								if (ph.parent.child == ph) {
									ph.parent.child = entry;
								} else {
									ph.prev.next = entry;
								}
								if (ph.next == null) {
									ph.parent.child.prev = entry;
								} else {
									ph.next.prev = entry;
								}

								ph.resourceItem.removeEntry(ph);

								// if (toCs != null) {
								// toCs.add(new ClusterResInfo_TreeEntry(
								// this.res.category,
								// this.res.resourceService.facadeClass,
								// this.treeId, entry.parent
								// .getResourceItemId(), entry
								// .getResourceItemId(),
								// Action.MOVE));
								// }
							}

							if (entry.state == State.NEW) {
								entry.state = State.RESOLVED;

								// if (toCs != null) {
								// toCs.add(new ClusterResInfo_TreeEntry(
								// this.res.category,
								// this.res.resourceService.facadeClass,
								// this.treeId, entry.parent
								// .getResourceItemId(), entry
								// .getResourceItemId(),
								// Action.ADD));
								// }
							}

							// REMIND? ��δ֧�����ڵ��ɾ�����ܡ�
						}
					} finally {
						this.res.endModify();
					}

					// if (toCs != null) {
					// localCluster.broadcastTreeEntryInfos(toCs);
					// }
				}
				this.dirtyEntries = null;
			}
		} finally {
			this.release();
		}
	}

	/**
	 * �ع�������м�¼�����и��ġ�
	 * 
	 * @param context
	 */
	@Override
	@SuppressWarnings("unchecked")
	final void rollback() {
		try {
			if (this.dirtyEntries != null) {
				int len = this.dirtyEntries.size();
				if (len > 0) {
					// LocalCluster localCluster = context.localCluster;
					// ArrayList<ClusterResInfo_TreeEntry> toCs = (localCluster
					// ==
					// null || !this.res.isGlobalResource) ? null
					// : new ArrayList<ClusterResInfo_TreeEntry>();

					this.res.beginModify();
					try {
						ResourceTreeEntry entry;
						for (int i = len - 1; i >= 0; i--) {
							entry = this.dirtyEntries.set(i, null);

							// entry�Ѿ���ɾ����
							if (entry.resourceItem == null
									|| entry.state == State.DISPOSED) {
								continue;
							}

							if (entry.newPlace != null) {
								entry.resourceItem.removeEntry(entry.newPlace);
								entry.newPlace.remove();
								entry.newPlace = null;
							}

							if (entry.state == State.NEW) {
								entry.resourceItem.removeEntry(entry);
								entry.remove();
								entry.state = State.DISPOSED;

								// if (toCs != null) {
								// toCs.add(new ClusterResInfo_TreeEntry(
								// this.res.category,
								// this.res.resourceService.facadeClass,
								// this.treeId, entry.parent
								// .getResourceItemId(), entry
								// .getResourceItemId(),
								// Action.DELETE));
								// }
							}
						}
					} finally {
						this.res.endModify();
					}

					// if (toCs != null) {
					// localCluster.broadcastTreeEntryInfos(toCs);
					// }
				}
				this.dirtyEntries = null;
			}
		} finally {
			this.release();
		}
	}

	// ---------------------------------���¼�Ⱥ���----------------------------------

	@Override
	final void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task) {
		if (this.dirtyEntries != null) {
			int len = this.dirtyEntries.size();
			if (len > 0) {
				this.res.beginModify();
				try {
					ResourceTreeEntry<?, ?, ?> entry;
					for (int i = len - 1; i >= 0; i--) {
						entry = this.dirtyEntries.get(i);
						if (entry.resourceItem == null
								|| entry.state == State.DISPOSED) {
							continue;
						}
						if (entry.newPlace != null) {
							Assertion.ASSERT(entry.state != State.NEW);
							final ResourceTreeEntry<?, ?, ?> parentEntry = entry.newPlace.parent;
							if (parentEntry == null
									|| entry.resourceItem.group
											.isGroupRootTreeEntry(parentEntry)) {
								task.addCreateTreeNodeAction(entry
										.getResourceItemId());
							} else {
								task.addMoveTreeNodeAction(entry
										.getResourceItemId(), parentEntry
										.getResourceItemId());
							}
						}
						if (entry.state == State.NEW) {
							final ResourceTreeEntry<?, ?, ?> parentEntry = entry.parent;
							if (parentEntry == null
									|| entry.resourceItem.group
											.isGroupRootTreeEntry(parentEntry)) {
								task.addCreateTreeNodeAction(entry
										.getResourceItemId());
							} else {
								task.addMoveTreeNodeAction(entry
										.getResourceItemId(), parentEntry
										.getResourceItemId());
							}
						}
					}
				} finally {
					this.res.endModify();
				}
			}
		}
	}

	// ---------------------------------���ϼ�Ⱥ���----------------------------------

}
