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
 * ��Դ����
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
			throw new IllegalDeclarationException("����Դ������û�ж����κ���Դ�ṩ��");
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
		throw new UnsupportedOperationException("�Ҳ���[" + ownerFacadeClass
				+ "]���͵ĸ���Դ");
	}

	/**
	 * ������Դ����������ͬ��ֵ����Դ�Ѿ����ڣ�������滻��
	 * 
	 * @param resourceItem
	 *            ��Դ��
	 */
	final void put(ExceptionCatcher catcher,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem) {
		this.checkIndexes();
		int i = 0, len = this.indexes.length;
		try {
			for (; i < len; i++) {
				this.indexes[i].put(resourceItem); // ����ͬ��
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
					rdd.remove(); // ���ڴ�����������ϲ�������쳣��
				} catch (Throwable ignore) {
				}
				resourceItem.removeEntry(rdd);
			}

			// ��ԭ����Entry��״̬
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
							// keysEqual�������ܻ���Ϊ�û���ʵ�ֶ������쳣���Ӷ�������ȷ����״̬��
							// �����ϣ��������֣������ϴ��޸�ʱ�ͻ���֣���������������֡�
							// ��Ϊ�˷�ֹ��ȷ�������ص��³����⣬��������Ѱ��������ʽ��������������״̬���⡣
						}
					}
					re = re.nextSibling;
				}
			}

			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * �򻺴���������Դ����
	 * 
	 * ����������Ѿ�������ͬ��ֵ����������Ч����Դ���������ָ���Ĳ��Դ���
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param resource
	 *            ��Դ����
	 * @param keys
	 *            ��Դ�����Ӧ�ļ���
	 * @param policy
	 *            ����ͬ��ֵ����Դ�����Ѿ������ڻ���ʱ��Ӧ��ȡ�Ĵ������
	 * @return ��Դ���������ڻ���������Ӧ����Դ��
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
			throw new IllegalArgumentException("��Դʵ����������");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("��Դ�������δʵ�ּ���ӿ�");
		}

		/**
		 * �����valid������
		 * 
		 * �Ѿ��ڻ����У�����Ч�ģ�
		 * 
		 * �����̸߳���ӻ�ɾ������δ�ύ����ģ� �����߳������޸ĵģ�
		 * 
		 * ���̸߳���ӵģ� ���̸߳�ɾ���ģ� ���߳������޸ĵģ�
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

				if (indexItem == null) {// ���ȫ�µ���Դ
					ResourceItem<TFacade, TImpl, TKeysHolder> item;
					if (withID) {
						item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
								this, id, resource, keys);
					} else {
						item = new ResourceItem<TFacade, TImpl, TKeysHolder>(
								this, resource, keys);
					}
					// ��Ϊ����new�����Ķ������Կ�����ͬ������ִ��������������������������
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

			// ����PARTΪ��ʱ����ʾ��Դ�ļ��뻺�������е���Դ�в��ּ���ͬ��������һ���ּ��ǲ�ͬ�ġ�
			boolean PART = false;
			if (i != 0 || !indexItem.isKeysEqual(keys)) { // ���ּ�ֵ��ͻ
				PART = true;
			}

			if (indexItem.state == ResourceItem.State.RESOLVED
					&& policy != WhenExists.REPLACE) {
				if (policy == WhenExists.EXCEPTION) {
					throw new IllegalArgumentException("������ͬ������Դ�Ѿ�����");
				} else if (policy == WhenExists.IGNORE) {
					return indexItem;
				}
			}

			/**
			 * ��������ɹ���valid�Ŀ���״̬�У�
			 * 
			 * �Ѿ��ڻ����У�����Ч�ģ�
			 * 
			 * �����߳�ɾ�������ύ������ģ�
			 * 
			 * ���̸߳���ӵģ� ���̸߳�ɾ���ģ� ���߳������޸ĵġ�
			 */
			handle = transaction.tryHandleItemIntoContextIfNot(indexItem,
					ResourceDemandFor.MODIFY);

			if (indexItem.state == State.DISPOSED) {
				handle.removeSelfFromHolderAndRelease();
				continue;
			} else if (indexItem.state == State.EMPTY
					|| indexItem.state == State.PROVIDED) {
				throw new RuntimeException("�ݹ���ѭ��");
			}

			State itemState = indexItem.state;

			// ������ض��������²����Ƿ��ͻ
			if (policy != WhenExists.REPLACE
					&& (itemState == State.RESOLVED
							|| itemState == State.FILLED || itemState == State.MODIFIED)) {
				if (policy == WhenExists.EXCEPTION) {
					if (handle != null && itemState == State.RESOLVED) {
						handle.removeSelfFromHolderAndRelease();
					}
					throw new IllegalArgumentException("������ͬ������Դ�Ѿ�����");
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
				// ���̸߳���ӵ�
				{
					indexItem.impl = resource;
					indexItem.keys = keys;
				} else if (itemState == State.RESOLVED
						|| itemState == State.MODIFIED
						|| itemState == State.REMOVED)
				// �Ѿ��ڻ����У�����Ч��
				// ���� ���߳������޸ĵ�
				// ���� ���̸߳�ɾ������δ�ύ��
				{
					indexItem.ensureTempValues();
					indexItem.tempValues.newImpl = resource;
					indexItem.tempValues.newKeys = keys;
					indexItem.state = State.MODIFIED;
				} else {
					handle.removeSelfFromHolderAndRelease();
					Assertion.ASSERT(false, "��Ӧ���ֵ�״̬: " + indexItem.state);
				}
			} finally {
				this.modifyLock.unlock();
			}

			return indexItem;
		}
	}

	// ֻ����Դ��ʼ�����̿���ʹ��Щ������
	final ResourceItem<TFacade, TImpl, TKeysHolder> putAndCommit(
			ContextImpl<TFacade, TImpl, TKeysHolder> context, TImpl resource,
			TKeysHolder keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		if (!this.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դʵ����������");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("��Դ�������δʵ�ּ���ӿ�");
		}

		/**
		 * �����validֻ���ܰ������̸߳����������Ч�Ķ���
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
						"��Ӧ���ֵ�״̬: " + indexItem.state);
				// policy �ھ����������¶���REPLACE��������������жϿ�ʹ����ļ����뱻��·��
				if (policy != WhenExists.REPLACE) {
					if (policy == WhenExists.EXCEPTION) {
						throw new IllegalArgumentException("������ͬ������Դ�Ѿ�����");
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
	 * ʹ������ָ������ԴʧЧ��������װ�ظ���Դ��
	 * 
	 * ���ָ������Դδ������װ�أ���ɾ������Դ�� �������װ�سɹ����������װ�س�������Դ����Ϊ����ӵ�״̬��
	 * 
	 * @param <TKey1>
	 * @param <TKey2>
	 * @param <TKey3>
	 * @param context
	 *            �����Ķ���
	 * @param handle
	 *            ��Դ����ľ��
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
		Assertion.ASSERT(indexEntry != null, "����������Ϊ�գ�null��");
		ResourceProviderBase provider = null;
		for (int i = 0, len = this.indexes.length; i < len; i++) {
			if (this.indexes[i].containsEntry(indexEntry)) {
				provider = this.indexes[i].provider;
				break;
			}
		}
		Assertion.ASSERT(provider != null, "��Դ�ṩ������Ϊ�գ�null��");

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
	 * �ͷ���Դ
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
	 * �õ���Դ����
	 * 
	 * @param index
	 *            ������λ��
	 */
	ResourceIndex<TFacade, TImpl, TKeysHolder, ?, ?, ?> getResourceIndex(
			int index) {
		return this.indexes[index];
	}

	/**
	 * �õ���Դ����
	 * 
	 * @param provider
	 *            ������λ��
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
	 * �Ƴ�ĳ��Դ
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
				 * REMIND? �������ڵ�ı�����⣺ ���ﲢû�ж����ڵ����ɾ����ǡ�
				 * ����ں���Ĳ������ְѱ���Դ������ӽ�������ô�ͻ�������������֣�<br/> 1.
				 * ����������ʱ��ָ�����µĸ��ڵ㣬���ֱ���Ƶ����µĸ��ڵ㣨��Ȼ�������ύʱ��Ч����<br/> 2.
				 * ����������ʱ��û��ָ���µĸ��ڵ㣬
				 * ������������ԭ����λ�ã����ܲ���ϣ������������ȷʵ�޸��˹��ڸ��ڵ����ݵ�����������ָ���µĸ��ڵ㣩��
				 */
			}
		}
		return resource;
	}

	/**
	 * ������Դ
	 */
	final ResourceItem<?, ?, ?> ownerResource;
	/**
	 * ��Դ�����
	 */
	final Object category;
	/**
	 * ��Դ������
	 */
	final String categoryTitle;
	/**
	 * ��Դ������
	 */
	final ResourceServiceBase<TFacade, TImpl, TKeysHolder> resourceService;
	final boolean isGlobalResource;
	final boolean inCluster;

	@SuppressWarnings("unchecked")
	final static ResourceIndex[] emptyResourceIndexes = {};

	/**
	 * ��ID,ֻ��ȫ����Դ�����
	 */
	final GUID groupID;

	final long id;

	/**
	 * ���캯��
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
			throw new IllegalDeclarationException("�ڣ�"
					+ this.resourceService.facadeClass + "�����͵���Դ������û�ж����κ���Դ�ṩ��");
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
	 * ��ʼ���飬װ����Դ������У�
	 */
	@SuppressWarnings("unchecked")
	final void ensureInit(ContextImpl<?, ?, ?> context) {
		// TODO ��֤��ResourceGroup����
		// �����ɹ�֮���ȼ��resourceGroup�Ƿ��Ѿ���ʼ�������û�У�����ó�ʼ�����̡�
		// TODO ͬ������
		// ������ӵ���Դ���Դ����㡢�������

		// TODO ����

		if (this.state < state_init_end) {
			this.modifyLock.lock();
			try {
				switch (this.state) {
				case state_none:
					// TODO ȥ������������ס����
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
						// TODO ȥ����������ͬ������,ȥ���������Ͻ�������
					}
					break;
				case state_inint_begin:
					throw new IllegalStateException(
							"��������Դ���ѭ�����������޸ĳ���\r\n��Դ�����:"
									+ this.resourceService.facadeClass
											.getName());
				case state_init_failed:
					throw new IllegalStateException("��Դ��ʼ�쳣��\r\n��Դ�����:"
							+ this.resourceService.facadeClass.getName());
				}
			} finally {
				this.modifyLock.unlock();
			}
		}
	}

	/**
	 * ��һ����Դ�飬������Դ���HASH��
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
			Assertion.ASSERT(parent.newPlace == null, "������ռλ������ز�Ϊ�գ�null��");
		}
		this.modifyLock.lock();
		try {
			// ������Ľڵ�
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
				// ����Ƿ��Ѵ��ڸ��ӹ�ϵ
				if (parent == childEntry.parent
						|| parent.resourceItem == childEntry.parent.resourceItem) {
					return;
				}

				// ���ѭ������
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
				 * ������ڵ���һ���ո��ƶ�����ռλ����ֱ���ƶ����ռλ�����ɡ�
				 */
				if (childEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
					this.takeOutEntry(childEntry);

					/*
					 * ռλ��Ҫ�ƻ�ԭ����λ�á� ��ռλ���µ��ӽڵ�ϲ���ԭ�ڵ��У�ɾ��ռλ�����ɡ�
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
							"������ռλ������ز�Ϊ�գ�null��");
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
	 * ��Դ��ʼ��ʱ���յ��ô˷����������νṹ��
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
			// ������Ľڵ�
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
				// ����Ƿ��Ѵ��ڸ��ӹ�ϵ
				if (parent == childEntry.parent) {
					return;
				}

				// ���ѭ������
				if (parent != this.root) {
					ResourceTreeEntry<TFacade, TImpl, TKeysHolder> p = parent;
					while (p != this.root && p != null) {
						Assertion.ASSERT(p.newPlace == null,
								"������ռλ������ز�Ϊ�գ�null��");
						if (p == childEntry) {
							throw new CycleReferenceException();
						}
						p = p.parent;
					}
				}

				Assertion.ASSERT(childEntry.newPlace == null,
						"������ռλ������ز�Ϊ�գ�null��");
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
	 * ��ָ����toTakeOut������ժ�������ǲ��޸�toTakeOut���κ����ԡ�
	 * 
	 * ժ��toTakeOutʱ�����µ�ȫ����EntryҲ��������ժ������������൱�ڴ���������ժ����һ����toTakeOutΪ����������
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
	 * ɾ��ָ����entry���ύ��
	 * 
	 * XXX ���÷���Ҫ��֤�Ѿ��Ի���ķ�ʽ�����˱�ResourceGroup��
	 */
	@SuppressWarnings("unchecked")
	public final void removeEntryCommitly(ResourceEntry<?, ?, ?> entry) {
		Assertion.ASSERT(entry.holder == this && this.root != null,
				"��Դ������������������Դ��״̬����");
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
	 * ֻ����Դ��ʼ�����̿��Ե��ô˷�����
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
	 * ֻ����Դ��ʼ�����̿��Ե��ô˷�����
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
	 * @return root������������еľ��Լ��Ρ�
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

	// �������
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
				// ��λ��Ч�Ľڵ�
				while ((currEntry.state != ResourceTreeEntry.State.RESOLVED)
						&& (currEntry.next != null)) {
					currEntry = currEntry.next;
				}

				if (currEntry.state == ResourceTreeEntry.State.RESOLVED) {
					// ȡ��Դ
					resource = currEntry.resourceItem.getResource(transaction);
					if (resource != null) {
						temp = node.append(resource);

						// ��λ��һ���ӽڵ�
						if (currEntry.child != null) {
							currEntry = currEntry.child; // push entry
							node = temp; // push node
							continue;
						}
					}

					// ��λ��һ���ֵܽڵ�
					if (currEntry.next != null) {
						currEntry = currEntry.next;
						continue;
					}
				}

				// ���϶�λ�������ֵܽڵ�
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

	// �������
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
				// ��λ��Ч�Ľڵ�
				while (currEntry.newPlace != null && currEntry.next != null) {
					currEntry = currEntry.next;
				}

				if (currEntry.newPlace == null) {
					// ȡ��Դ
					resource = currEntry.resourceItem.getResource(transaction);
					if (resource != null) {
						temp = node.append(resource);

						// ��λ��һ���ӽڵ�
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

					// ��λ��һ���ֵܽڵ�
					if (currEntry.next != null) {
						currEntry = currEntry.next;
						continue;
					}
				}

				// ���϶�λ�������ֵܽڵ�
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
	// // �������
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
	// // ��λ��Ч�Ľڵ�
	// while ((currEntry.state != ResourceTreeEntry.State.RESOLVED)
	// && (currEntry.next != null)) {
	// currEntry = currEntry.next;
	// }
	// if (currEntry.state == ResourceTreeEntry.State.RESOLVED) {
	// // ȡ��Դ
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
	// // ��λ��һ���ӽڵ�
	// if (currEntry.child != null && acp == Acception.ALL) {
	// currEntry = currEntry.child; // push entry
	// absoluteLevel++;
	// relativeLevel++;
	// node = temp; // push node
	// continue;
	// }
	// }
	//
	// // ��λ��һ���ֵܽڵ�
	// if (currEntry.next != null) {
	// currEntry = currEntry.next;
	// continue;
	// }
	// }
	//
	// // ���϶�λ�������ֵܽڵ�
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
	// // �������
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
	// // ��λ��Ч�Ľڵ�
	// while (currEntry.newPlace != null && currEntry.next != null) {
	// currEntry = currEntry.next;
	// }
	//
	// if (currEntry.newPlace == null) {
	// resource = currEntry.resourceItem.getResource(context);
	// // ȡ��Դ
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
	// // ��λ��һ���ӽڵ�
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
	// // ��λ��һ���ֵܽڵ�
	// if (currEntry.next != null) {
	// currEntry = currEntry.next;
	// continue;
	// }
	// }
	//
	// // ���϶�λ�������ֵܽڵ�
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
					// �������
					while (entry != null && entry != this.root) {
						// �㲥��ǰ�ڵ�
						localCluster.broadcast(new ClusterResInfo_TreeEntry(
								this.category, facadeClass, None.NONE,
								entry.parent.getResourceItemId(), entry
										.getResourceItemId(), Action.INIT));

						// ��λ��һ���ڵ�
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

	// ------------------------------����ΪȨ�����-------------------------------------------

	final void lockFillList(Operation<? super TFacade> operation,
			DnaArrayList<TFacade> to, TransactionImpl transaction) {
		this.indexes[0].lockFillResources(operation, to, transaction);
	}

	/**
	 * @return root������������еľ��Լ��Ρ�
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
					// ����
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
					// ����
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
	 * �Ƿ�Ȩ�޿���
	 */
	final boolean isAuthorizable() {
		return this.authResourceIndex != null;
	}

	/**
	 * ����authID���Ҷ�Ӧ����Դ��
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

	// ------------------------------����ΪȨ�����-------------------------------------------

	// ------------------------------���¼�Ⱥ���----------------------------------
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
	 * ��ʼ���ɹ�����true�����򷵻�false
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
						System.out.println("�������ڵ��ʼ����Դ["
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

	// ֻ����Դ��ʼ�����̿���ʹ��Щ������
	final ResourceItem<TFacade, TImpl, TKeysHolder> putAndCommit(
			ContextImpl<TFacade, TImpl, TKeysHolder> context, long itemID,
			TImpl resource, TKeysHolder keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		if (!this.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դʵ����������");
		}
		if (!this.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
		}
		if (!this.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("��Դ�������δʵ�ּ���ӿ�");
		}

		/**
		 * �����validֻ���ܰ������̸߳����������Ч�Ķ���
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
						"��Ӧ���ֵ�״̬: " + indexItem.state);
				// policy �ھ����������¶���REPLACE��������������жϿ�ʹ����ļ����뱻��·��
				if (policy != WhenExists.REPLACE) {
					if (policy == WhenExists.EXCEPTION) {
						throw new IllegalArgumentException("������ͬ������Դ�Ѿ�����");
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

	// ------------------------------���ϼ�Ⱥ���----------------------------------

}
