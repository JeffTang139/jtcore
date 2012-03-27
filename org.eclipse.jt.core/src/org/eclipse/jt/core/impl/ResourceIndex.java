package org.eclipse.jt.core.impl;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAuthorityResourceException;
import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.HashUtil;


/**
 * 资源的哈西键表
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 * @param <TImpl>
 * @param <TKeysHolder>
 */
final class ResourceIndex<TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2, TKey3>
		implements ResourceEntryHolder {

	/**
	 * 资源提供器
	 */
	final ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> provider;
	/**
	 * 索引所属的资源组
	 */
	final ResourceGroup<TFacade, TImpl, TKeysHolder> group;

	/**
	 * @param group
	 *            - 索引所属的资源组
	 * @param provider
	 *            - 索引对应的提供器
	 */
	ResourceIndex(
			ResourceGroup<TFacade, TImpl, TKeysHolder> group,
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> provider) {
		if (group == null) {
			throw new NullArgumentException("group");
		}
		if (provider == null) {
			throw new NullArgumentException("provider");
		}
		this.provider = provider;
		this.group = group;
	}

	/**
	 * 检查是否有指定的Entry，只比较引用。
	 * 
	 * @param entry
	 * @return
	 */
	final boolean containsEntry(ResourceIndexEntry<TFacade, ?, ?, ?, ?, ?> entry) {
		if (this.resourceEntrySize == 0) {
			return false;
		}
		for (ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> e = this.resourceEntries[entry.hash
				& (this.resourceEntries.length - 1)]; e != null; e = e.next) {
			if (entry == e) {
				return true;
			}
		}
		return false;
	}

	final ResourceIndexEntry<TFacade, ?, ?, ?, ?, ?> lockNextOf(
			final ResourceIndexEntry<TFacade, ?, ?, ?, ?, ?> entry,
			TransactionImpl transaction) {
		if (entry != null && entry.holder != this) {
			throw new IllegalArgumentException();
		}
		ResourceIndexEntry<TFacade, ?, ?, ?, ?, ?> e;
		this.group.beginFind();
		try {
			ResourceItem<?, ?, ?> item;
			Acquirer<?, ?> acq = null;
			int index = 0;
			if (entry != null) {
				e = entry.next;
				while (e != null) {
					item = e.resourceItem;
					if (item != null) {
						acq = item.acquirer;
						if (acq != null && acq.similarTransaction(transaction) ? (item.state == State.REMOVED || e.state == ResourceIndexEntry.State.REMOEVED)
								: item.state == State.FILLED) {
							e = e.next;
							continue;
						}
					}
					return e;
				}

				index = entry.hash
						& (this.resourceEntries == null ? -1
								: this.resourceEntries.length - 1);
				index++;
			}

			if (this.resourceEntries != null) {
				for (int len = this.resourceEntries.length; index < len; index++) {
					e = this.resourceEntries[index];
					while (e != null) {
						item = e.resourceItem;
						if (item != null) {
							acq = item.acquirer;
							if (acq != null
									&& acq.similarTransaction(transaction) ? (item.state == State.REMOVED || e.state == ResourceIndexEntry.State.REMOEVED)
									: item.state == State.FILLED) {
								e = e.next;
								continue;
							}
						}
						return e;
					}
				}
			}

			return null;
		} finally {
			this.group.endFind();
		}
	}

	/**
	 * 返回的结果包括的范围：
	 * 
	 * 已经在缓存中，且有效的；
	 * 
	 * 其它线程正在装入的；
	 * 
	 * 其它线程刚添加或删除且尚未提交事务的； 其它线程正在修改的；
	 * 
	 * 本线程刚添加的； 本线程刚删除的； 本线程正在修改的。
	 * 
	 * ！！！靠调用者对group加锁。
	 */
	ResourceItem<TFacade, TImpl, TKeysHolder> findResourceItem(TKeysHolder keys) {
		TKey1 key1 = this.provider.getKey1(keys);
		TKey2 key2 = this.provider.getKey2(keys);
		TKey3 key3 = this.provider.getKey3(keys);
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> entry = this
				.findEntry(key1, key2, key3, HashUtil.hash(key1, key2, key3));
		return entry == null ? null : entry.resourceItem;
	}

	private void internalLockFillResources(DnaArrayList<TFacade> to,
			TransactionImpl transaction) {
		this.group.beginFind();
		try {
			if (this.resourceEntrySize > 0) {
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry;
				TImpl resource;
				for (int i = 0, len = this.resourceEntries.length; i < len; i++) {
					entry = this.resourceEntries[i];
					while (entry != null) {
						/*
						 * 无论是否是当前线程，只要修改了资源的键且事务尚未提交，就会出现entry的状态为已删除的状况，
						 * 并且，在相同的index内还会有另一个有效的entry存在，它们拥有相同的Item，
						 * 所以遇到这种情况时，可以直接忽略状态为已删除的entry， 以防止同一个资源项被处理多次。
						 */
						if (entry.state != ResourceIndexEntry.State.REMOEVED) {
							resource = entry.resourceItem
									.getResource(transaction);
							if (resource != null) {
								to.add(resource);
							}
						}
						entry = entry.next;
					}
				}
			}
		} finally {
			this.group.endFind();
		}
	}

	// ------------------------------以下为权限相关-------------------------------------------
	final void lockFillResources(Operation<? super TFacade> operation,
			DnaArrayList<TFacade> to, TransactionImpl transaction) {
		if (this.group.isAuthorizable()) {
			final ContextImpl<?, ?, ?> ctx = transaction.getCurrentContext();
			if (ctx.session.getUser() == InternalUser.debugger) {
				this.lockFillResources(to, transaction);
				return;
			}
			final OperationEntry opEntry = this.group.resourceService
					.getOperationEntry(operation);
			this.group.resourceService.beforeAccessAuthorityResource(ctx);
			try {
				this.group.beginFind();
				final long[][] acl = ctx
						.getCurrentUserOperationAuthorityChecker().acl;
				final boolean defaultAuth = this.group.resourceService
						.getDefaultAuth();
				try {
					if (this.resourceEntrySize > 0) {
						ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry;
						ResourceItem<TFacade, TImpl, TKeysHolder> rItem;
						for (int i = 0, len = this.resourceEntries.length; i < len; i++) {
							entry = this.resourceEntries[i];
							while (entry != null) {
								/*
								 * 无论是否是当前线程，只要修改了资源的键且事务尚未提交，就会出现entry的状态为已删除的状况
								 * ， 并且，在相同的index内还会有另一个有效的entry存在，它们拥有相同的Item，
								 * 所以遇到这种情况时，可以直接忽略状态为已删除的entry， 以防止同一个资源项被处理多次。
								 */
								if (entry.state != ResourceIndexEntry.State.REMOEVED) {
									rItem = entry.resourceItem;
									final boolean currAuth = rItem
											.validateAuthority(transaction,
													opEntry, acl, defaultAuth);
									if (currAuth) {
										final TImpl resource = rItem
												.getResource(transaction);
										if (resource != null) {
											to.add(resource);
										}
									}
								}
								entry = entry.next;
							}
						}
					}
				} finally {
					this.group.endFind();
				}
			} finally {
				this.group.resourceService.endAccessAuthorityResource(ctx);
			}
		} else {
			throw new UnsupportedAuthorityResourceException(
					this.group.resourceService.facadeClass);
		}
	}

	// ------------------------------以上为权限相关-------------------------------------------

	/**
	 * 线程安全地将索引下的资源填充到列表中
	 * 
	 * @return 如果进行过缺省的排序返回<code>true</code>，否则返回<code>false</code>。
	 */
	@SuppressWarnings("unchecked")
	final boolean lockFillResources(final DnaArrayList<TFacade> to,
			TransactionImpl transaction) {
		final Comparator<TFacade> comparator = (Comparator) this.group.resourceService.defaultSortComparator;
		if (comparator == null
				|| transaction.hasResHandleAboutGroup(this.group)) {
			this.internalLockFillResources(to, transaction);
			return false;
		}
		for (;;) {
			final TFacade[] cachedList = this.group.cachedList;
			if (cachedList == null) {
				this.internalLockFillResources(to, transaction);
				final int size = to.size();
				final TFacade[] newCachedList;
				if (size > 0) {
					TFacade[] data = to.getElementData();
					Arrays.sort(data, 0, size, comparator);
					newCachedList = (TFacade[]) new Object[size];
					System.arraycopy(data, 0, newCachedList, 0, size);
				} else {
					newCachedList = (TFacade[]) DnaArrayList.EMPTY_OBJECT_ARRAY;
				}
				if (this.group.trySwapCachedList(newCachedList)) {
					return true;
				}
			} else {
				to.copyData(cachedList, cachedList.length);
				return true;
			}
		}
	}

	/**
	 * 线程安全的获取资源项，不在索引中则返回null
	 */
	ResourceItem<TFacade, TImpl, TKeysHolder> lockFind(
			ContextImpl<?, ?, ?> context, TKey1 key1, TKey2 key2, TKey3 key3) {
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> entry = null;
		ResourceItem<TFacade, TImpl, TKeysHolder> item = null;
		this.group.beginFind();
		try {
			entry = this.findEntry(key1, key2, key3, HashUtil.hash(key1, key2,
					key3));
		} finally {
			this.group.endFind();
		}
		if (entry != null) {
			item = entry.resourceItem;
			if (item != null) {
				boolean effect;
				Acquirer<?, ?> acquirer;
				do {
					effect = true;
					acquirer = item.acquirer;
					if (acquirer != null) {
						// FIXME 集群中thread有可能为空，需要做处理。
						effect = context.transaction.belongs(acquirer) ? (item.state != State.REMOVED && entry.state != ResourceIndexEntry.State.REMOEVED)
								: item.state != State.FILLED;
					}
				} while (acquirer != item.acquirer);
				if (effect) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 线程安全的获取资源项，不在索引中则调用资源提供器提供
	 */
	ResourceItem<TFacade, TImpl, TKeysHolder> lockGet(TKey1 key1, TKey2 key2,
			TKey3 key3, TransactionImpl transaction) {
		final int hash = HashUtil.hash(key1, key2, key3);
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry = null;
		ResourceItem<TFacade, TImpl, TKeysHolder> item = null, lastItem = null;
		// boolean isLocalLock = false;
		int modifyVersion;
		ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle;
		while (true) {
			entry = null;
			item = null;
			handle = null;
			this.group.beginFind();
			try {
				entry = this.findEntry(key1, key2, key3, hash);
				if (entry != null) {
					item = entry.resourceItem;
					if (item != null && lastItem == item) {
						throw new AssertionError("死循环");
					}
					lastItem = item;
				}
			} finally {
				modifyVersion = this.group.endFind();
			}

			if (item == null) {
				if (!this.provider.isProvideOverridden) {
					return null;
				}
				this.group.beginModify();
				try {
					if (modifyVersion != this.group.modifyVersion) {
						entry = this.findEntry(key1, key2, key3, hash);
					}
					if (entry == null) {
						entry = this.put(hash,
								new ResourceItem<TFacade, TImpl, TKeysHolder>(
										this.group), key1, key2, key3, false);
						handle = transaction.newResourceHandle(
								entry.resourceItem, ResourceDemandFor.MODIFY);
						break;
					} else {
						item = entry.resourceItem;
					}
				} finally {
					this.group.endModify();
				}
			}

			if (item.state == State.RESOLVED) {
				return item;
			}

			boolean isLocalLock = false;
			handle = transaction.findResourceHandle(item,
					ResourceDemandFor.READ);
			if (handle == null) {
				handle = transaction.newResourceHandle(item,
						ResourceDemandFor.READ);
				isLocalLock = true;
			}
			if (item.state == State.RESOLVED) {
				handle.release();
				if (entry.state == ResourceIndexEntry.State.REMOEVED) {
					// Assertion.ASSERT(!item.holdedByEntry(entry));
					continue;
				}
				return item;
			}
			if (item.state == State.FILLED || item.state == State.MODIFIED) {
				if (isLocalLock) {
					handle.release();
					throw new AssertionError("状态为FILLED或MODIFIED时不应该锁住");
				}
				if (entry.state == ResourceIndexEntry.State.REMOEVED) {
					return null;
				}
				return item;
			}
			if (item.state == State.REMOVED) {
				if (isLocalLock) {
					handle.release();
					throw new AssertionError("状态为REMOVED时不应该锁住");
				}
				return null; // REMIND? 可能还有其它路径可选
			}
			if (isLocalLock) {
				handle.release();
				handle = null;
			}

			if (item.state != State.DISPOSED) {
				throw new AssertionError("状态应为 DISPOSED ：" + item.state);
			}
		}

		ResourceItem<TFacade, TImpl, TKeysHolder> currItem = entry.resourceItem;
		if (currItem == null) {
			return null;
		}
		ResourceItem<TFacade, TImpl, TKeysHolder> entryItem;
		do {
			if (handle == null) {
				/*
				 * 锁定Item
				 */
				handle = transaction.newResourceHandle(currItem,
						ResourceDemandFor.MODIFY);
			}

			/*
			 * 检查本线程锁定的Item是否依然有效
			 */
			entryItem = entry.resourceItem;
			if (entryItem == null || entryItem.state == State.DISPOSED) {
				handle.release();
				return null;
			}

			if (entryItem.state == ResourceItem.State.RESOLVED) {
				handle.release();
				return entryItem; // 缓存中的Item是有效的
			}

			/*
			 * 本线程锁定的Item无效，需要重新锁定
			 */
			if (entryItem != currItem) {
				handle.release();
				currItem = entryItem;
				handle = null;
				continue;
			}

			break; // 这时候已经锁定了正确的Item
		} while (true);

		final ContextImpl<?, ?, ?> ctx = transaction.getCurrentContext();
		final ExceptionCatcher exceptionCatcher = ctx.catcher;
		try {
			transaction.addResourceHandle(handle);
			ctx.loadLockedResource(this.provider, handle, key1, key2, key3);
		} catch (Throwable e) {
			try {
				if (currItem.state == State.EMPTY
						|| currItem.state == State.PROVIDED) {
					this.group.removePlaceholder(currItem);
				}
				handle.res.dispose(exceptionCatcher);
			} finally {
				handle.removeSelfFromHolderAndRelease();
			}
			throw Utils.tryThrowException(e);
		}

		if (currItem.state == State.EMPTY) {
			try {
				this.group.removePlaceholder(currItem);
				handle.res.dispose(exceptionCatcher);
			} finally {
				handle.removeSelfFromHolderAndRelease();
			}
			return null;
		}

		this.group.beginModify();
		try {
			entryItem = entry.resourceItem;
			if (entryItem == currItem) {
				this.group.put(exceptionCatcher, currItem);
				currItem.state = State.FILLED;
				return currItem;
			}
		} catch (Throwable e) {
			try {
				currItem.dispose(exceptionCatcher);
			} finally {
				handle.removeSelfFromHolderAndRelease();
			}
			throw Utils.tryThrowException(e);
		} finally {
			this.group.endModify();
		}

		try {
			handle.res.dispose(exceptionCatcher);
		} finally {
			handle.removeSelfFromHolderAndRelease();
		}

		if (entryItem == null) {
			return null;
		} else if (entryItem.state == ResourceItem.State.RESOLVED) {
			return entryItem;
		} else {
			handle = transaction.newResourceHandle(entryItem,
					ResourceDemandFor.READ);
			try {
				if (entryItem.state == State.RESOLVED) {
					return entryItem;
				} else {
					return null;
				}
			} finally {
				handle.release();
			}
		}
	}

	/**
	 * 将资源项放入索引中，该资源项必须是有效的，且索引中没有具有相同键值的项。
	 */
	@SuppressWarnings("unchecked")
	void put(ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		synchronized (resourceItem) {
			ResourceEntry e = resourceItem.ownerEntries;
			ResourceIndexEntry entry = null;
			while (e != null) {
				if ((entry = e.asIndexEntry()) != null && entry.holder == this) {
					return;
				}
				e = e.nextSibling;
			}
		}
		TKeysHolder keys = resourceItem.keys;
		TKey1 key1 = this.provider.getKey1(keys);
		TKey2 key2 = this.provider.getKey2(keys);
		TKey3 key3 = this.provider.getKey3(keys);
		this.put(HashUtil.hash(key1, key2, key3), resourceItem, key1, key2,
				key3, true);
	}

	/**
	 * 把指定的资源项放入索引存储中。
	 * 
	 * @param hash
	 *            资源对象对于当前索引键值的哈希值。
	 * @param resourceItem
	 *            要加入索引存储的资源项。
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return 指定的资源项加入本索引存储后对应的索引条目。
	 */
	@SuppressWarnings("unchecked")
	private ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> put(
			final int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem, TKey1 key1,
			TKey2 key2, TKey3 key3, final boolean checkExisted) {
		/*
		 * 确定脊的当前长度。
		 */
		int oldLen;
		if (this.resourceEntries == null) {
			oldLen = 4;
			this.resourceEntries = new ResourceIndexEntry[oldLen];
		} else {
			oldLen = this.resourceEntries.length;
		}

		// 定位散列桶。
		int index = hash & (oldLen - 1);

		if (checkExisted) {
			/*
			 * 确认是否有相同键值的资源条目存在。
			 */
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry = this.resourceEntries[index];
			while (entry != null) {
				// 满足条件时，表示索引存储中已有与指定资源的键值相同的资源存在。
				if (entry.hash == hash && entry.keysEqual(key1, key2, key3)) {
					// 如果是同一个资源项，直接返回。
					if (entry.resourceItem == resourceItem) {
						return entry;
					}

					/*
					 * 确定要保留的资源项。
					 */
					ResourceItem currItem = entry.resourceItem;
					if (currItem.state == State.EMPTY
							|| currItem.state == State.PROVIDED) {
						// 保留新的资源项。
						entry.resourceItem = resourceItem;
						synchronized (resourceItem) {
							entry.nextSibling = resourceItem.ownerEntries;
							resourceItem.ownerEntries = entry;
						}
						currItem.state = State.DISPOSED;
						return entry;
					} else {
						throw new ResourceKeysRepeatException(
								this.group.resourceService.facadeClass,
								this.group.resourceService.implClass,
								this.group.resourceService.keysClass,
								this.group.resourceService.providers,
								currItem.keys, resourceItem.keys);
					}
				}
				entry = entry.next;
			}
		}

		/**
		 * 添加新的资源条目。
		 */
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newEntry = this.resourceEntries[index] = this.provider
				.newIndexEntry(this, hash, resourceItem,
						this.resourceEntries[index], key1, key2, key3);
		synchronized (resourceItem) {
			newEntry.nextSibling = resourceItem.ownerEntries;
			resourceItem.ownerEntries = newEntry;
		}
		if (++this.resourceEntrySize > oldLen * 0.75) {
			int newLen = oldLen * 2;
			ResourceIndexEntry[] newTable = new ResourceIndexEntry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (ResourceIndexEntry e = this.resourceEntries[j], next; e != null; e = next) {
					index = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[index];
					newTable[index] = e;
				}
			}
			this.resourceEntries = newTable;
		}
		return newEntry;
	}

	/**
	 * 添加资源的冗余项。
	 * 
	 * @return 添加的冗余项（如果未添加冗余，则返回空（<code>null</code>））。
	 */
	@SuppressWarnings("unchecked")
	ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> putRedundance(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			TKeysHolder keys) {
		TKey1 key1 = this.provider.getKey1(keys);
		TKey2 key2 = this.provider.getKey2(keys);
		TKey3 key3 = this.provider.getKey3(keys);
		final int hash = HashUtil.hash(key1, key2, key3);

		int oldLen = this.resourceEntries.length;
		int index = hash & (oldLen - 1);
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry = this.resourceEntries[index];
		while (entry != null) {
			if (entry.hash == hash && entry.keysEqual(key1, key2, key3)) {
				Assertion.ASSERT(entry.resourceItem == resourceItem);
				if (entry.state == ResourceIndexEntry.State.REMOEVED) {
					entry.state = null;
					synchronized (resourceItem) {
						ResourceEntry e = resourceItem.ownerEntries;
						while (e != null) {
							if (e.holder == this && e != entry) {
								e.asIndexEntry().state = ResourceIndexEntry.State.REMOEVED;
							}
							e = e.nextSibling;
						}
					}
				}
				return null;
			}
			entry = entry.next;
		}

		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newEntry = this.resourceEntries[index] = this.provider
				.newIndexEntry(this, hash, resourceItem,
						this.resourceEntries[index], key1, key2, key3);

		synchronized (resourceItem) {
			ResourceEntry e = resourceItem.ownerEntries;
			while (e != null) {
				if (e.holder == this) {
					e.asIndexEntry().state = ResourceIndexEntry.State.REMOEVED;
				}
				e = e.nextSibling;
			}

			newEntry.nextSibling = resourceItem.ownerEntries;
			resourceItem.ownerEntries = newEntry;
		}

		if (++this.resourceEntrySize > oldLen * 0.75) {
			int newLen = oldLen * 2;
			ResourceIndexEntry[] newTable = new ResourceIndexEntry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (ResourceIndexEntry e = this.resourceEntries[j], next; e != null; e = next) {
					index = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[index];
					newTable[index] = e;
				}
			}
			this.resourceEntries = newTable;
		}

		return newEntry;
	}

	/**
	 * 释放索引中的全部资源
	 */
	void releaseResources(ExceptionCatcher catcher) {
		if (this.resourceEntrySize > 0) {
			for (int i = 0; i < this.resourceEntries.length; i++) {
				for (ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> e = this.resourceEntries[i]; e != null; e = e.next) {
					e.resourceItem.dispose(catcher);
				}
			}
			this.resourceEntries = null;
			this.resourceEntrySize = 0;
		}
	}

	@SuppressWarnings("unchecked")
	public final void removeEntryCommitly(ResourceEntry<?, ?, ?> entry) {
		Assertion.ASSERT(entry.holder == this, "来历不明的资源项");
		final ResourceIndexEntry toDel = (ResourceIndexEntry) entry;
		Assertion.ASSERT(this.resourceEntrySize > 0, "意外地没有资源项");
		this.group.beginModify();
		try {
			int index = toDel.hash & (this.resourceEntries.length - 1);
			for (ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> e = this.resourceEntries[index], last = null; e != null; last = e, e = e.next) {
				if (e == toDel) {
					e.resourceItem = null;
					if (last == null) {
						this.resourceEntries[index] = e.next;
					} else {
						last.next = e.next;
					}
					toDel.holder = null;
					toDel.resourceItem = null;
					this.resourceEntrySize--;
					return;
				}
			}
			System.err.println("没有找到要删除的Entry"); // XXX 不确定有没有重复删除的情况。
		} finally {
			this.group.endModify();
		}
	}

	private int resourceEntrySize;
	private ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>[] resourceEntries;

	private ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> findEntry(
			TKey1 key1, TKey2 key2, TKey3 key3, int hash) {
		if (this.resourceEntrySize > 0) {
			for (ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> e = this.resourceEntries[hash
					& (this.resourceEntries.length - 1)]; e != null; e = e.next) {
				if (hash == e.hash && e.keysEqual(key1, key2, key3)) {
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * 资源的个数，包括已经失效的。
	 */
	int size() {
		return this.resourceEntrySize;
	}

	/**
	 * 检查是否有资源，包括已经失效的。
	 */
	boolean isEmpty() {
		return this.resourceEntrySize == 0;
	}

	// // ------------------------------以下集群相关----------------------------------

	final int addResourceItemTo(final NClusterResourceInitTask task) {
		if (this.resourceEntries == null) {
			return 0;
		}
		int count = 0;
		for (ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry : this.resourceEntries) {
			while (entry != null) {
				ResourceItem<TFacade, TImpl, TKeysHolder> item = entry.resourceItem;
				if (item != null) {
					task.addResourceItem(item.id, item.impl, item.keys);
					item.tryAddReferenceStorageTo(task);
					count++;
				}
				entry = entry.next;
			}
		}
		return count;
	}
	// ------------------------------以上集群相关----------------------------------

}
