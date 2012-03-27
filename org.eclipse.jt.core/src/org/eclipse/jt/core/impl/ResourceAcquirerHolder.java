/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceAcquirerHolder.java
 * Date 2009-5-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ResourceAcquirerHolder<TResourceAcquirer extends ResourceAcquirer<?, ?, ?, TResourceAcquirer>>
		extends AcquirerHolder<TResourceAcquirer> {

	ResourceAcquirerHolder(TransactionImpl transaction) {
		super(transaction);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final TResourceAcquirer[] newArray(int length) {
		return (TResourceAcquirer[]) new ResourceAcquirer[length];
	}

	final boolean hasResHandleAboutGroup(ResourceGroup<?, ?, ?> group) {
		if (this.size > 0) {
			final TResourceAcquirer[] acquirers = this.acquirers;
			TResourceAcquirer a;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; a = a.nextInHolder) {
					if (a.res.group == group) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	final void postModifiedResource(TransactionImpl transaction,
			Object modifiedResource) {
		if (this.size > 0) {
			final TResourceAcquirer[] acquirers = this.acquirers;
			TResourceAcquirer a;
			ResourceItem item;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; a = a.nextInHolder) {
					item = a.res;
					if (item.tempValues != null
							&& modifiedResource == item.tempValues.copyForModification) {
						boolean flag = item.impl == item.keys;
						if (flag) {
							if (!item.isKeysEqual(modifiedResource)) {
								item.group.putResource(transaction,
										modifiedResource, modifiedResource,
										WhenExists.REPLACE);
								item.tempValues.copyForModification = null;
								if (item.group.resourceService.isOnlyOneKey()) {
									item.state = State.REMOVED;
								}
								return;
							}
						}
						item.tempValues.copyForModification = null;
						if (item.state == State.RESOLVED
								|| item.state == State.MODIFIED) {
							if (flag) {
								item.tempValues.newKeys = modifiedResource;
							}
							item.tempValues.newImpl = modifiedResource;
							item.state = State.MODIFIED;
						} else if (item.state == State.FILLED) {
							if (flag) {
								item.keys = modifiedResource;
							}
							item.impl = modifiedResource;
						} else {
							throw new IllegalStateException("资源的修改状态被重置了");
						}
						return;
					}
				}
			}
		}
		throw new IllegalStateException("资源的修改状态被重置了");
	}

	@SuppressWarnings("unchecked")
	final void commit(ExceptionCatcher catcher) {

		if (this.size > 0) {
			this.size = 0;
			final TResourceAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResourceAcquirer a, b;
			ResourceItem item;
			State itemState;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; b = a, a = a.nextInHolder, b.nextInHolder = null) {
					item = a.res;
					itemState = item.state;
					if (itemState == State.FILLED) {
						item.state = State.RESOLVED;
						item.tempValues = null;
					} else if (itemState == State.MODIFIED) {
						Assertion.ASSERT(item.tempValues != null);
						item.keys = item.tempValues.newKeys;
						item.impl = item.tempValues.newImpl;
						item.tempValues = null;
						item.state = State.RESOLVED;
					} else if (itemState == State.REMOVED) {
						item.remove(catcher);
					} else if (itemState == State.RESOLVED) {
						item.tempValues = null;
					} else {
						a.release();
						Assertion.ASSERT(false, "不应出现的状态：" + item.state);
					}

					item.group.invalidCachedList();

					/*
					 * 剔除冗余的IndexEntry。这些IndexEntry的键值与所指向的资源的键值不符。
					 */
					if (itemState == State.FILLED
							|| itemState == State.MODIFIED) {
						// ContextImpl.removeRedundantIndexEntry(item);

						synchronized (item) {
							ResourceIndexEntry indexE = null;
							ResourceEntry entry = item.ownerEntries, last = null;
							while (entry != null) {
								if ((indexE = entry.asIndexEntry()) != null
										&& indexE.state == ResourceIndexEntry.State.REMOEVED) {
									if (last == null) {
										item.ownerEntries = entry.nextSibling;
									} else {
										last.nextSibling = entry.nextSibling;
									}
									entry.remove();
								} else {
									last = entry;
								}
								entry = entry.nextSibling;
							}
						}
					}
					a.release();
				}
				acquirers[i] = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	final void rollback(ExceptionCatcher catcher) {
		if (this.size > 0) {
			this.size = 0;
			final TResourceAcquirer[] acquirers = this.acquirers;
			this.acquirers = null;
			TResourceAcquirer a, b;
			ResourceItem item;
			State itemState;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; b = a, a = a.nextInHolder, b.nextInHolder = null) {
					item = a.res;
					itemState = item.state;
					if (itemState == State.FILLED) {
						item.remove(catcher);
					} else if (itemState == State.MODIFIED
							|| itemState == State.REMOVED) {
						item.tempValues = null;
						item.state = State.RESOLVED;
					} else if (itemState == State.RESOLVED) {
						item.tempValues = null;
					} else {
						a.release();
						Assertion.ASSERT(false, "不应出现的状态：" + item.state);
					}

					/*
					 * 剔除冗余的IndexEntry。
					 */
					if (itemState == State.MODIFIED) {
						synchronized (item) {
							ResourceIndexEntry indexE = null, tempE = null;
							ResourceEntry entry = item.ownerEntries, tempEntry, last;
							while (entry != null) {
								if ((indexE = entry.asIndexEntry()) != null
										&& indexE.state == ResourceIndexEntry.State.REMOEVED) {
									last = null;
									tempEntry = item.ownerEntries;
									while (tempEntry != null) {
										if ((tempE = tempEntry.asIndexEntry()) != null
												&& tempE.holder == indexE.holder) {
											if (last == null) {
												item.ownerEntries = tempEntry.nextSibling;
											} else {
												last.nextSibling = tempEntry.nextSibling;
											}
											tempEntry.remove();
										} else {
											last = tempEntry;
										}
										tempEntry = tempEntry.nextSibling;
									}
								}
								entry = entry.nextSibling;
							}
						}
					}
					a.release();
				}
				acquirers[i] = null;
			}
		}
	}

	// ---------------------------------以下集群相关----------------------------------

	final void buildClusterResourceUpdateTask(
			final NClusterResourceUpdateTask task) {
		if (this.size > 0) {
			final TResourceAcquirer[] acquirers = this.acquirers;
			TResourceAcquirer a;
			ResourceItem<?, ?, ?> item;
			State itemState;
			for (int i = 0, len = acquirers.length; i < len; i++) {
				for (a = acquirers[i]; a != null; a = a.nextInHolder) {
					item = a.res;
					if (item.group.inCluster) {
						itemState = item.state;
						if (itemState == State.FILLED) {
							task.addCreateItemAction(item.group.id, item.id,
									item.impl, item.keys);
						} else if (itemState == State.MODIFIED) {
							Assertion.ASSERT(item.tempValues != null);
							task.addModifyItemAction(item.id,
									item.tempValues.newImpl,
									item.tempValues.newKeys);
						} else if (itemState == State.REMOVED) {
							task.addDeleteItemAction(item.id);
						} else {
							Assertion.ASSERT(itemState != State.RESOLVED,
									"不应出现的状态：" + item.state);
						}
					}
				}
			}
		}
	}

	// ---------------------------------以上集群相关----------------------------------

}
