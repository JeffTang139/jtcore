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
	 * 事实上，这里应该锁的是树，但现在尚不支持多棵树，故先以锁Group代替。
	 */
	final Object treeId = None.NONE;

	ResourceGroupHandle(
			ResourceGroupAcquirerHolder<ResourceGroupHandle> holder,
			ResourceGroup<?, ?, ?> group) {
		super(holder);
		this.exclusive(group, 0); // 目前只支持排斥锁
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
	 * 提交本句柄中记录的所有更改。
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
							// entry已经被删除了
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
								// 下面的 (ph.parent.child == ph) 和 (ph.next ==
								// null)
								// 两个判断处理块的顺序不能颠倒。
								// 因为后者中的处理过程会依赖于前者的处理结果。
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

							// REMIND? 尚未支持树节点的删除功能。
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
	 * 回滚本句柄中记录的所有更改。
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

							// entry已经被删除了
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

	// ---------------------------------以下集群相关----------------------------------

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

	// ---------------------------------以上集群相关----------------------------------

}
