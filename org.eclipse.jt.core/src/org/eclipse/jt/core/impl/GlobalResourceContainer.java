package org.eclipse.jt.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.invoke.AsyncState;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.type.GUID;


/**
 * 全局资源容器<br>
 * 最高4位是集群节点ID<br>
 * 次高29位是删除版本<br>
 * 低31位表示ID，最多只能同时存在2^31个资源
 * 
 * @author Jeff Tang
 * 
 */
final class GlobalResourceContainer {
	/**
	 * 个数
	 */
	private volatile int resourceItemSize;
	/**
	 * 哈西表
	 */
	private volatile ResourceItem<?, ?, ?>[] resourceItemHashTable;
	/**
	 * 资源ID
	 */
	private final AtomicLong newResourceItemID;

	/**
	 * 资源组ID
	 */
	private final AtomicLong newResourceGroupID;
	private final ReadLock resourceGroupReadLock;
	private final WriteLock resourceGroupWriteLock;
	private final ReadLock resourceItemReadLock;
	private final WriteLock resourceItemWriteLock;

	/**
	 * 资源项创建时调用，分配id并将全局资源放入哈西表
	 */
	final long resourceItemCreated(ResourceItem<?, ?, ?> item) {
		if (!item.group.isGlobalResource) {
			return 0l;
		}
		final long newID = this.newResourceItemID.incrementAndGet();
		this.internalResourceItemCreated(item, newID);
		return newID;
	}

	final void resourceItemCreatedWithID(ResourceItem<?, ?, ?> item, long itemID) {
		if (!item.group.isGlobalResource) {
			return;
		}
		this.internalResourceItemCreated(item, itemID);
	}

	private final void internalResourceItemCreated(ResourceItem<?, ?, ?> item,
			long itemID) {
		this.resourceItemWriteLock.lock();
		try {
			ResourceItem<?, ?, ?>[] hashTable = this.resourceItemHashTable;
			int hashL = hashTable.length;
			final int hashH;
			if (this.resourceItemSize >= hashL) {
				hashL *= 2;
				hashTable = new ResourceItem<?, ?, ?>[hashL];
				hashH = hashL - 1;
				for (ResourceItem<?, ?, ?> old : this.resourceItemHashTable) {
					while (old != null) {
						final ResourceItem<?, ?, ?> next = old.nextInIDHashTable;
						final int index = (int) old.id & hashH;
						old.nextInIDHashTable = hashTable[index];
						hashTable[index] = old;
						old = next;
					}
				}
				this.resourceItemHashTable = hashTable;
			} else {
				hashH = hashL - 1;
			}
			final int index = (int) itemID & hashH;
			item.nextInIDHashTable = hashTable[index];
			hashTable[index] = item;
			this.resourceItemSize++;
		} finally {
			this.resourceItemWriteLock.unlock();
		}
	}

	/**
	 * 资源对象销毁时调用，将资源项从全局哈西表中移除
	 */
	final boolean resourceItemDisposed(ResourceItem<?, ?, ?> item) {
		final long id = item.id;
		if (id == 0l) {
			return false;
		}
		this.resourceItemWriteLock.lock();
		try {
			if (this.resourceItemSize > 0) {
				final ResourceItem<?, ?, ?>[] hashTable = this.resourceItemHashTable;
				int index = (int) id & (hashTable.length - 1);
				for (ResourceItem<?, ?, ?> one = hashTable[index], last = null; one != null; last = one, one = one.nextInIDHashTable) {
					if (item == one) {
						if (last != null) {
							last.nextInIDHashTable = one.nextInIDHashTable;
							one.nextInIDHashTable = null;
						} else {
							hashTable[index] = one.nextInIDHashTable;
						}
						return true;
					}
				}
			}
		} finally {
			this.resourceItemWriteLock.unlock();
		}
		return false;
	}

	/**
	 * 根据id查找全局资源项
	 */
	final ResourceItem<?, ?, ?> find(long resourceItemID) {
		this.resourceItemReadLock.lock();
		try {
			if (this.resourceItemSize > 0) {
				final ResourceItem<?, ?, ?>[] hashTable = this.resourceItemHashTable;
				final int index = ((int) resourceItemID)
						& (hashTable.length - 1);
				for (ResourceItem<?, ?, ?> item = hashTable[index]; item != null; item = item.nextInIDHashTable) {
					if (item.id == resourceItemID) {
						return item;
					}
				}
			}
		} finally {
			this.resourceItemReadLock.unlock();
		}
		return null;
	}

	/**
	 * 有权限要求的资源组，使用较少，直接用HashMap
	 */
	private final HashMap<GUID, ResourceGroup<?, ?, ?>> authResourceGroups;

	private final LongKeyMap<ResourceGroup<?, ?, ?>> resourceGroups;

	final ResourceGroup<?, ?, ?>[] getAllAuthGroup() {
		this.resourceGroupReadLock.lock();
		try {
			final Collection<ResourceGroup<?, ?, ?>> values = this.authResourceGroups
					.values();
			return values.toArray(new ResourceGroup<?, ?, ?>[values.size()]);
		} finally {
			this.resourceGroupReadLock.unlock();
		}
	}

