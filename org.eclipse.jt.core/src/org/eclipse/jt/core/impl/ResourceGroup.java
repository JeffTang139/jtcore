package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAuthorityResourceException;
import org.eclipse.jt.core.impl.ClusterResInfo_TreeEntry.Action;
import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.impl.ResourceServiceBase.AuthorizableResourceProvider;
import org.eclipse.jt.core.invoke.AsyncState;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;
import org.eclipse.jt.core.type.GUID;


/**
 * 资源容器
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 * @param <TImpl>
 * @param <TKeysHolder>
 */
final class ResourceGroup<TFacade, TImpl extends TFacade, TKeysHolder> extends
		Acquirable implements ResourceEntryHolder {
	private final ResourceIndex<TFacade, TImpl, TKeysHolder, ?, ?, ?>[] indexes;

	private void checkIndexes() throws IllegalStateException {
		if (this.indexes.length == 0) {
			throw new IllegalDeclarationException("在资源服务中没有定义任何资源提供器");
		}
	}

	@SuppressWarnings("unchecked")
	final <TOwnerFacade> ResourceItem<TOwnerFacade, ?, ?> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass) {
		if (ownerFacadeClass == null) {
			throw new NullPointerException();
		}
		for (ResourceItem<?, ?, ?> item = this.ownerResource; item != null; item = item.group.ownerResource) {
			if (item.group.resourceService.facadeClass == ownerFacadeClass) {
				return (ResourceItem<TOwnerFacade, ?, ?>) item;
			}
		}
		throw new UnsupportedOperationException("找不到[" + ownerFacadeClass
				+ "]类型的父资源");
	}

	/**
	 * 设置资源项，如果具有相同键值的资源已经存在，则进行替换。
	 * 
	 * @param resourceItem
	 *            资源项
	 */
	final void put(ExceptionCatcher catcher,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		this.checkIndexes();
		int i = 0, len = this.indexes.length;
		try {
			for (; i < len; i++) {
				this.indexes[i].put(resourceItem); // 考虑同步
			}
		} catch (Throwable e) {
			resourceItem.remove(catcher);
			throw Utils.tryThrowException(e);
		}
		// this.idIndex.put(resourceItem);
	}

	@SuppressWarnings("unchecked")
	private void putRedundance(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			TKeysHolder keys) {
		this.checkIndexes();
		ResourceIndexEntry[] redundances = null;
		int rddCount = 0;
		try {
			ResourceIndexEntry rdd;
			for (int i = 0, len = this.indexes.length; i < len; i++) {
				rdd = this.indexes[i].putRedundance(resourceItem, keys);
				if (rdd != null) {
					if (redundances == null) {
						redundances = new ResourceIndexEntry[len];
					}
					redundances[rddCount++] = rdd;
				}
			}
		} catch (Throwable e) {
			ResourceIndexEntry rdd;
			while (rddCount-- > 0) {
				rdd = redundances[rddCount];
				try {
					rdd.remove(); // 纯内存操作，理论上不会出现异常。
				} catch (Throwable ignore) {
				}
				resourceItem.removeEntry(rdd);
			}

			// 还原其它Entry的状态
			synchronized (resourceItem) {
				State itemState = resourceItem.state;
				if (itemState == State.RESOLVED || itemState == State.FILLED
						|| itemState == State.REMOVED) {
					keys = resourceItem.keys; // real value
				} else if (itemState == State.MODIFIED) {
					keys = resourceItem.tempValues.newKeys; // temp value
				} else {
					throw Utils.tryThrowException(e);
				}

				ResourceProviderBase provider;
				ResourceIndexEntry indexEntry;
				ResourceEntry re = resourceItem.ownerEntries;
				while (re != null) {
					if ((indexEntry = re.asIndexEntry()) != null) {
						provider = ((ResourceIndex) indexEntry.holder).provider;
						try {
							if (indexEntry.keysEqual(provider.getKey1(keys),
									provider.getKey2(keys), provider
											.getKey3(keys))) {
								indexEntry.state = null;
							} else {
								indexEntry.state = ResourceIndexEntry.State.REMOEVED;
							}
						} catch (Throwable xxx) {
							// keysEqual方法可能会因为用户的实现而导致异常，从而引发不确定的状态。
							// 理论上，如果会出现，则在上次修改时就会出现，而不会在这里出现。
							// 但为了防止不确定的因素导致出问题，可以另行寻找其它方式解决这里所处理的状态问题。
						}
					}
					re = re.nextSibling;
				}
			}

			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 向缓存中设置资源对象。
	 * 
	 * 如果缓存中已经存在相同键值的理论上有效的资源对象，则根据指定的策略处理。
	 * 
	 * @param context
	 *            上下文对象
	 * @param resource
	 *            资源对象
	 * @param keys
	 *            资源对象对应的键组
	 * @param policy
	 *            当相同键值的资源对象已经存在于缓存时所应采取的处理策略
	 * @return 资源对象设置在缓存中所对应的资源项
	 */
	final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TransactionImpl transaction, TImpl resource, TKeysHolder keys,
			WhenExists policy) {
		return this.putResource(transaction, resource, keys, policy, false, 0L);
	}

	@SuppressWarnings("unchecked")
	private final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TransactionImpl transaction, TImpl resource, TKeysHolder keys,
			WhenExists policy, boolean withID, long id) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		if (!this.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源实现类型有误");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源未实现外观接口");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("资源键组对象未实现键组接口");
		}

		/**
		 * 这里的valid包括：
		 * 
		 * 已经在缓存中，且有效的；
		 * 
		 * 其它线程刚添加或删除且尚未提交事务的； 其它线程正在修改的；
		 * 
		 * 本线程刚添加的； 本线程刚删除的； 本线程正在修改的；
		 */
		ResourceHandleImpl handle;
		ResourceItem<TFacade, TImpl, TKeysHolder> indexItem = null;
		while (true) {
			int i = 0;
			this.modifyLock.lock();
			try {
				for (; i < this.indexes.length; i++) {
					indexItem = this.indexes[i].findResourceItem(keys);
					if (indexItem != null) {
						break;
					}
				}

				if (indexItem == null) {// 添加全新的资源
					ResourceItem<TFacade, TImpl, TKeysHolder> item;
					if (withID) {
						item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
								this, id, resource, keys);
					} else {
						item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
								this, resource, keys);
					}
					// 因为是新new出来的对象，所以可以在同步块中执行这个操作，而不会造成死锁。
					handle = transaction.newResourceHandle(item,
							ResourceDemandFor.MODIFY);
					try {
						this.put(transaction.getExceptionCatcher(), item);
						transaction.addResourceHandle(handle);
						return item;
					} catch (Throwable e) {
						handle.release();
						throw Utils.tryThrowException(e);
					}
				}

				for (int j = i + 1; j < this.indexes.length; j++) {
					ResourceItem<TFacade, TImpl, TKeysHolder> temp = this.indexes[j]
							.findResourceItem(keys);
					if (temp != null && temp != indexItem
							&& temp.state != State.REMOVED) {
						if (indexItem.state == State.REMOVED) {
							indexItem = temp;
							i = j;
						} else {
							throw new ResourceKeysRepeatException(
									this.resourceService.facadeClass,
									this.resourceService.implClass,
									this.resourceService.keysClass,
									this.indexes[i].provider,
									this.indexes[j].provider, indexItem.keys,
									temp.keys, keys);
						}
					}
				}
			} finally {
				this.modifyLock.unlock();
			}

			// 变量PART为真时，表示资源的键与缓存中已有的资源有部分键相同，但另有一部分键是不同的。
			boolean PART = false;
			if (i != 0 || !indexItem.isKeysEqual(keys)) { // 部分键值冲突
				PART = true;
			}

			if (indexItem.state == ResourceItem.State.RESOLVED
					&& policy != WhenExists.REPLACE) {
				if (policy == WhenExists.EXCEPTION) {
					throw new IllegalArgumentException("包含相同键的资源已经存在");
				} else if (policy == WhenExists.IGNORE) {
					return indexItem;
				}
			}

			/**
			 * 如果锁定成功，valid的可能状态有：
			 * 
			 * 已经在缓存中，且有效的；
			 * 
			 * 其它线程删除并已提交了事务的；
			 * 
			 * 本线程刚添加的； 本线程刚删除的； 本线程正在修改的。
			 */
			handle = transaction.tryHandleItemIntoContextIfNot(indexItem,
					ResourceDemandFor.MODIFY);

			if (indexItem.state == State.DISPOSED) {
				handle.removeSelfFromHolderAndRelease();
				continue;
			} else if (indexItem.state == State.EMPTY
					|| indexItem.state == State.PROVIDED) {
				throw new RuntimeException("递归死循环");
			}

			State itemState = indexItem.state;

			// 检查在特定的条件下策略是否冲突
			if (policy != WhenExists.REPLACE
					&& (itemState == State.RESOLVED
							|| itemState == State.FILLED || itemState == State.MODIFIED)) {
				if (policy == WhenExists.EXCEPTION) {
					if (handle != null && itemState == State.RESOLVED) {
						handle.removeSelfFromHolderAndRelease();
					}
					throw new IllegalArgumentException("包含相同键的资源已经存在");
				} else if (policy == WhenExists.IGNORE) {
					if (handle != null && itemState == State.RESOLVED) {
						handle.removeSelfFromHolderAndRelease();
					}
					return indexItem;
				}
			}

			this.modifyLock.lock();
			try {
				itemState = indexItem.state;
				if (PART) {
					if (this.indexes[i].findResourceItem(keys) != indexItem) {
						Assertion.ASSERT(itemState != State.RESOLVED
								&& itemState != State.FILLED
								&& itemState != State.MODIFIED);
						handle.removeSelfFromHolderAndRelease();
						continue;
					}
					try {
						this.putRedundance(indexItem, keys);
					} catch (Throwable e) {
						if (itemState == State.RESOLVED) {
							handle.removeSelfFromHolderAndRelease();
						}
						throw Utils.tryThrowException(e);
					}
				}

				if (itemState == State.FILLED)
				// 本线程刚添加的
				{
					indexItem.impl = resource;
					indexItem.keys = keys;
				} else if (itemState == State.RESOLVED
						|| itemState == State.MODIFIED
						|| itemState == State.REMOVED)
				// 已经在缓存中，且有效的
				// 或者 本线程正在修改的
				// 或者 本线程刚删除，还未提交的
				{
					indexItem.ensureTempValues();
					indexItem.tempValues.newImpl = resource;
					indexItem.tempValues.newKeys = keys;
					indexItem.state = State.MODIFIED;
				} else {
					handle.removeSelfFromHolderAndRelease();
					Assertion.ASSERT(false, "不应出现的状态: " + indexItem.state);
				}
			} finally {
				this.modifyLock.unlock();
			}

			return indexItem;
		}
	}

	// 只有资源初始化过程可以使用些方法。
	final ResourceItem<TFacade, TImpl, TKeysHolder> putAndCommit(
			ContextImpl<TFacade, TImpl, TKeysHolder> context, TImpl resource,
			TKeysHolder keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		if (!this.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源实现类型有误");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源未实现外观接口");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("资源键组对象未实现键组接口");
		}

		/**
		 * 这里的valid只可能包括本线程刚添加且已生效的对象。
		 */
		ResourceItem<TFacade, TImpl, TKeysHolder> indexItem = null;

		int i = 0;
		for (; i < this.indexes.length; i++) {
			indexItem = this.indexes[i].findResourceItem(keys);
			if (indexItem != null) {
				break;
			}
		}

		if (indexItem != null) {
			if (i != 0 || !indexItem.isKeysEqual(keys)) {
				throw new ResourceKeysRepeatException(
						this.resourceService.facadeClass,
						this.resourceService.implClass,
						this.resourceService.keysClass,
						this.resourceService.providers, indexItem.keys, keys);
			} else {
				Assertion.ASSERT(
						indexItem.state == ResourceItem.State.RESOLVED,
						"不应出现的状态: " + indexItem.state);
				// policy 在绝大多数情况下都是REPLACE，所以利用这个判断可使后面的检查代码被短路。
				if (policy != WhenExists.REPLACE) {
					if (policy == WhenExists.EXCEPTION) {
						throw new IllegalArgumentException("包含相同键的资源已经存在");
					} else if (policy == WhenExists.IGNORE) {
						return indexItem;
					}
				}

				indexItem.keys = keys;
				indexItem.impl = resource;
				return indexItem;
			}
		}

		ResourceItem<TFacade, TImpl, TKeysHolder> item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
				this, resource, keys);
		this.put(context.catcher, item);
		item.state = State.RESOLVED;
		return item;
	}

	/**
	 * 使缓存中指定的资源失效，并重新装载该资源。
	 * 
	 * 如果指定的资源未能重新装载，则删除该资源。 如果重新装载成功，则把重新装载出来的资源设置为新添加的状态。
	 * 
	 * @param <TKey1>
	 * @param <TKey2>
	 * @param <TKey3>
	 * @param context
	 *            上下文对象
	 * @param handle
	 *            资源对象的句柄
	 */
	@SuppressWarnings("unchecked")
	final <TKey1, TKey2, TKey3> void invalidResource(
			ContextImpl<?, ?, ?> context,
			ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle) {
		ResourceItem<TFacade, TImpl, TKeysHolder> item = handle.res;
		if (item.state == ResourceItem.State.REMOVED
				|| item.state == ResourceItem.State.DISPOSED) {
			return;
		}

		ResourceIndexEntry indexEntry = null;
		synchronized (item) {
			ResourceEntry<TFacade, ?, ?> entry = item.ownerEntries;
			while (entry != null && (indexEntry = entry.asIndexEntry()) == null) {
				entry = entry.nextSibling;
			}
		}
		Assertion.ASSERT(indexEntry != null, "索引项意外为空（null）");
		ResourceProviderBase provider = null;
		for (int i = 0, len = this.indexes.length; i < len; i++) {
			if (this.indexes[i].containsEntry(indexEntry)) {
				provider = this.indexes[i].provider;
				break;
			}
		}
		Assertion.ASSERT(provider != null, "资源提供器意外为空（null）");

		if (item.state != State.FILLED) {
			item.state = ResourceItem.State.INVALID;
		}
		try {
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> p = provider;
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> e = indexEntry;
			if (provider.isProvideOverridden) {
				context.loadLockedResource(provider, handle, p.getKey1(e), p
						.getKey2(e), p.getKey3(e));
			}
		} finally {
			if (item.state == ResourceItem.State.INVALID) {
				item.state = ResourceItem.State.REMOVED;
			} else {
				if (item.state == State.PROVIDED) {
					item.state = State.MODIFIED;
				}
			}
		}
	}

	final void removePlaceholder(
			ResourceItem<TFacade, TImpl, TKeysHolder> placeholder) {
		if (null != placeholder) {
			List<ResourceEntry<?, ?, ?>> entries = new ArrayList<ResourceEntry<?, ?, ?>>();
			synchronized (placeholder) {
				ResourceEntry<?, ?, ?> entry = placeholder.ownerEntries;
				while (entry != null) {
					entries.add(entry);
					entry = entry.nextSibling;
				}
				placeholder.ownerEntries = null;
			}
			for (int i = 0, len = entries.size(); i < len; i++) {
				entries.get(i).remove();
			}
		}
	}

	final void beginFind() {
		this.findLock.lock();
	}

	final int endFind() {
		this.findLock.unlock();
		return this.modifyVersion;
	}

	final void beginModify() {
		this.modifyLock.lock();
	}

	final int endModify() {
		final int mv = this.modifyVersion++;
		this.modifyLock.unlock();
		return mv;
	}

	/**
	 * 释放资源
	 * 
	 * @param catcher
	 */
	final void reset(ExceptionCatcher catcher, boolean dispose) {
		if (this.state == state_disposed) {
			return;
		}
		this.modifyLock.lock();
		try {
			this.indexes[0].releaseResources(catcher);
			this.state = dispose ? state_none : state_disposed;
		} finally {
			this.modifyVersion++;
			this.modifyLock.unlock();
		}
		if (dispose && this.isGlobalResource) {
			this.resourceService.site.globalResourceContainer
					.resourceGroupDisposed(this);
		}
	}

	/**
	 * 得到资源索引
	 * 
	 * @param index
	 *            索引的位置
	 */
	ResourceIndex<TFacade, TImpl, TKeysHolder, ?, ?, ?> getResourceIndex(
			int index) {
		return this.indexes[index];
	}

	/**
	 * 得到资源索引
	 * 
	 * @param provider
	 *            索引的位置
	 */
	@SuppressWarnings("unchecked")
	<TKey1, TKey2, TKey3> ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> getResourceIndex(
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> provider) {
		this.checkIndexes();
		for (int i = 0, len = this.indexes.length; i < len; i++) {
			if (this.indexes[i].provider == provider) {
				return (ResourceIndex) this.indexes[i];
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 移出某资源
	 */
	@SuppressWarnings("unchecked")
	TImpl lockRemove(TransactionImpl transaction,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		TImpl resource = null;
		if (resourceItem != null) {
			resource = resourceItem.impl;
			ResourceHandleImpl handle = transaction
					.tryHandleItemIntoContextIfNot(resourceItem,
							ResourceDemandFor.MODIFY);
			if (resourceItem.state == State.FILLED
					|| resourceItem.state == State.EMPTY) {
				resourceItem.remove(transaction.getExceptionCatcher());
				handle.removeSelfFromHolderAndRelease();
			} else if (resourceItem.state == State.DISPOSED) {
				handle.removeSelfFromHolderAndRelease();
			} else {
				if (resourceItem.state == State.MODIFIED) {
					Assertion.ASSERT(resourceItem.tempValues != null);
					resource = resourceItem.tempValues.newImpl;
				}
				resourceItem.state = State.REMOVED;
				resourceItem.markReferencesRemoved(transaction);
				/*
				 * REMIND? 关于树节点的标记问题： 这里并没有对树节点打上删除标记。
				 * 如果在后序的操作中又把本资源重新添加进来，那么就会有两种情况出现：<br/> 1.
				 * 如果重新添加时，指定了新的父节点，则会直接移到到新的父节点（当然是事务提交时生效）；<br/> 2.
				 * 如果重新添加时，没有指定新的父节点，
				 * 则会继续保留在原来的位置（可能并不希望这样，或者确实修改了关于父节点数据但忘了在这里指定新的父节点）。
				 */
			}
		}
		return resource;
	}

	/**
	 * 所属资源
	 */
	final ResourceItem<?, ?, ?> ownerResource;
	/**
	 * 资源类别标记
	 */
	final Object category;
	/**
	 * 资源类别标题
	 */
	final String categoryTitle;
	/**
	 * 资源管理器
	 */
	final ResourceServiceBase<TFacade, TImpl, TKeysHolder> resourceService;
	final boolean isGlobalResource;
	final boolean inCluster;

	@SuppressWarnings("unchecked")
	final static ResourceIndex[] emptyResourceIndexes = {};

	/**
	 * 组ID,只有全局资源组才有
	 */
	final GUID groupID;

	final long id;

	/**
	 * 构造函数
	 * 
	 * @param ownerResource
	 * @param resourceService
	 */

	@SuppressWarnings("unchecked")
	ResourceGroup(String categoryTitle, ResourceItem<?, ?, ?> ownerResource,
			ResourceServiceBase<TFacade, TImpl, TKeysHolder> resourceService,
			Object category) {
		if (category == null) {
			throw new NullArgumentException("category");
		}
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.categoryTitle = categoryTitle;
		this.findLock = rwl.readLock();
		this.modifyLock = rwl.writeLock();
		this.resourceService = resourceService;
		this.isGlobalResource = resourceService.kind.isGlobal;
		this.inCluster = resourceService.kind.inCluster;
		this.ownerResource = ownerResource;
		ResourceProviderBase[] ps = resourceService.providers;
		if (ps == null || ps.length == 0) {
			throw new IllegalDeclarationException("在［"
					+ this.resourceService.facadeClass + "］类型的资源服务中没有定义任何资源提供器");
		}
		this.indexes = new ResourceIndex[ps.length];
		for (int i = 0, len = this.indexes.length; i < len; i++) {
			this.indexes[i] = ps[i].newIndex(this);
		}
		this.hash = resourceService.calCategoryHashCode(category);
		this.category = category;
		if (this.isGlobalResource) {
			this.groupID = resourceService.calGroupID(category);
			if (resourceService.isAuthorizable()) {
				this.authResourceIndex = this.indexes[resourceService.authorizableResourceProvider.providerIndex];
			} else {
				this.authResourceIndex = null;
			}
		} else {
			this.authResourceIndex = null;
			this.groupID = null;
		}
		this.id = resourceService.site.globalResourceContainer
				.resourceGroupCreated(this);
	}

	private volatile int state;
	private final static int state_none = 0;
	private final static int state_inint_begin = 1;
	private final static int state_init_failed = 2;
	private final static int state_init_end = 3;
	private final static int state_disposed = 4;

	final boolean isInited() {
		return this.state == ResourceGroup.state_init_end;
	}

	/**
	 * 初始化组，装载资源（如果有）
	 */
	@SuppressWarnings("unchecked")
	final void ensureInit(ContextImpl<?, ?, ?> context) {
		// TODO 保证对ResourceGroup有锁
		// 锁定成功之后，先检查resourceGroup是否已经初始化，如果没有，则调用初始化过程。
		// TODO 同步数据
		// 包括添加的资源项、资源树结点、引用项等

		// TODO 解锁

		if (this.state < state_init_end) {
			this.modifyLock.lock();
			try {
				switch (this.state) {
				case state_none:
					// TODO 去其他机器上锁住该组
					try {
						this.state = state_inint_begin;
						try {
							if (this.inCluster) {
								if (!this
										.tryInitFromCluster((ContextImpl<TFacade, TImpl, TKeysHolder>) context)) {
									context.initResources(this);
									this.trySynInitToCluster();
								}
							} else {
								context.initResources(this);
							}
							this.state = state_init_end;
						} catch (Throwable e) {
							this.state = state_init_failed;
							throw Utils.tryThrowException(e);
						}
					} finally {
						// TODO 去其他机器上同步数据,去其他机器上解锁该组
					}
					break;
				case state_inint_begin:
					throw new IllegalStateException(
							"出现了资源间的循环依赖，请修改程序\r\n资源外观类:"
									+ this.resourceService.facadeClass
											.getName());
				case state_init_failed:
					throw new IllegalStateException("资源初始异常！\r\n资源外观类:"
							+ this.resourceService.facadeClass.getName());
				}
			} finally {
				this.modifyLock.unlock();
			}
		}
	}

	/**
	 * 下一个资源组，用于资源组的HASH表
	 */
	@SuppressWarnings("unchecked")
	ResourceGroup next;
	volatile int modifyVersion;
	final int hash;

	volatile TFacade[] cachedList;
	private static final long CACHEDLIST_OFFSET = Utils.tryGetFieldOffset(
			ResourceGroup.class, "cachedList");

	final boolean trySwapCachedList(TFacade[] newList) {
		if (CACHEDLIST_OFFSET == Utils.ILLEGAL_OFFSET) {
			throw new IllegalAccessError();
		}
		return Unsf.unsafe.compareAndSwapObject(this, CACHEDLIST_OFFSET,
				this.cachedList, newList);
	}

	final void invalidCachedList() {
		this.cachedList = null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// Tree
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * <pre>
	 *     (0)
	 *    / | \
	 *   /  |  \
	 * (1) (2) (3)
	 * 
	 * The node (0) is a node in the tree.
	 * The nodes (1), (2) and (3) are children of the node (0).
	 * The numbers 1, 2, ... are orders that the nodes were added to the tree.
	 * 
	 * (0).child = (1)
	 * (1).parent = (0)
	 * (2).parent = (0)
	 * (3).parent = (0)
	 * (1).next = (2)
	 * (2).next = (3)
	 * (3).next = null
	 * (1).prev = (3)
	 * (2).prev = (1)
	 * (3).prev = (2)
	 * </pre>
	 */

	private ResourceTreeEntry<TFacade, TImpl, TKeysHolder> root;
	private final ReadLock findLock;
	private final WriteLock modifyLock;

	@SuppressWarnings("unchecked")
	final boolean isGroupRootTreeEntry(final ResourceTreeEntry treeEntry) {
		return treeEntry == this.root;
	}

	private void putItemIntoTree(ResourceGroupHandle groupHandle,
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> parent,
			ResourceItem<TFacade, TImpl, TKeysHolder> child) {
		if (child == null || groupHandle == null) {
			throw new NullPointerException();
		}
		if (parent != null) {
			Assertion.ASSERT(parent.newPlace == null, "父结点的占位符意外地不为空（null）");
		}
		this.modifyLock.lock();
		try {
			// 待处理的节点
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> childEntry = child
					.findTreeEntry(groupHandle);
			if (parent == null) {
				if (this.root == null) {
					this.root = new ResourceTreeEntry<TFacade, TImpl, TKeysHolder>(
							this);
				}
				parent = this.root;
			}
			if (childEntry == null) {
				childEntry = new ResourceTreeEntry<TFacade, TImpl, TKeysHolder>(
						this);
				childEntry.resourceItem = child;
				groupHandle.putDirtyEntry(childEntry);
				child.appendEntry(childEntry);
			} else {
				// 检查是否已存在父子关系
				if (parent == childEntry.parent
						|| parent.resourceItem == childEntry.parent.resourceItem) {
					return;
				}

				// 检查循环依赖
				if (parent != this.root) {
					ResourceTreeEntry<TFacade, TImpl, TKeysHolder> p = parent;
					while (p != this.root && p != null) {
						if (p.newPlace != null) {
							p = p.newPlace;
						}
						if (p == childEntry) {
							throw new CycleReferenceException();
						}
						p = p.parent;
					}
				}

				/*
				 * 待处理节点是一个刚刚移动过的占位符，直接移动这个占位符即可。
				 */
				if (childEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
					this.takeOutEntry(childEntry);

					/*
					 * 占位符要移回原来的位置。 把占位符下的子节点合并进原节点中，删除占位符即可。
					 */
					if (((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) childEntry)
							.original().parent == parent) {
						ResourceTreeEntry<TFacade, TImpl, TKeysHolder> origin = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) childEntry)
								.original();
						// merge children
						if (childEntry.child != null) {
							ResourceTreeEntry<TFacade, TImpl, TKeysHolder> temp;
							for (temp = childEntry.child; temp != null; temp = temp.next) {
								temp.parent = origin;
							}
							if (origin.child != null) {
								temp = origin.child.prev;
								temp.next = childEntry.child;
								origin.child.prev = childEntry.child.prev;
								childEntry.child.prev = temp;
							} else {
								origin.child = childEntry.child;
							}
						}
						origin.newPlace = null;
						childEntry.resourceItem.removeEntry(childEntry);
						return;
					}
				} else {
					Assertion.ASSERT(childEntry.newPlace == null,
							"树结点的占位符意外地不为空（null）");
					if (childEntry.state == ResourceTreeEntry.State.NEW) {
						this.takeOutEntry(childEntry);
					} else {
						childEntry.newPlace = childEntry.new RTEPlaceHolder();
						groupHandle.putDirtyEntry(childEntry);
						childEntry = childEntry.newPlace;
						child.appendEntry(childEntry);
					}
				}
			}

			childEntry.parent = parent;
			Assertion.ASSERT(childEntry.prev == null);
			if (parent.child != null) {
				childEntry.prev = parent.child.prev;
				childEntry.prev.next = childEntry;
				parent.child.prev = childEntry;
			} else {
				childEntry.prev = childEntry;
				parent.child = childEntry;
			}
		} finally {
			this.modifyLock.unlock();
		}
	}

	/**
	 * 资源初始化时最终调用此方法构造树形结构。
	 * 
	 * @param parent
	 * @param child
	 */
	private void putItemIntoTreeAndCommit(
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> parent,
			ResourceItem<TFacade, TImpl, TKeysHolder> child) {
		if (child == null) {
			throw new NullPointerException();
		}
		this.modifyLock.lock();
		try {
			// 待处理的节点
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> childEntry = child
					.findTreeEntry(null);
			if (parent == null) {
				if (this.root == null) {
					this.root = new ResourceTreeEntry<TFacade, TImpl, TKeysHolder>(
							this);
				}
				parent = this.root;
			}
			if (childEntry == null) {
				childEntry = new ResourceTreeEntry<TFacade, TImpl, TKeysHolder>(
						this);
				childEntry.resourceItem = child;
				childEntry.state = ResourceTreeEntry.State.RESOLVED;
				child.appendEntry(childEntry);
			} else {
				// 检查是否已存在父子关系
				if (parent == childEntry.parent) {
					return;
				}

				// 检查循环依赖
				if (parent != this.root) {
					ResourceTreeEntry<TFacade, TImpl, TKeysHolder> p = parent;
					while (p != this.root && p != null) {
						Assertion.ASSERT(p.newPlace == null,
								"树结点的占位符意外地不为空（null）");
						if (p == childEntry) {
							throw new CycleReferenceException();
						}
						p = p.parent;
					}
				}

				Assertion.ASSERT(childEntry.newPlace == null,
						"树结点的占位符意外地不为空（null）");
				this.takeOutEntry(childEntry);
			}

			childEntry.parent = parent;
			Assertion.ASSERT(childEntry.prev == null);
			if (parent.child != null) {
				childEntry.prev = parent.child.prev;
				childEntry.prev.next = childEntry;
				parent.child.prev = childEntry;
			} else {
				childEntry.prev = childEntry;
				parent.child = childEntry;
			}
		} finally {
			this.modifyLock.unlock();
		}
	}

	/**
	 * 把指定的toTakeOut从树中摘除，但是不修改toTakeOut的任何属性。
	 * 
	 * 摘除toTakeOut时，其下的全部子Entry也被从树中摘除。这个操作相当于从整棵树中摘除了一棵以toTakeOut为根的子树。
	 * 
	 * @param toTakeOut
	 */
	@SuppressWarnings("unchecked")
	final void takeOutEntry(ResourceTreeEntry toTakeOut) {
		final ResourceTreeEntry parent = toTakeOut.parent;
		if (toTakeOut == parent.child) {
			parent.child = toTakeOut.next;
			if (parent.child != null) {
				parent.child.prev = toTakeOut.prev;
			}
		} else {
			toTakeOut.prev.next = toTakeOut.next;
			if (toTakeOut.next != null) {
				toTakeOut.next.prev = toTakeOut.prev;
			} else {
				parent.child.prev = toTakeOut.prev;
			}
		}
		toTakeOut.parent = toTakeOut.next = toTakeOut.prev = null;
	}

	/**
	 * 删除指定的entry并提交。
	 * 
	 * XXX 调用方需要保证已经以互斥的方式锁定了本ResourceGroup。
	 */
	@SuppressWarnings("unchecked")
	public final void removeEntryCommitly(ResourceEntry<?, ?, ?> entry) {
		Assertion.ASSERT(entry.holder == this && this.root != null,
				"资源项来历不明，或者资源树状态不对");
		final ResourceTreeEntry toDel = (ResourceTreeEntry) entry;
		this.modifyLock.lock();
		try {
			this.takeOutEntry(toDel);
		} finally {
			this.modifyLock.unlock();
		}
		toDel.holder = null;
		toDel.resourceItem = null;
		toDel.newPlace = null;
		toDel.state = ResourceTreeEntry.State.DISPOSED;
	}

	final void putResource(TransactionImpl transaction,
			ResourceItem<TFacade, TImpl, TKeysHolder> treeParent,
			ResourceItem<TFacade, TImpl, TKeysHolder> child) {
		ResourceGroupHandle groupHandle = transaction
				.tryHandleGroupIntoContextIfNot(this);
		this.putItemIntoTree(groupHandle, treeParent == null ? null
				: treeParent.getTreeEntry(groupHandle), child);
	}

	/**
	 * 只有资源初始化过程可以调用此方法。
	 * 
	 * @param context
	 * @param treeParent
	 * @param child
	 */
	final void putResourceAndCommit(
			ResourceItem<TFacade, TImpl, TKeysHolder> treeParent,
			ResourceItem<TFacade, TImpl, TKeysHolder> child) {
		this.putItemIntoTreeAndCommit(treeParent == null ? null : treeParent
				.getTreeEntry(null), child);
	}

	final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TransactionImpl transaction,
			ResourceItem<TFacade, TImpl, TKeysHolder> treeParent,
			TImpl resource, TKeysHolder keys, WhenExists policy) {
		ResourceGroupHandle groupHandle = transaction
				.tryHandleGroupIntoContextIfNot(this);
		ResourceItem<TFacade, TImpl, TKeysHolder> item = this.putResource(
				transaction, resource, keys, policy);
		this.putItemIntoTree(groupHandle, treeParent == null ? null
				: treeParent.getTreeEntry(groupHandle), item);
		return item;
	}

	/**
	 * 只有资源初始化过程可以调用此方法。
	 * 
	 * @param context
	 * @param treeParent
	 * @param resource
	 * @param keys
	 * @param policy
	 * @return
	 */
	final ResourceItem<TFacade, TImpl, TKeysHolder> putAndCommit(
			ContextImpl<TFacade, TImpl, TKeysHolder> context,
			ResourceItem<TFacade, TImpl, TKeysHolder> treeParent,
			TImpl resource, TKeysHolder keys, WhenExists policy) {
		ResourceItem<TFacade, TImpl, TKeysHolder> item = this.putAndCommit(
				context, resource, keys, policy);
		this.putItemIntoTreeAndCommit(treeParent == null ? null : treeParent
				.getTreeEntry(null), item);
		return item;
	}

	/**
	 * @return root在整个树层次中的绝对级次。
	 */
	final int fillTree(TreeNodeImpl<TFacade> rootTreeNode,
			TransactionImpl transaction
	// ,
	// TreeNodeFilter<? super TFacade> filter,
	// SortComparator<? super TFacade> comparator
	) {
		if (rootTreeNode == null) {
			throw new NullArgumentException("rootTreeNode");
		}
		if (this.root != null) {
			this.fillTreeNode(transaction, rootTreeNode, this.root);
			// if (filter == null && comparator == null) {
			// this.fillTreeNode(context, rootNode, this.root);
			// } else {
			// this.fillTreeNode(context, rootNode, this.root, filter,
			// comparator);
			// }
		}
		return 0;
	}

	final int fillTreeNode(TransactionImpl transaction,
			final TreeNodeImpl<TFacade> root,
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry) {
		if (treeEntry == null) {
			throw new NullPointerException();
		}
		boolean handled = transaction.findResGroupHandle(this) != null;
		this.findLock.lock();
		try {
			if (handled) {
				this.fillTreeNodeWithHandle(transaction, root, treeEntry);
			} else {
				this.fillTreeNodeWithoutHandle(transaction, root, treeEntry);
			}
			int level = 0;
			while (treeEntry.parent != null) {
				level++;
				treeEntry = treeEntry.parent;
			}
			return level;
		} finally {
			this.findLock.unlock();
		}
	}

	// 先序遍历
	private final void fillTreeNodeWithoutHandle(TransactionImpl transaction,
			final TreeNodeImpl<TFacade> root,
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry) {
		Assertion
				.ASSERT(!(treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder));
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currEntry = treeEntry.child;
		if (currEntry != null) {
			TreeNodeImpl<TFacade> node = root;
			TreeNodeImpl<TFacade> temp = null;
			TImpl resource;
			do {
				// 定位有效的节点
				while ((currEntry.state != ResourceTreeEntry.State.RESOLVED)
						&& (currEntry.next != null)) {
					currEntry = currEntry.next;
				}

				if (currEntry.state == ResourceTreeEntry.State.RESOLVED) {
					// 取资源
					resource = currEntry.resourceItem.getResource(transaction);
					if (resource != null) {
						temp = node.append(resource);

						// 定位下一个子节点
						if (currEntry.child != null) {
							currEntry = currEntry.child; // push entry
							node = temp; // push node
							continue;
						}
					}

					// 定位下一个兄弟节点
					if (currEntry.next != null) {
						currEntry = currEntry.next;
						continue;
					}
				}

				// 向上定位父级的兄弟节点
				while (currEntry != treeEntry) {
					currEntry = currEntry.parent; // pop entry
					node = node.getParent(); // pop node
					if (currEntry != treeEntry && currEntry.next != null) {
						currEntry = currEntry.next;
						break;
					}
				}
			} while (currEntry != treeEntry);
		}
	}

	// 先序遍历
	private final void fillTreeNodeWithHandle(TransactionImpl transaction,
			final TreeNodeImpl<TFacade> root,
			final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry) {
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currEntry = treeEntry.child;
		if (treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> oEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) treeEntry)
					.original();
			if (oEntry.child != null) {
				currEntry = oEntry.child;
			}
		}
		if (currEntry != null) {
			TreeNodeImpl<TFacade> node = root;
			TreeNodeImpl<TFacade> temp = null;
			TImpl resource;
			do {
				// 定位有效的节点
				while (currEntry.newPlace != null && currEntry.next != null) {
					currEntry = currEntry.next;
				}

				if (currEntry.newPlace == null) {
					// 取资源
					resource = currEntry.resourceItem.getResource(transaction);
					if (resource != null) {
						temp = node.append(resource);

						// 定位下一个子节点
						if (currEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
							currEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) currEntry)
									.original();
							if (currEntry.child == null) {
								currEntry = currEntry.newPlace;
							}
						}
						if (currEntry.child != null) {
							currEntry = currEntry.child; // push entry
							node = temp; // push node
							continue;
						}
					}

					// 定位下一个兄弟节点
					if (currEntry.next != null) {
						currEntry = currEntry.next;
						continue;
					}
				}

				// 向上定位父级的兄弟节点
				while (currEntry != treeEntry) {
					currEntry = currEntry.parent; // pop entry
					if (currEntry.newPlace != null) {
						currEntry = currEntry.newPlace;
						if (currEntry.child != null) {
							currEntry = currEntry.child;
							break;
						}
					}

					node = node.getParent(); // pop node
					if (currEntry != treeEntry && currEntry.next != null) {
						currEntry = currEntry.next;
						break;
					}
				}
			} while (currEntry != treeEntry);
		}
	}

	// final void fillTreeNode(ContextImpl<?, ?, ?> context,
	// final TreeNodeImpl<TFacade> root,
	// final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry,
	// TreeNodeFilter<? super TFacade> filter,
	// SortComparator<? super TFacade> comparator) {
	// if (treeEntry == null) {
	// throw new NullPointerException();
	// }
	// boolean handled = context.findResGroupHandle(this) != null;
	// this.findLock.lock();
	// try {
	// if (handled) {
	// this.fillTreeNodeWithHandle(context, root, treeEntry, filter,
	// comparator);
	// } else {
	// this.fillTreeNodeWithoutHandle(context, root, treeEntry,
	// filter, comparator);
	// }
	// } finally {
	// this.findLock.unlock();
	// }
	// }
	//
	// // 先序遍历
	// private final void fillTreeNodeWithoutHandle(ContextImpl<?, ?, ?>
	// context,
	// final TreeNodeImpl<TFacade> root,
	// ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry,
	// TreeNodeFilter<? super TFacade> filter,
	// SortComparator<? super TFacade> comparator) {
	// Assertion
	// .ASSERT(!(treeEntry instanceof ResourceTreeEntry<?, ?,
	// ?>.RTEPlaceHolder));
	// Acception acp = null;
	// int absoluteLevel = treeEntry.getLevel(), relativeLevel = 0;
	// ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currEntry =
	// treeEntry.child;
	// absoluteLevel++;
	// relativeLevel++;
	// if (currEntry != null) {
	// TreeNodeImpl<TFacade> node = root;
	// TreeNodeImpl<TFacade> temp = null;
	// TImpl resource;
	// do {
	// acp = Acception.ALL;
	//
	// // 定位有效的节点
	// while ((currEntry.state != ResourceTreeEntry.State.RESOLVED)
	// && (currEntry.next != null)) {
	// currEntry = currEntry.next;
	// }
	// if (currEntry.state == ResourceTreeEntry.State.RESOLVED) {
	// // 取资源
	// resource = currEntry.resourceItem.getResource(context);
	// if (resource != null) {
	// if (filter != null) {
	// acp = filter.accept(resource, absoluteLevel,
	// relativeLevel);
	// if (acp == Acception.ALL
	// || acp == Acception.NO_CHILDREN) {
	// temp = node.append(resource);
	// }
	// // acp == null, accept nothing.
	// } else {
	// temp = node.append(resource);
	// }
	//
	// // 定位下一个子节点
	// if (currEntry.child != null && acp == Acception.ALL) {
	// currEntry = currEntry.child; // push entry
	// absoluteLevel++;
	// relativeLevel++;
	// node = temp; // push node
	// continue;
	// }
	// }
	//
	// // 定位下一个兄弟节点
	// if (currEntry.next != null) {
	// currEntry = currEntry.next;
	// continue;
	// }
	// }
	//
	// // 向上定位父级的兄弟节点
	// while (currEntry != treeEntry) {
	// currEntry = currEntry.parent; // pop entry
	// absoluteLevel--;
	// relativeLevel--;
	// if (comparator != null) {
	// node.sortChildren(comparator);
	// }
	// node = node.getParent(); // pop node
	// if (currEntry != treeEntry && currEntry.next != null) {
	// currEntry = currEntry.next;
	// break;
	// }
	// }
	// } while (currEntry != treeEntry);
	// }
	// }
	//
	// // 先序遍历
	// private final void fillTreeNodeWithHandle(ContextImpl<?, ?, ?> context,
	// final TreeNodeImpl<TFacade> root,
	// ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry,
	// TreeNodeFilter<? super TFacade> filter,
	// SortComparator<? super TFacade> comparator) {
	// Acception acp = null;
	// int absoluteLevel = treeEntry.getLevel(), relativeLevel = 0;
	// ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currEntry =
	// treeEntry.child;
	// if (treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
	// ResourceTreeEntry<TFacade, TImpl, TKeysHolder> oEntry =
	// ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder)
	// treeEntry)
	// .original();
	// if (oEntry.child != null) {
	// currEntry = oEntry.child;
	// }
	// }
	// absoluteLevel++;
	// relativeLevel++;
	// if (currEntry != null) {
	// TreeNodeImpl<TFacade> node = root;
	// TreeNodeImpl<TFacade> temp = null;
	// TImpl resource;
	// do {
	// acp = Acception.ALL;
	//
	// // 定位有效的节点
	// while (currEntry.newPlace != null && currEntry.next != null) {
	// currEntry = currEntry.next;
	// }
	//
	// if (currEntry.newPlace == null) {
	// resource = currEntry.resourceItem.getResource(context);
	// // 取资源
	// if (resource != null) {
	// if (filter != null) {
	// acp = filter.accept(resource, absoluteLevel,
	// relativeLevel);
	// if (acp == Acception.ALL
	// || acp == Acception.NO_CHILDREN) {
	// temp = node.append(resource);
	// }
	// // if acp == null, then accept nothing.
	// } else {
	// temp = node.append(resource);
	// }
	//
	// // 定位下一个子节点
	// if (acp == Acception.ALL) {
	// if (currEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
	// currEntry = ((ResourceTreeEntry<TFacade, TImpl,
	// TKeysHolder>.RTEPlaceHolder) currEntry)
	// .original();
	// if (currEntry.child == null) {
	// currEntry = currEntry.newPlace;
	// }
	// }
	// if (currEntry.child != null) {
	// currEntry = currEntry.child; // push entry
	// node = temp; // push node
	// continue;
	// }
	// }
	// }
	//
	// // 定位下一个兄弟节点
	// if (currEntry.next != null) {
	// currEntry = currEntry.next;
	// continue;
	// }
	// }
	//
	// // 向上定位父级的兄弟节点
	// while (currEntry != treeEntry) {
	// currEntry = currEntry.parent; // pop entry
	// if (currEntry.newPlace != null) {
	// currEntry = currEntry.newPlace;
	// if (currEntry.child != null) {
	// currEntry = currEntry.child;
	// break;
	// }
	// }
	// absoluteLevel--;
	// relativeLevel--;
	// if (comparator != null) {
	// node.sortChildren(comparator);
	// }
	// node = node.getParent(); // pop node
	// if (currEntry != treeEntry && currEntry.next != null) {
	// currEntry = currEntry.next;
	// break;
	// }
	// }
	// } while (currEntry != treeEntry);
	// }
	// }

	// /////////////////////////////////////////////////////////////////////////
	// Cluster
	// /////////////////////////////////////////////////////////////////////////

	@Deprecated
	final void broadcastInitedData(LocalCluster localCluster) {
		if (this.inCluster) {
			this.modifyLock.lock();
			try {
				// items
				// this.idIndex.broadcastInitedData(localCluster);

				// tree
				ResourceTreeEntry<TFacade, TImpl, TKeysHolder> entry = this.root;
				if (entry != null) {
					Class<?> facadeClass = this.resourceService.facadeClass;
					entry = entry.child;
					// 先序遍历
					while (entry != null && entry != this.root) {
						// 广播当前节点
						localCluster.broadcast(new ClusterResInfo_TreeEntry(
								this.category, facadeClass, None.NONE,
								entry.parent.getResourceItemId(), entry
										.getResourceItemId(), Action.INIT));

						// 定位下一个节点
						if (entry.child != null) {
							entry = entry.child;
						} else if (entry.next != null) {
							entry = entry.next;
						} else {
							do {
								entry = entry.parent;
								if (entry.next != null) {
									entry = entry.next;
									break;
								}
							} while (entry != this.root);
						}
					}
				}
			} finally {
				this.modifyLock.unlock();
			}
		}
	}

	// ------------------------------以下为权限相关-------------------------------------------

	final void lockFillList(Operation<? super TFacade> operation,
			DnaArrayList<TFacade> to, TransactionImpl transaction) {
		this.indexes[0].lockFillResources(operation, to, transaction);
	}

	/**
	 * @return root在整个树层次中的绝对级次。
	 */
	final int lockFillTree(Operation<? super TFacade> operation,
			TreeNodeImpl<TFacade> rootTreeNode, TransactionImpl transaction) {
		if (rootTreeNode == null) {
			throw new NullArgumentException("rootTreeNode");
		}
		if (this.root != null) {
			return this.lockFillTreeNode(operation, transaction, rootTreeNode,
					this.root);
		}
		return 0;
	}

	final int lockFillTreeNode(Operation<? super TFacade> operation,
			TransactionImpl transaction, final TreeNodeImpl<TFacade> root,
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry) {
		if (this.isAuthorizable()) {
			if (treeEntry == null) {
				throw new NullPointerException();
			}
			final ContextImpl<?, ?, ?> ctx = transaction.getCurrentContext();
			final IInternalUser user = ctx.session.internalGetUser();
			if (user.isBuildInUser()) {
				boolean auth = ((InternalUser) user).getAuthority();
				if (auth) {
					return this.fillTree(root, transaction);
				} else {
					return 0;
				}
			}
			final boolean handled = transaction.findResGroupHandle(this) != null;
			final OperationEntry opEntry = this.resourceService
					.getOperationEntry(operation);
			this.resourceService.beforeAccessAuthorityResource(ctx);
			try {
				this.findLock.lock();
				try {
					final UserAuthorityCheckerImpl authChecker = ctx
							.getCurrentUserOperationAuthorityChecker();
					final long[][] acl = authChecker.acl;
					final boolean defaultAuth;
					if (treeEntry == this.root) {
						defaultAuth = this.resourceService.getDefaultAuth();
					} else {
						defaultAuth = authChecker.hasAuthority(operation,
								treeEntry.resourceItem);
					}
					if (handled) {
						if (treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
							treeEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) treeEntry)
									.original();
						}
						this.fillTreeNodeWithHandle(opEntry, transaction, root,
								treeEntry, acl, defaultAuth);
					} else {
						Assertion
								.ASSERT(!(treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder));
						this.fillTreeNodeWithoutHandle(opEntry, transaction,
								root, treeEntry, acl, defaultAuth);
					}
					int level = 0;
					while (treeEntry.parent != null) {
						level++;
						treeEntry = treeEntry.parent;
					}
					return level;
				} finally {
					this.findLock.unlock();
				}
			} finally {
				this.resourceService.endAccessAuthorityResource(ctx);
			}
		} else {
			throw new UnsupportedAuthorityResourceException(
					this.resourceService.facadeClass);
		}
	}

	private final void fillTreeNodeWithoutHandle(final OperationEntry opEntry,
			final TransactionImpl transaction,
			final TreeNodeImpl<TFacade> root,
			final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry,
			final long[][] acl, final boolean parentAuth) {
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currTreeEntry = treeEntry.child;
		if (currTreeEntry == null) {
			return;
		}
		while ((currTreeEntry.state != ResourceTreeEntry.State.RESOLVED)
				&& (currTreeEntry.next != null)) {
			currTreeEntry = currTreeEntry.next;
		}
		while (currTreeEntry != null) {
			if (currTreeEntry.state == ResourceTreeEntry.State.RESOLVED) {
				TreeNodeImpl<TFacade> tempTreeNode;
				final boolean currAuth = currTreeEntry.resourceItem
						.internalValidateAuthority_Item(opEntry, acl,
								parentAuth);
				TImpl resource = null;
				if (currAuth) {
					// 允许
					resource = currTreeEntry.resourceItem
							.getResource(transaction);
				}
				tempTreeNode = root.append(resource);
				if (currTreeEntry.child != null) {
					this.fillTreeNodeWithoutHandle(opEntry, transaction,
							tempTreeNode, currTreeEntry, acl, currAuth);
				}
			}
			currTreeEntry = currTreeEntry.next;
		}
	}

	private final void fillTreeNodeWithHandle(final OperationEntry opEntry,
			final TransactionImpl transaction,
			final TreeNodeImpl<TFacade> root,
			final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry,
			final long[][] acl, final boolean parentAuth) {
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> currTreeEntry = treeEntry.child;
		if (currTreeEntry == null) {
			return;
		}
		while (currTreeEntry.newPlace != null && currTreeEntry.next != null) {
			currTreeEntry = currTreeEntry.next;
		}
		while (currTreeEntry != null) {
			if (currTreeEntry.newPlace == null) {
				TreeNodeImpl<TFacade> tempTreeNode;
				final boolean currAuth = currTreeEntry.resourceItem
						.internalValidateAuthority_Item(opEntry, acl,
								parentAuth);
				TImpl resource = null;
				if (currAuth) {
					// 允许
					resource = currTreeEntry.resourceItem
							.getResource(transaction);
				}
				tempTreeNode = root.append(resource);
				if (currTreeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
					ResourceTreeEntry<TFacade, TImpl, TKeysHolder> oEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) currTreeEntry)
							.original();
					if (oEntry.child != null) {
						this.fillTreeNodeWithHandle(opEntry, transaction,
								tempTreeNode, oEntry, acl, currAuth);
					}
				}
				if (currTreeEntry.child != null) {
					this.fillTreeNodeWithHandle(opEntry, transaction,
							tempTreeNode, currTreeEntry, acl, currAuth);
				}
			}
			currTreeEntry = currTreeEntry.next;
		}
	}

	@SuppressWarnings("unchecked")
	private final ResourceIndex authResourceIndex;

	/**
	 * 是否权限控制
	 */
	final boolean isAuthorizable() {
		return this.authResourceIndex != null;
	}

	/**
	 * 根据authID查找对应的资源项
	 */
	@SuppressWarnings("unchecked")
	final ResourceItem<?, ?, ?> findAuthResourceItem(
			TransactionImpl transaction, GUID authID) {
		if (this.authResourceIndex != null) {
			return this.authResourceIndex.lockGet(authID, null, null,
					transaction);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final GUID tryGetAuthID(ResourceItem<?, ?, ?> item) {
		final AuthorizableResourceProvider authorizableResourceProvider = this.resourceService.authorizableResourceProvider;
		if (authorizableResourceProvider != null) {
			return (GUID) authorizableResourceProvider.getKey1(item.keys);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final String tryGetAuthTitle(ResourceItem<?, ?, ?> item) {
		final AuthorizableResourceProvider authorizableResourceProvider = this.resourceService.authorizableResourceProvider;
		if (authorizableResourceProvider != null) {
			final String title = authorizableResourceProvider.getResourceTitle(
					item.impl, item.keys);
			return title == null ? "" : title;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final ResourceEntry<?, ?, ?> getResItemChildren(
			ContextImpl<?, ?, ?> context, GUID authID) {
		if (this.root == null) {
			return this.authResourceIndex.lockNextOf(null, context.transaction);
		}
		if (authID != null) {
			final ResourceItem<?, ?, ?> rootResItem = this
					.findAuthResourceItem(context.transaction, authID);
			if (rootResItem != null) {
				return rootResItem.internalGetChildren(context.transaction);
			}
		}
		return this.root.child;
	}

	final int generateAuthorityInfo(long[][] acl) {
		if (this.isAuthorizable()) {
			int authInfo = 0;
			boolean auth;
			final boolean defaultAuth = this.resourceService.getDefaultAuth();
			for (OperationEntry opEntry : this.resourceService.authorizableResourceProvider.operations) {
				int result = ACLHelper.getAuthCode(acl[0], this.id)
						& opEntry.authMask;
				if (result != 0) {
					if (result == opEntry.allowAuthCode) {
						auth = true;
					} else {
						auth = false;
					}
				} else {
					syn: {
						for (int index = 1, size = acl.length; index < size; index++) {
							long[] roleACL = acl[index];
							result = ACLHelper.getAuthCode(roleACL, this.id)
									& opEntry.authMask;
							if (result != 0) {
								if (result == opEntry.allowAuthCode) {
									auth = true;
								} else {
									auth = false;
								}
								break syn;
							}
						}
						auth = defaultAuth;
					}
				}
				authInfo <<= 1;
				if (auth) {
					authInfo |= 1;
				}
			}
			return authInfo;
		} else {
			throw new UnsupportedAuthorityResourceException(
					this.resourceService.facadeClass);
		}
	}

	// ------------------------------以上为权限相关-------------------------------------------

	// ------------------------------以下集群相关----------------------------------
	final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TransactionImpl transaction, TImpl resource, TKeysHolder keys,
			WhenExists policy, long id) {
		return this.putResource(transaction, resource, keys, policy, true, id);
	}

	final void acquireGroupExclusiveLock() {
		this.modifyLock.lock();
	}

	final void releaseGroupExclusiveLock() {
		this.modifyLock.unlock();
	}

	/**
	 * 初始化成功返回true，否则返回false
	 * 
	 * @return
	 */
	private final boolean tryInitFromCluster(
			final ContextImpl<TFacade, TImpl, TKeysHolder> context) {
		NetNodeImpl netNode = this.resourceService.site.application
				.getNetCluster().getFirstNetNode();
		NetSessionImpl netSession;
		AsyncTask<NClusterResourceInitTask, None> mainTask;
		AsyncTask<NClusterResourceInitTask, None> task;
		if (netNode != null) {
			try {
				ArrayList<AsyncTask<NClusterResourceInitTask, None>> taskList = new ArrayList<AsyncTask<NClusterResourceInitTask, None>>();
				netSession = netNode.newSession();
				mainTask = netSession.newRequest(new NClusterResourceInitTask(
						this.id, true, true, true), None.NONE);
				taskList.add(mainTask);
				netNode = netNode.getNextNodeInCluster();
				while (netNode != null) {
					netSession = netNode.newSession();
					task = netSession.newRequest(new NClusterResourceInitTask(
							this.id, true, true, false), None.NONE);
					taskList.add(task);
					netNode = netNode.getNextNodeInCluster();
				}
				ContextImpl.internalWaitFor(0L, null, taskList
						.toArray(new AsyncTask[taskList.size()]));
				if (mainTask.getState() == AsyncState.FINISHED) {
					NClusterResourceInitTask initTask = mainTask.getTask();
					if (initTask.isGroupInited()) {
						System.out.println("从其它节点初始化资源["
								+ this.resourceService.facadeClass + "]");
						this.initResourceItemFrom(context, initTask);
						return true;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// 只有资源初始化过程可以使用些方法。
	final ResourceItem<TFacade, TImpl, TKeysHolder> putAndCommit(
			ContextImpl<TFacade, TImpl, TKeysHolder> context, long itemID,
			TImpl resource, TKeysHolder keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		if (!this.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源实现类型有误");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("资源未实现外观接口");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("资源键组对象未实现键组接口");
		}

		/**
		 * 这里的valid只可能包括本线程刚添加且已生效的对象。
		 */
		ResourceItem<TFacade, TImpl, TKeysHolder> indexItem = null;

		int i = 0;
		for (; i < this.indexes.length; i++) {
			indexItem = this.indexes[i].findResourceItem(keys);
			if (indexItem != null) {
				break;
			}
		}

		if (indexItem != null) {
			if (i != 0 || !indexItem.isKeysEqual(keys)) {
				throw new ResourceKeysRepeatException(
						this.resourceService.facadeClass,
						this.resourceService.implClass,
						this.resourceService.keysClass,
						this.resourceService.providers, indexItem.keys, keys);
			} else {
				Assertion.ASSERT(
						indexItem.state == ResourceItem.State.RESOLVED,
						"不应出现的状态: " + indexItem.state);
				// policy 在绝大多数情况下都是REPLACE，所以利用这个判断可使后面的检查代码被短路。
				if (policy != WhenExists.REPLACE) {
					if (policy == WhenExists.EXCEPTION) {
						throw new IllegalArgumentException("包含相同键的资源已经存在");
					} else if (policy == WhenExists.IGNORE) {
						return indexItem;
					}
				}

				indexItem.keys = keys;
				indexItem.impl = resource;
				return indexItem;
			}
		}

		ResourceItem<TFacade, TImpl, TKeysHolder> item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
				this, itemID, resource, keys);
		this.put(context.catcher, item);
		item.state = State.RESOLVED;
		return item;
	}

	final int addResourceItemTo(final NClusterResourceInitTask task) {
		return this.indexes[0].addResourceItemTo(task);
	}

	final void addResourceTreeTo(final NClusterResourceInitTask task) {
		ResourceTreeEntry<?, ?, ?> entry = this.root;
		if (entry == null || entry.child == null) {
			return;
		}
		this.internalAddResourceTree(entry.child, task.getResourceTree());
	}

	private final void internalAddResourceTree(
			ResourceTreeEntry<?, ?, ?> treeEntry,
			final TreeNodeImpl<Long> treeNode) {
		do {
			TreeNodeImpl<Long> newTreeNode = new TreeNodeImpl<Long>(treeNode,
					treeEntry.resourceItem.id);
			treeNode.appendChild(newTreeNode);
			if (treeEntry.child != null) {
				this.internalAddResourceTree(treeEntry.child, newTreeNode);
			}
			treeEntry = treeEntry.next;
		} while (treeEntry != null);
	}

	@SuppressWarnings("unchecked")
	final void initResourceItemFrom(
			final ContextImpl<TFacade, TImpl, TKeysHolder> context,
			final NClusterResourceInitTask task) {
		final ArrayList<NClusterResourceInitTask.ResourceItem> itemList = task
				.clearResourceList();
		if (itemList != null && itemList.size() != 0) {
			for (NClusterResourceInitTask.ResourceItem item : itemList) {
				this.putAndCommit(context, item.id, (TImpl) (item.resource),
						(TKeysHolder) (item.keysHolder), WhenExists.EXCEPTION);
			}
		}
		final GlobalResourceContainer globalContainer = this.resourceService.site.globalResourceContainer;
		final ArrayList<NClusterResourceInitTask.ReferenceStorage> storageList = task
				.clearReferenceStorageList();
		if (storageList != null && storageList.size() != 0) {
			for (NClusterResourceInitTask.ReferenceStorage storage : storageList) {
				if (storage.getReferenceCount() == 0) {
					continue;
				}
				final ResourceItem<?, ?, ?> item = globalContainer
						.find(storage.holderLongID);
				final ResourceGroup<?, ?, ?> group = globalContainer
						.findResourceGroup(storage.refrenceGroupLongID);
				if (item == null || group == null) {
					continue;
				}
				group.ensureInit(context);
				for (long referenceLongID : storage.getReferenceList()) {
					final ResourceItem<?, ?, ?> reference = globalContainer
							.find(referenceLongID);
					if (reference == null) {
						continue;
					}
					item.putReferenceAndCommit(reference);
				}
			}
		}
		final TreeNodeImpl<Long> tree = task.clearResourceTree();
		if (tree != null && tree.getChildCount() != 0) {
			this.internalInitTree(context.transaction, null, tree,
					globalContainer);
		}
	}

	@SuppressWarnings("unchecked")
	private final void internalInitTree(final TransactionImpl transaction,
			final ResourceItem<TFacade, TImpl, TKeysHolder> parentItem,
			final TreeNodeImpl<Long> treeNode,
			final GlobalResourceContainer globalContainer) {
		for (int index = 0, count = treeNode.getChildCount(); index < count; index++) {
			final TreeNodeImpl<Long> child = treeNode.getChild(index);
			final ResourceItem<TFacade, TImpl, TKeysHolder> item = (ResourceItem<TFacade, TImpl, TKeysHolder>) globalContainer
					.find(child.getElement());
			if (item == null) {
				continue;
			}
			this.putResource(transaction, parentItem, item);
			if (child.getChildCount() != 0) {
				this
						.internalInitTree(transaction, item, child,
								globalContainer);
			}
		}
	}

	final void trySynInitToCluster() {
		NetNodeImpl netNode = this.resourceService.site.application
				.getNetCluster().getFirstNetNode();
		if (netNode != null) {
			NetSessionImpl netSession;
			AsyncTask<NClusterResourceInitTask, None> task;
			final NClusterResourceInitTask initTask = new NClusterResourceInitTask(
					this.id, false, false, false);
			this.addResourceItemTo(initTask);
			ArrayList<AsyncTask<NClusterResourceInitTask, None>> taskList = new ArrayList<AsyncTask<NClusterResourceInitTask, None>>();
			do {
				netSession = netNode.newSession();
				task = netSession.newRequest(initTask, None.NONE);
				taskList.add(task);
				netNode = netNode.getNextNodeInCluster();
			} while (netNode != null);
			try {
				ContextImpl.internalWaitFor(0, null, taskList
						.toArray(new AsyncTask[taskList.size()]));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// ------------------------------以上集群相关----------------------------------

}
