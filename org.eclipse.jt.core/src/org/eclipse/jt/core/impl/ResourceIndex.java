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
 * ��Դ�Ĺ�������
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
	 * ��Դ�ṩ��
	 */
	final ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> provider;
	/**
	 * ������������Դ��
	 */
	final ResourceGroup<TFacade, TImpl, TKeysHolder> group;

	/**
	 * @param group
	 *            - ������������Դ��
	 * @param provider
	 *            - ������Ӧ���ṩ��
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
	 * ����Ƿ���ָ����Entry��ֻ�Ƚ����á�
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
	 * ���صĽ�������ķ�Χ��
	 * 
	 * �Ѿ��ڻ����У�����Ч�ģ�
	 * 
	 * �����߳�����װ��ģ�
	 * 
	 * �����̸߳���ӻ�ɾ������δ�ύ����ģ� �����߳������޸ĵģ�
	 * 
	 * ���̸߳���ӵģ� ���̸߳�ɾ���ģ� ���߳������޸ĵġ�
	 * 
	 * �������������߶�group������
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
						 * �����Ƿ��ǵ�ǰ�̣߳�ֻҪ�޸�����Դ�ļ���������δ�ύ���ͻ����entry��״̬Ϊ��ɾ����״����
						 * ���ң�����ͬ��index�ڻ�������һ����Ч��entry���ڣ�����ӵ����ͬ��Item��
						 * ���������������ʱ������ֱ�Ӻ���״̬Ϊ��ɾ����entry�� �Է�ֹͬһ����Դ������Ρ�
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

	// ------------------------------����ΪȨ�����-------------------------------------------
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
								 * �����Ƿ��ǵ�ǰ�̣߳�ֻҪ�޸�����Դ�ļ���������δ�ύ���ͻ����entry��״̬Ϊ��ɾ����״��
								 * �� ���ң�����ͬ��index�ڻ�������һ����Ч��entry���ڣ�����ӵ����ͬ��Item��
								 * ���������������ʱ������ֱ�Ӻ���״̬Ϊ��ɾ����entry�� �Է�ֹͬһ����Դ������Ρ�
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

	// ------------------------------����ΪȨ�����-------------------------------------------

	/**
	 * �̰߳�ȫ�ؽ������µ���Դ��䵽�б���
	 * 
	 * @return ������й�ȱʡ�����򷵻�<code>true</code>�����򷵻�<code>false</code>��
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
	 * �̰߳�ȫ�Ļ�ȡ��Դ������������򷵻�null
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
						// FIXME ��Ⱥ��thread�п���Ϊ�գ���Ҫ������
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
	 * �̰߳�ȫ�Ļ�ȡ��Դ������������������Դ�ṩ���ṩ
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
						throw new AssertionError("��ѭ��");
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
					throw new AssertionError("״̬ΪFILLED��MODIFIEDʱ��Ӧ����ס");
				}
				if (entry.state == ResourceIndexEntry.State.REMOEVED) {
					return null;
				}
				return item;
			}
			if (item.state == State.REMOVED) {
				if (isLocalLock) {
					handle.release();
					throw new AssertionError("״̬ΪREMOVEDʱ��Ӧ����ס");
				}
				return null; // REMIND? ���ܻ�������·����ѡ
			}
			if (isLocalLock) {
				handle.release();
				handle = null;
			}

			if (item.state != State.DISPOSED) {
				throw new AssertionError("״̬ӦΪ DISPOSED ��" + item.state);
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
				 * ����Item
				 */
				handle = transaction.newResourceHandle(currItem,
						ResourceDemandFor.MODIFY);
			}

			/*
			 * ��鱾�߳�������Item�Ƿ���Ȼ��Ч
			 */
			entryItem = entry.resourceItem;
			if (entryItem == null || entryItem.state == State.DISPOSED) {
				handle.release();
				return null;
			}

			if (entryItem.state == ResourceItem.State.RESOLVED) {
				handle.release();
				return entryItem; // �����е�Item����Ч��
			}

			/*
			 * ���߳�������Item��Ч����Ҫ��������
			 */
			if (entryItem != currItem) {
				handle.release();
				currItem = entryItem;
				handle = null;
				continue;
			}

			break; // ��ʱ���Ѿ���������ȷ��Item
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
	 * ����Դ����������У�����Դ���������Ч�ģ���������û�о�����ͬ��ֵ���
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
	 * ��ָ������Դ����������洢�С�
	 * 
	 * @param hash
	 *            ��Դ������ڵ�ǰ������ֵ�Ĺ�ϣֵ��
	 * @param resourceItem
	 *            Ҫ���������洢����Դ�
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return ָ������Դ����뱾�����洢���Ӧ��������Ŀ��
	 */
	@SuppressWarnings("unchecked")
	private ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> put(
			final int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem, TKey1 key1,
			TKey2 key2, TKey3 key3, final boolean checkExisted) {
		/*
		 * ȷ�����ĵ�ǰ���ȡ�
		 */
		int oldLen;
		if (this.resourceEntries == null) {
			oldLen = 4;
			this.resourceEntries = new ResourceIndexEntry[oldLen];
		} else {
			oldLen = this.resourceEntries.length;
		}

		// ��λɢ��Ͱ��
		int index = hash & (oldLen - 1);

		if (checkExisted) {
			/*
			 * ȷ���Ƿ�����ͬ��ֵ����Դ��Ŀ���ڡ�
			 */
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry = this.resourceEntries[index];
			while (entry != null) {
				// ��������ʱ����ʾ�����洢��������ָ����Դ�ļ�ֵ��ͬ����Դ���ڡ�
				if (entry.hash == hash && entry.keysEqual(key1, key2, key3)) {
					// �����ͬһ����Դ�ֱ�ӷ��ء�
					if (entry.resourceItem == resourceItem) {
						return entry;
					}

					/*
					 * ȷ��Ҫ��������Դ�
					 */
					ResourceItem currItem = entry.resourceItem;
					if (currItem.state == State.EMPTY
							|| currItem.state == State.PROVIDED) {
						// �����µ���Դ�
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
		 * ����µ���Դ��Ŀ��
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
	 * �����Դ�������
	 * 
	 * @return ��ӵ���������δ������࣬�򷵻ؿգ�<code>null</code>������
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
	 * �ͷ������е�ȫ����Դ
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
		Assertion.ASSERT(entry.holder == this, "������������Դ��");
		final ResourceIndexEntry toDel = (ResourceIndexEntry) entry;
		Assertion.ASSERT(this.resourceEntrySize > 0, "�����û����Դ��");
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
			System.err.println("û���ҵ�Ҫɾ����Entry"); // XXX ��ȷ����û���ظ�ɾ���������
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
	 * ��Դ�ĸ����������Ѿ�ʧЧ�ġ�
	 */
	int size() {
		return this.resourceEntrySize;
	}

	/**
	 * ����Ƿ�����Դ�������Ѿ�ʧЧ�ġ�
	 */
	boolean isEmpty() {
		return this.resourceEntrySize == 0;
	}

	// // ------------------------------���¼�Ⱥ���----------------------------------

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
	// ------------------------------���ϼ�Ⱥ���----------------------------------

}