	final long getResourceGroupLongIDFromCLuster(final NetClusterImpl cluster,
			final GUID groupGUID) {
		final NetNodeImpl netNode = cluster.getFirstNetNode();
		final NetSessionImpl netSession = netNode.newSession();
		final NetTaskRequestImpl<NClusterGetResGroupLongIDTask, None> asyncTask = netSession
				.newRequest(new NClusterGetResGroupLongIDTask(groupGUID),
						None.NONE);
		try {
			asyncTask.waitStop(0);
			if (asyncTask.getState() == AsyncState.FINISHED) {
				final NClusterGetResGroupLongIDTask task = asyncTask.getTask();
				if (task.getClusterTaskState() == ClusterSynTask.State.HANDLE_SUCCESSED) {
					return task.getResourceGroupLongID();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 资源组创建完后调用
	 */
	final long resourceGroupCreated(ResourceGroup<?, ?, ?> group) {
		if (!group.isGlobalResource) {
			return 0l;
		}
		long newGroupID;
		allocGroupID: {
			// 如果集群中有其它节点，从其它节点获取资源组LongID
			if (group.resourceService.kind == ResourceKind.SINGLETON_IN_CLUSTER) {
				final NetClusterImpl cluster = this.site.application
						.getNetCluster();
				final NetNodeImpl netNode = cluster.getFirstNetNode();
				if (netNode != null) {
					final NetSessionImpl netSession = netNode.newSession();
					final NetTaskRequestImpl<NClusterGetResGroupLongIDTask, None> asyncTask = netSession
							.newRequest(new NClusterGetResGroupLongIDTask(
									group.groupID), None.NONE);
					try {
						asyncTask.waitStop(0);
						if (asyncTask.getState() == AsyncState.FINISHED) {
							final NClusterGetResGroupLongIDTask task = asyncTask
									.getTask();
							newGroupID = task.getResourceGroupLongID();
							break allocGroupID;
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				newGroupID = this.newResourceGroupID.incrementAndGet();
			} else {
				newGroupID = this.newResourceGroupID.incrementAndGet();
			}
		}
		this.resourceGroupWriteLock.lock();
		try {
			if (group.isAuthorizable()) {
				ResourceGroup<?, ?, ?> old = this.authResourceGroups.put(
						group.groupID, group);
				if (old != null) {
					this.authResourceGroups.put(old.groupID, old);
					new Exception("权限类别ID冲突：["
							+ group.resourceService.getClass().getName() + ":"
							+ group.category + "]==["
							+ old.resourceService.getClass().getName() + ":"
							+ old.category + "]").printStackTrace();
				}
			}
			this.resourceGroups.put(newGroupID, group);
		} finally {
			this.resourceGroupWriteLock.unlock();
		}
		return newGroupID;
	}

	/**
	 * 资源组销毁后调用
	 * 
	 * @param group
	 */
	final boolean resourceGroupDisposed(ResourceGroup<?, ?, ?> group) {
		final long id = group.id;
		if (id == 0l) {
			return false;
		}
		this.resourceGroupWriteLock.lock();
		try {
			if (group.isAuthorizable()) {
				ResourceGroup<?, ?, ?> g2 = this.authResourceGroups
						.remove(group.groupID);
				if (g2 != null && g2 != group) {
					this.authResourceGroups.put(g2.groupID, g2);
					return false;
				}
			}
			this.resourceGroups.remove(group.id);
		} finally {
			this.resourceGroupWriteLock.unlock();
		}
		return true;
	}

	/**
	 * 尝试查找权限资源组，如果未初始则初始
	 * 
	 * @param context
	 * @param groupID
	 * @return
	 */
	final ResourceGroup<?, ?, ?> findAndResolveAuthResourceGroup(
			ContextImpl<?, ?, ?> context, GUID groupID) {
		final ResourceGroup<?, ?, ?> g;
		this.resourceGroupReadLock.lock();
		try {
			g = this.authResourceGroups.get(groupID);
		} finally {
			this.resourceGroupReadLock.unlock();
		}
		if (g != null) {
			g.ensureInit(context);
		}
		return g;
	}

	final ResourceGroup<?, ?, ?> findAuthResourceGroup(GUID groupID) {
		final ResourceGroup<?, ?, ?> g;
		this.resourceGroupReadLock.lock();
		try {
			g = this.authResourceGroups.get(groupID);
		} finally {
			this.resourceGroupReadLock.unlock();
		}
		return g;
	}

	final ResourceGroup<?, ?, ?> findResourceGroup(long groupID) {
		this.resourceGroupReadLock.lock();
		try {
			return this.resourceGroups.get(groupID);
		} finally {
			this.resourceGroupReadLock.unlock();
		}
	}

	final Site site;

	GlobalResourceContainer(Site site) {
		final int clusterNodeIndex = site.application.getNetCluster().thisClusterNodeIndex;
		this.site = site;
		// 集群节点ID放入最高4位
		this.newResourceItemID = new AtomicLong(((long) clusterNodeIndex) << 60);
		this.newResourceGroupID = new AtomicLong(
				((long) clusterNodeIndex) << 60);
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.resourceGroupReadLock = rwl.readLock();
		this.resourceGroupWriteLock = rwl.writeLock();
		rwl = new ReentrantReadWriteLock();
		this.resourceItemReadLock = rwl.readLock();
		this.resourceItemWriteLock = rwl.writeLock();
		this.resourceItemHashTable = new ResourceItem[1024 * 64];
		this.authResourceGroups = new HashMap<GUID, ResourceGroup<?, ?, ?>>();
		this.resourceGroups = new LongKeyMap<ResourceGroup<?, ?, ?>>();
	}
}
