package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.DisposedException;
import org.eclipse.jt.core.exception.NoAccessAuthorityException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.UnsupportedAuthorityResourceException;
import org.eclipse.jt.core.impl.NClusterResourceInitTask.ReferenceStorage;
import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceReference;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.misc.SortUtil;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceTokenLink;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;


/**
 * ��Դ��ʼ���ӿ�ʵ��
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ����۽ӿڣ��ӿڻ���󣩸����͵����з���Ӧ��ֻ��������Դ��ֻ�����ʷ���
 * @param <TImpl>
 *            ��Դ��ʵ�����ͣ��ӿڻ���󣩸�����Ӧ�ð�������Դ�������޸ķ��ʵķ���
 * @param <TKeysHolder>��Դ�ļ�������
 */
final class ResourceItem<TFacade, TImpl extends TFacade, TKeysHolder> extends
		Acquirable implements ResourceToken<TFacade> {

	final <TOwnerFacade> ResourceItem<TOwnerFacade, ?, ?> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass) {
		return this.group.getOwnerResource(ownerFacadeClass);
	}

	@SuppressWarnings("unchecked")
	final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(
			TransactionImpl transaction, ResourceToken<TFacade> treeParent,
			TImpl resource, TKeysHolder keys) {
		ResourceItem<TFacade, TImpl, TKeysHolder> token = this.putResource(
				resource, keys, WhenExists.REPLACE);
		ResourceItem<TFacade, TImpl, TKeysHolder> treeParentItem = (ResourceItem<TFacade, TImpl, TKeysHolder>) treeParent;
		this.group.putResource(transaction, treeParentItem, token);
		return token;
	}

	/**
	 * ������Դ���ϵ������Entry��
	 */
	final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> findTreeEntry(
			ResourceGroupHandle groupHandle) {
		if (groupHandle != null && groupHandle.res != this.group) {
			throw new IllegalArgumentException("�����ResourceGroup���");
		}
		return this.findTreeEntry(groupHandle != null);
	}

	private synchronized ResourceTreeEntry<TFacade, TImpl, TKeysHolder> findTreeEntry(
			boolean hasGroupHandle) {
		ResourceEntry<TFacade, TImpl, TKeysHolder> entry = this.ownerEntries;
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry = null;
		while (entry != null && (treeEntry = entry.asTreeEntry()) == null) {
			entry = entry.nextSibling;
		}
		if (entry != null) {
			if (treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
				if (!hasGroupHandle) {
					treeEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) treeEntry)
							.original();
				}
			} else {
				if (hasGroupHandle && treeEntry.newPlace != null) {
					treeEntry = treeEntry.newPlace;
				}
			}
			Assertion.ASSERT(treeEntry.resourceItem == this);
		}
		return treeEntry;
	}

	final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> getTreeEntry(
			ResourceGroupHandle groupHandle) {
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> entry = this
				.findTreeEntry(groupHandle);
		if (entry == null) {
			throw new IllegalStateException("����Դ�������");
		}
		return entry;
	}

	final ResourceItem<TFacade, TImpl, TKeysHolder> putResource(TImpl resource,
			TKeysHolder keys, WhenExists policy) {
		if (resource == null || keys == null) {
			throw new NullPointerException();
		}
		if (!this.group.resourceService.implClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դʵ�����Ͳ���");
		}
		if (!this.group.resourceService.facadeClass.isInstance(resource)) {
			throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
		}
		if (!this.group.resourceService.keysClass.isInstance(keys)) {
			throw new IllegalArgumentException("��Դδ��ʵ�ּ���ṹ");
		}
		this.checkKeysEqual(keys);
		if (this.state == State.INVALID) {
			this.ensureTempValues();
			this.tempValues.newKeys = keys;
			this.tempValues.newImpl = resource;
			this.state = State.PROVIDED;
		} else {
			this.keys = keys;
			this.impl = resource;
			if (this.state == State.EMPTY) {
				this.state = State.PROVIDED;
			}
		}
		return this;
	}

	final void checkKeysEqual(TKeysHolder keys) {
		if (!this.isKeysEqual(keys)) {
			throw new IllegalArgumentException("��Դ�ļ�ֵ������ֵ����");
		}
	}

	@SuppressWarnings("unchecked")
	final synchronized boolean isKeysEqual(TKeysHolder keys) {
		ResourceEntry entry = this.ownerEntries;
		ResourceIndexEntry indexEntry;
		ResourceProviderBase provider;
		while (entry != null) {
			indexEntry = entry.asIndexEntry();
			if (indexEntry != null) {
				provider = ((ResourceIndex) indexEntry.holder).provider;
				if (!indexEntry.keysEqual(provider.getKey1(keys), provider
						.getKey2(keys), provider.getKey3(keys))) {
					return false;
				}
			}
			entry = entry.nextSibling;
		}
		return true;
	}

	/**
	 * ��Դ�ṩ��
	 */
	final ResourceGroup<TFacade, TImpl, TKeysHolder> group;

	final boolean isGlobalResource() {
		return this.group.isGlobalResource;
	}

	final boolean inCluster() {
		return this.group.inCluster;
	}

	public final TFacade getFacade() {
		if (this.state == State.DISPOSED) {
			throw new DisposedException();
		}
		Acquirer<?, ?> acquirer;
		TImpl resource;
		do {
			acquirer = this.acquirer;
			if (acquirer == null) {
				resource = this.impl;
			} else if (acquirer.similarTransaction(null)) {
				// FIXME ��Ⱥ��ownerThread�п���Ϊ�գ���Ҫ������
				if (this.state == State.REMOVED) {
					throw new DisposedException();
				} else if (this.state == State.MODIFIED) {
					Assertion.ASSERT(this.tempValues != null);
					resource = this.tempValues.newImpl;
				} else {
					resource = this.impl;
				}
			} else {
				if (this.state == State.FILLED) {
					// acquirer �ڼ�С��С��С�Ŀ���֮�»ᱻ������滻����
					throw new MissingObjectException();
				} else {
					resource = this.impl;
				}
			}
		} while (acquirer != this.acquirer);
		return resource;
	}

	@SuppressWarnings("unchecked")
	final TImpl getImpl() {
		return ((TImpl) this.getFacade());
	}

	public final TFacade tryGetFacade() {
		if (this.state == State.DISPOSED) {
			return null;
		}
		Acquirer<?, ?> acquirer;
		TImpl resource;
		do {
			acquirer = this.acquirer;
			if (acquirer == null) {
				resource = this.impl;
			} else if (acquirer.similarTransaction(null)) {
				// FIXME ��Ⱥ��ownerThread�п���Ϊ�գ���Ҫ������
				if (this.state == State.REMOVED) {
					return null;
				} else if (this.state == State.MODIFIED) {
					Assertion.ASSERT(this.tempValues != null);
					resource = this.tempValues.newImpl;
				} else {
					resource = this.impl;
				}
			} else {
				if (this.state == State.FILLED) {
					// acquirer �ڼ�С��С��С�Ŀ���֮�»ᱻ������滻����
					throw new MissingObjectException();
				} else {
					resource = this.impl;
				}
			}
		} while (acquirer != this.acquirer);
		return resource;
	}

	final TImpl getResource(TransactionImpl transaction) {
		if (this.state == State.DISPOSED) {
			return null; // ���������Ӧ�ó���
		}
		Acquirer<?, ?> acquirer;
		TImpl resource;
		do {
			acquirer = this.acquirer;
			if (acquirer == null) {
				resource = this.impl;
			} else if (transaction.belongs(acquirer)) {
				// FIXME ��Ⱥ��ownerThread�п���Ϊ�գ���Ҫ������
				if (this.state == State.REMOVED) {
					resource = null;
				} else if (this.state == State.MODIFIED) {
					Assertion.ASSERT(this.tempValues != null);
					resource = this.tempValues.newImpl;
				} else {
					resource = this.impl;
				}
			} else {
				if (this.state == State.FILLED) {
					// acquirer �ڼ�С��С��С�Ŀ���֮�»ᱻ������滻����
					resource = null;
				} else {
					resource = this.impl;
				}
			}
		} while (acquirer != this.acquirer);
		return resource;
	}

	/**
	 * ��Դʵ��
	 */
	volatile TImpl impl;
	/**
	 * ��Դ������
	 */
	TKeysHolder keys;

	volatile TempValues<TImpl, TKeysHolder> tempValues;

	final void ensureTempValues() {
		if (this.tempValues == null) {
			this.tempValues = new TempValues<TImpl, TKeysHolder>(this.keys,
					this.impl);
		}
	}

	static final class TempValues<TImpl, TKeysHolder> {
		// �����ύʱ�������ֵ�������е�ֵ
		TKeysHolder newKeys;
		// �����ύʱ�������ֵ�������е�ֵ
		TImpl newImpl;

		// Ϊ�޸���Դ����ĸ���
		TImpl copyForModification;

		private TempValues(TKeysHolder tempKeys, TImpl tempImpl) {
			this.newKeys = tempKeys;
			this.newImpl = tempImpl;
		}
	}

	volatile State state;

	static enum State {
		/**
		 * ֻ������˿յ�ResourceItem����
		 */
		EMPTY,

		/**
		 * �Ѿ���ResourceItem�����г�ʼ������Դ����
		 */
		FILLED,

		/**
		 * ��Դ��ʧЧ
		 */
		INVALID,

		/**
		 * �ո���ResourceProvider�����ֵ
		 */
		PROVIDED,

		/**
		 * ResourceItem���ھ���״̬����������ʹ��
		 */
		RESOLVED,

		/**
		 * ResourceItem�е���Դ�Ѿ����޸ģ�����δ�ύ
		 */
		MODIFIED,

		/**
		 * ResourceItem�е���Դ�����Ϊɾ��
		 */
		REMOVED,

		/**
		 * ResourceItem�Ѿ���������
		 */
		DISPOSED;
	}

	/**
	 * ȫ����Դ��ID����ȫ����ԴID== 0;
	 */
	final long id;

	/**
	 * ���캯��
	 */
	ResourceItem(ResourceGroup<TFacade, TImpl, TKeysHolder> group) {
		this.group = group;
		this.state = State.EMPTY;
		this.id = group.resourceService.site.globalResourceContainer
				.resourceItemCreated(this);
	}

	ResourceItem(ResourceGroup<TFacade, TImpl, TKeysHolder> group,
			TImpl resource, TKeysHolder keys) {
		this(group);
		try {
			if (resource == null || keys == null) {
				throw new NullPointerException();
			}
			if (!group.resourceService.implClass.isInstance(resource)) {
				throw new IllegalArgumentException("��Դʵ�����Ͳ���");
			}
			if (!group.resourceService.facadeClass.isInstance(resource)) {
				throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
			}
			if (!group.resourceService.keysClass.isInstance(keys)) {
				throw new IllegalArgumentException("��Դδ��ʵ�ּ���ṹ");
			}
		} catch (Throwable e) {
			group.resourceService.site.globalResourceContainer
					.resourceItemDisposed(this);
			throw Utils.tryThrowException(e);
		}
		this.impl = resource;
		this.keys = keys;
		this.state = State.FILLED;
	}

	/**
	 * �ͷţ�ʹ��Ч���÷�������ǰһ��Ҫȷ����Դ�Ѿ�����Դ�����Ƴ�
	 */
	final void dispose(ExceptionCatcher catcher) {
		if (this.state != State.DISPOSED) {
			try {
				ResourceGroup<?, ?, ?>[] subResources = this.subResourceGroups;
				if (subResources != null) {
					for (int i = 0; i < subResources.length; i++) {
						subResources[i].reset(catcher, true);
					}
				}
				this.group.resourceService.disposeResource(this.impl,
						this.keys, catcher);
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
			this.state = State.DISPOSED;
			this.group.resourceService.site.globalResourceContainer
					.resourceItemDisposed(this);
			this.impl = null;
			this.keys = null;
			this.tempValues = null;
			this.ownerEntries = null;
			this.references = null;
		}
	}

	volatile ResourceEntry<TFacade, TImpl, TKeysHolder> ownerEntries;

	synchronized final void appendEntry(
			ResourceEntry<TFacade, TImpl, TKeysHolder> entry) {
		if (entry.resourceItem != this) {
			throw new IllegalArgumentException("���Ϸ���Entry");
		}
		entry.nextSibling = this.ownerEntries;
		this.ownerEntries = entry;
	}

	/**
	 * �ӱ���Դ����ɾ��һ��ָ�����������entry����entryָ�����Դ������Ǳ���Դ�
	 * 
	 * @param entry
	 */
	synchronized final ResourceEntry<TFacade, TImpl, TKeysHolder> removeEntry(
			ResourceEntry<TFacade, TImpl, TKeysHolder> entry) {
		if (entry == null || entry.resourceItem != this) {
			return null;
		}
		ResourceEntry<TFacade, TImpl, TKeysHolder> e = this.ownerEntries, last = null;
		while (e != null) { // ��������ܻ�ܳ����ɿ���˫������
			if (e == entry) {
				if (last == null) {
					this.ownerEntries = e.nextSibling;
				} else {
					last.nextSibling = e.nextSibling;
				}
				return e;
			}
			last = e;
			e = e.nextSibling;
		}
		return null;
	}

	synchronized final boolean holdedByEntry(
			ResourceEntry<TFacade, TImpl, TKeysHolder> entry) {
		if (entry == null) {
			throw new NullArgumentException("entry");
		}
		if (entry.resourceItem != this) {
			return false;
		}
		ResourceEntry<TFacade, TImpl, TKeysHolder> e = this.ownerEntries;
		while (e != null && e != entry) {
			e = e.nextSibling;
		}
		return (e == entry);
	}

	/**
	 * ɾ������Դ�ͬʱ����ص�����EntryҲɾ��
	 */
	synchronized final void remove(ExceptionCatcher catcher) {
		ResourceEntry<?, ?, ?> entry = this.ownerEntries;
		while (entry != null) {
			entry.remove();
			entry = entry.nextSibling;
		}
		this.dispose(catcher);
	}

	private ResourceGroup<?, ?, ?>[] subResourceGroups;

	/**
	 * ��ȡ��Ԫ�ص���
	 * 
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final ResourceGroup getSubResourceGroup(int index,
			ContextImpl<?, ?, ?> context) {
		if (this.subResourceGroups == null) {
			// XXX ��ʹ��this����
			synchronized (this) {
				if (this.subResourceGroups == null) {
					this.subResourceGroups = this.group.resourceService
							.tryNewSubResourceGroupsAndInit(this, context);
				}
			}
		}
		return this.subResourceGroups[index];
	}

	@SuppressWarnings("unchecked")
	final <TSubFacade> ResourceGroup<TSubFacade, ?, ?> getSubResourceGroup(
			Class<TSubFacade> subFacadeClass, ContextImpl<?, ?, ?> context) {
		if (this.subResourceGroups == null) {
			// XXX ��ʹ��this����
			synchronized (this) {
				if (this.subResourceGroups == null) {
					this.subResourceGroups = this.group.resourceService
							.tryNewSubResourceGroupsAndInit(this, context);
				}
			}
		}
		ResourceGroup group;
		for (int i = 0, len = this.subResourceGroups.length; i < len; i++) {
			group = this.subResourceGroups[i];
			if (group.resourceService.facadeClass == subFacadeClass) {
				return group;
			}
		}
		throw new UnsupportedOperationException("û���ҵ���Դ����Ϊ��" + subFacadeClass
				+ "�ݵ�����Դ����");
	}

	private volatile ResourceReferenceStorage<?> references; // FIXME Lock

	// TODO ������
	@SuppressWarnings("unchecked")
	static final <TFacade, TImpl extends TFacade, TKeysHolder> void moveReferences(
			ResourceItem<TFacade, TImpl, TKeysHolder> from,
			ResourceItem<TFacade, TImpl, TKeysHolder> to) {
		if (from != null && to != null && from.references != null) {
			ResourceReferenceStorage fromRefs = from.references;
			from.references = null;
			if (to.references == null) {
				to.references = fromRefs;
			} else {
				ResourceReferenceStorage toRefs, nextFrom;
				final ResourceReferenceStorage toRefsStart = to.references;
				boolean needAppend;
				while (fromRefs != null) {
					toRefs = toRefsStart;
					needAppend = true;
					while (toRefs != null) {
						if (toRefs.reference == fromRefs.reference) {
							ResourceReferenceStorage.moveReferences(fromRefs,
									toRefs);
							needAppend = false;
							break;
						}
						toRefs = toRefs.next;
					}
					nextFrom = fromRefs.next;
					fromRefs.next = null;
					if (needAppend) {
						fromRefs.next = to.references;
						to.references = fromRefs;
					}
					fromRefs = nextFrom;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	<TRefFacade> ResourceReferenceStorage<TRefFacade> getRRS(
			Class<TRefFacade> refFacadeClas) {
		ResourceReferenceStorage ref = this.references;
		while (ref != null && ref.reference.refFacadeClass != refFacadeClas) {
			ref = ref.next;
		}
		if (ref == null) {
			ref = this.group.resourceService.newResourceReferenceStorage(this,
					refFacadeClas);
			ref.next = this.references;
			this.references = ref;
		}
		return ref;
	}

	@SuppressWarnings("unchecked")
	private void putReference(ResourceReferenceStorage ref,
			ResourceReferenceEntry newEntry) {
		synchronized (ref) {
			if (ref.first != null) {
				ref.first.prev.next = newEntry;
				newEntry.prev = ref.first.prev;
				ref.first.prev = newEntry;
			} else {
				newEntry.prev = newEntry;
				ref.first = newEntry;
			}
		}
	}

	@SuppressWarnings("unchecked")
	final <TRefFacade> void putReference(TransactionImpl transaction,
			ResourceItem<TRefFacade, ?, ?> reference) {
		final ResourceReferenceStorage ref = this
				.getRRS(reference.group.resourceService.facadeClass);
		transaction.tryHandleResRefStorageIntoContextIfNot(ref);
		if (ref.first != null) {
			synchronized (reference) {
				ResourceEntry e = reference.ownerEntries;
				while (e != null) {
					if (e.holder == ref) {
						if (((ResourceReferenceEntry) e).state == ResourceReferenceEntry.State.REMOVED) {
							((ResourceReferenceEntry) e).state = ResourceReferenceEntry.State.NEW;
						}
						return;
					}
					e = e.nextSibling;
				}
			}
		}
		ResourceReferenceEntry newEntry = new ResourceReferenceEntry(ref,
				reference);
		reference.appendEntry(newEntry);
		this.putReference(ref, newEntry);
		if (ref.reference.isAuthorityReference()) {
			if (this.authReference != null) {
				throw new IllegalStateException("����Ϊһ������Ȩ��Դ��ͬʱ������������Ȩ��Դ��");
			}
			this.authReference = ref;
		}
	}

	/**
	 * ������ֻ������Դ��ʼ������ʹ�á�
	 */
	@SuppressWarnings("unchecked")
	final <TRefFacade> void putReferenceAndCommit(
			ResourceItem<TRefFacade, ?, ?> reference) {
		ResourceReferenceStorage ref = this
				.getRRS(reference.group.resourceService.facadeClass);
		if (ref.first != null) {
			synchronized (reference) {
				ResourceEntry e = reference.ownerEntries;
				while (e != null) {
					if (e.holder == ref) {
						Assertion
								.ASSERT(
										((ResourceReferenceEntry) e).state == ResourceReferenceEntry.State.RESOLVED,
										"��Ӧ���ֵ�״̬: "
												+ ((ResourceReferenceEntry) e).state);
						return;
					}
					e = e.nextSibling;
				}
			}
		}
		ResourceReferenceEntry newEntry = new ResourceReferenceEntry(ref,
				reference);
		newEntry.state = ResourceReferenceEntry.State.RESOLVED;
		reference.appendEntry(newEntry);
		this.putReference(ref, newEntry);
		if (ref.reference.isAuthorityReference()) {
			if (this.authReference != null) {
				throw new IllegalStateException("����Ϊһ������Ȩ��Դ��ͬʱ������������Ȩ��Դ��");
			}
			this.authReference = ref;
		}
	}

	@SuppressWarnings("unchecked")
	final <TRefFacade> void removeReference(TransactionImpl transaction,
			ResourceItem<TRefFacade, ?, ?> reference) {
		final Class<?> facadeClass = reference.group.resourceService.facadeClass;
		ResourceReferenceStorage ref = this.references;
		while (ref != null && ref.reference.refFacadeClass != facadeClass) {
			ref = ref.next;
		}
		if (ref != null) {
			transaction.tryHandleResRefStorageIntoContextIfNot(ref);
			ResourceEntry e = null, last = null;
			synchronized (reference) {
				e = reference.ownerEntries;
				while (e != null) {
					if (e.holder == ref) {
						// ��������ܻ�ܳ����ɿ���˫������
						if (((ResourceReferenceEntry) e).state == ResourceReferenceEntry.State.NEW) {
							if (last == null) {
								e.resourceItem.ownerEntries = e.nextSibling;
							} else {
								last.nextSibling = e.nextSibling;
							}
						}
						break;
					}
					last = e;
					e = e.nextSibling;
				}
			}
			if (e != null) {
				if (((ResourceReferenceEntry) e).state == ResourceReferenceEntry.State.NEW) {
					e.remove();
					if (e == this.authReference.first) {
						this.authReference = null;
					}
				} else {
					((ResourceReferenceEntry) e).state = ResourceReferenceEntry.State.REMOVED;
				}
			}
		} else {
			this.group.resourceService
					.checkDeclaredResourceReference(facadeClass);
		}
	}

	@SuppressWarnings("unchecked")
	final void markReferencesRemoved(TransactionImpl transaction) {
		ResourceReferenceEntry refEntry;

		List<ResourceReferenceEntry> storages = new ArrayList<ResourceReferenceEntry>();
		synchronized (this) {
			ResourceEntry entry = this.ownerEntries;
			while (entry != null) {
				refEntry = entry.asReferenceEntry();
				if (refEntry != null) {
					storages.add(refEntry);
				}
				entry = entry.nextSibling;
			}
		}
		for (int i = 0, len = storages.size(); i < len; i++) {
			refEntry = storages.get(i);
			transaction
					.tryHandleResRefStorageIntoContextIfNot((ResourceReferenceStorage) refEntry.holder);
			refEntry.state = ResourceReferenceEntry.State.REMOVED;
		}
		storages = null;

		ResourceReferenceStorage ref = this.references;
		while (ref != null) {
			transaction.tryHandleResRefStorageIntoContextIfNot(ref);
			refEntry = ref.first;
			while (refEntry != null) {
				refEntry.state = ResourceReferenceEntry.State.REMOVED;
				refEntry = refEntry.next;
			}
			ref = ref.next;
		}
	}

	/**
	 * ������ֻ������Դ��ʼ������ʹ�á�
	 */
	@SuppressWarnings("unchecked")
	final <TRefFacade> void removeReferenceAndCommit(
			ResourceItem<TRefFacade, ?, ?> reference) {
		final Class<?> facadeClass = reference.group.resourceService.facadeClass;
		ResourceReferenceStorage ref = this.references;
		while (ref != null && ref.reference.refFacadeClass != facadeClass) {
			ref = ref.next;
		}
		if (ref != null) {
			ResourceEntry e = null, last = null;
			synchronized (reference) {
				e = reference.ownerEntries;
				while (e != null) {
					if (e.holder == ref) {
						// ��������ܻ�ܳ����ɿ���˫������
						if (last == null) {
							e.resourceItem.ownerEntries = e.nextSibling;
						} else {
							last.nextSibling = e.nextSibling;
						}
						break;
					}
					last = e;
					e = e.nextSibling;
				}
			}
			if (e != null) {
				e.remove();
			}
		} else {
			this.group.resourceService
					.checkDeclaredResourceReference(facadeClass);
		}
	}

	/**
	 * �����Item�ϵ�ָ�����͵���Դ���á�<code>absolutely</code>��������ָ���Ƿ���Դ����Ҳ����ɾ����
	 * 
	 * @param <TRefFacade>
	 *            ��Դ���ö������������
	 * @param context
	 *            �����Ķ���
	 * @param refFacadeClass
	 *            ��Դ���ö��������ʵ��
	 * @param absolutely
	 *            �Ƿ�ͬʱɾ����Ӧ����Դ����
	 */
	/*
	 * FIXME ����ļ�������ֻ����һ����������ݹ�Ĵ���༶��<br/>
	 * ���������ɾ���������л�������������ʱ�����û��ֲ�֪����һ��Ϊ���Ϳ��ܳ����⡣<br/>
	 * 
	 * ��һ���ļ������Կ��ܲ�ͬ�����Խ���ȥ������ɾ����һ���ܡ�
	 */
	@SuppressWarnings("unchecked")
	final <TRefFacade> void clearReferences(TransactionImpl transaction,
			final Class<TRefFacade> refFacadeClass, boolean absolutely) {
		ResourceReferenceStorage ref = this.references;
		while (ref != null && ref.reference.refFacadeClass != refFacadeClass) {
			ref = ref.next;
		}
		if (ref != null) {
			transaction.tryHandleResRefStorageIntoContextIfNot(ref);
			ResourceReferenceEntry e = ref.first;
			while (e != null) {
				if (absolutely) {
					e.resourceItem.group
							.lockRemove(transaction, e.resourceItem);
				} else {
					if (e.state == ResourceReferenceEntry.State.NEW) {
						Assertion.ASSERT(e.resourceItem.removeEntry(e) == e);
						e.remove();
					} else {
						e.state = ResourceReferenceEntry.State.REMOVED;
					}
				}
				e = e.next;
			}
		} else {
			this.group.resourceService
					.checkDeclaredResourceReference(refFacadeClass);
		}
	}

	@SuppressWarnings("unchecked")
	final <TRefFacade> void fillReferences(
			Operation<? super TRefFacade> operation,
			final Class<TRefFacade> refFacadeClass,
			final DnaArrayList<TRefFacade> to, TransactionImpl transaction,
			Filter<? super TRefFacade> filter,
			Comparator<? super TRefFacade> sortComparator) {
		final ContextImpl<?, ?, ?> ctx = transaction.getCurrentContext();
		ctx.makeSureResourceInited(refFacadeClass, this.group.category);
		ResourceReferenceStorage<?> ref = this.references;
		while (ref != null && ref.reference.refFacadeClass != refFacadeClass) {
			ref = ref.next;
		}
		if (ref != null) {
			final boolean handled = transaction.findResRefStorageHandle(ref) != null;
			final ResourceReference<TRefFacade, ?> reference = (ResourceReference) ref.reference;
			final OperationEntry opEntry;
			if (operation == null) {
				opEntry = null;
			} else if (reference.refResourceService.isAuthorizable()) {
				if (ctx.session.getUser() != InternalUser.debugger) {
					opEntry = reference.refResourceService
							.getOperationEntry(operation);
				} else {
					opEntry = null;
				}
			} else {
				throw new UnsupportedAuthorityResourceException(
						reference.refResourceService.facadeClass);
			}
			if (opEntry != null) {
				ref.reference.refResourceService
						.beforeAccessAuthorityResource(ctx);
			}
			try {
				synchronized (ref) {
					ResourceReferenceEntry<TRefFacade, ? extends TRefFacade, ?> entry = (ResourceReferenceEntry) ref.first;
					if (entry != null) {
						TRefFacade resource;
						if (opEntry == null) {
							do {
								resource = entry.resourceItem
										.getResource(transaction);
								if (handled ? entry.state != ResourceReferenceEntry.State.REMOVED
										: entry.state != ResourceReferenceEntry.State.NEW) {
									if (resource != null) {
										to.add(resource);
									}
								}
								entry = entry.next;
							} while (entry != null);
						} else {

							do {
								resource = entry.resourceItem
										.getResource(transaction);
								if (handled ? entry.state != ResourceReferenceEntry.State.REMOVED
										: entry.state != ResourceReferenceEntry.State.NEW) {
									if (resource != null) {
										if (entry.resourceItem
												.validateAuthorityInternal(
														operation, ctx, true)) {
											to.add(resource);
										}
									}
								}
								entry = entry.next;
							} while (entry != null);
						}
					}
				}
			} finally {
				if (opEntry != null) {
					ref.reference.refResourceService
							.endAccessAuthorityResource(ctx);
				}
			}

			int len = to.size();
			if (len > 0) {
				int acceptedCount = 0;
				TRefFacade item;
				if (filter != null && reference.supportDftAccept) {
					for (int i = 0; i < len; i++) {
						item = to.get(i);
						if (filter.accept(item) && reference.accept(item)) {
							if (acceptedCount != i) {
								to.set(acceptedCount, item);
							}
							acceptedCount++;
						}
					}
				} else {
					if (filter == null && reference.supportDftAccept) {
						filter = reference;
					}
					if (filter == null) {
						acceptedCount = len;
					} else {
						for (int i = 0; i < len; i++) {
							item = to.get(i);
							if (filter.accept(item)) {
								if (acceptedCount != i) {
									to.set(acceptedCount, item);
								}
								acceptedCount++;
							}
						}
					}
				}
				if (acceptedCount == 0) {
					to.clear();
					len = 0;
				} else if (acceptedCount < len) {
					to.removeTail(acceptedCount);
					len = acceptedCount;
				}
			}
			if (len > 0) {
				if (sortComparator != null) {
					SortUtil.sort(to, sortComparator);
				} else if (reference.supportDftCompare) {
					SortUtil.sort(to, reference);
				}
			}
		} else {
			this.group.resourceService
					.checkDeclaredResourceReference(refFacadeClass);
		}
	}

	/**
	 * @return rootNode���������ṹ�еľ��Լ��Ρ�
	 */
	final int fillTree(TreeNodeImpl<TFacade> rootTreeNode,
			TransactionImpl transaction) {
		if (rootTreeNode == null) {
			throw new NullArgumentException("rootTreeNode");
		}
		TImpl resource = this.getResource(transaction);
		if (resource == null) {
			return 0;
		}

		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry = this
				.findTreeEntry(transaction.findResGroupHandle(this.group));
		if (treeEntry == null) {
			return 0;
		}

		rootTreeNode.setElement(resource);
		return this.group.fillTreeNode(transaction, rootTreeNode, treeEntry);

		// if (filter == null && comparator == null) {
		// root.setElement(resource);
		// this.group.fillTreeNode(context, root, treeEntry);
		// } else {
		// if (filter != null) {
		// Acception acc = filter
		// .accept(resource, treeEntry.getLevel(), 0);
		// if (acc == null) {
		// return root;
		// } else if (acc == Acception.ALL) {
		// root.setElement(resource);
		// } else if (acc == Acception.NO_CHILDREN) {
		// root.setElement(resource);
		// return root;
		// } else {
		// Assertion.ASSERT(false, "�������޸���Acception��������δ����Ӧ�仯��");
		// }
		// } else {
		// root.setElement(resource);
		// }
		// this.group.fillTreeNode(context, root, treeEntry, filter,
		// comparator);
		// }
	}

	@SuppressWarnings("unchecked")
	final void putResource(TransactionImpl transaction,
			ResourceToken<TFacade> treeParent, ResourceToken<TFacade> child) {
		if (child == null) {
			throw new NullPointerException();
		}
		ResourceItem<TFacade, TImpl, TKeysHolder> treeParentItem = (ResourceItem<TFacade, TImpl, TKeysHolder>) treeParent;
		ResourceItem<TFacade, TImpl, TKeysHolder> childItem = (ResourceItem<TFacade, TImpl, TKeysHolder>) child;
		childItem.group.putResource(transaction, treeParentItem, childItem);
	}

	public final Object getCategory() {
		return this.group.category;
	}

	@SuppressWarnings("unchecked")
	public final Class<TFacade> getFacadeClass() {
		return (Class<TFacade>) this.group.resourceService.facadeClass;
	}

	public final ResourceKind getKind() {
		return this.group.resourceService.kind;
	}

	public final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> getChildren() {
		return this.internalGetChildren(null);
	}

	final ResourceTreeEntry<TFacade, TImpl, TKeysHolder> internalGetChildren(
			TransactionImpl transaction) {
		Acquirer<?, ?> acq = this.acquirer;
		boolean itemHandled = acq != null
				&& acq.similarTransaction(transaction);
		if (itemHandled ? this.state == State.REMOVED
				: this.state == State.FILLED) {
			return null;
		}

		synchronized (this) {
			acq = this.group.acquirer;
			boolean groupHandled = acq != null
					&& acq.similarTransaction(transaction);

			ResourceEntry<TFacade, TImpl, TKeysHolder> entry = this.ownerEntries;
			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry = null;
			while (entry != null && (treeEntry = entry.asTreeEntry()) == null) {
				entry = entry.nextSibling;
			}
			if (entry != null) {
				if (treeEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
					treeEntry = ((ResourceTreeEntry<TFacade, TImpl, TKeysHolder>.RTEPlaceHolder) treeEntry)
							.original();
					if (treeEntry.child == null && groupHandled) {
						treeEntry = treeEntry.newPlace;
					}
				} else {
					if (groupHandled && treeEntry.newPlace != null
							&& treeEntry.child == null) {
						treeEntry = treeEntry.newPlace;
					}
				}
				Assertion.ASSERT(treeEntry.resourceItem == this);
			}

			ResourceTreeEntry<TFacade, TImpl, TKeysHolder> childEntry = treeEntry.child;
			if (childEntry == null) {
				return null;
			}

			do {
				if (groupHandled) {
					while (childEntry != null && childEntry.newPlace != null) {
						childEntry = childEntry.next;
					}
					if (childEntry == null && treeEntry.newPlace != null) {
						childEntry = treeEntry.newPlace.child;
						while (childEntry != null
								&& childEntry.newPlace != null) {
							childEntry = childEntry.next;
						}
					}
				} else {
					while (childEntry instanceof ResourceTreeEntry<?, ?, ?>.RTEPlaceHolder) {
						childEntry = childEntry.next;
					}
				}

				if (childEntry != null) {
					ResourceItem<?, ?, ?> item = childEntry.resourceItem;
					acq = item.acquirer;
					itemHandled = acq != null
							&& acq.similarTransaction(transaction);
					if (itemHandled ? item.state == State.REMOVED
							: item.state == State.FILLED) {
						childEntry = childEntry.next;
					} else {
						break;
					}
				}
			} while (childEntry != null);

			return childEntry;
		}
	}

	public final ResourceItem<TFacade, ?, ?> getParent() {
		Acquirer<?, ?> acq = this.acquirer;
		boolean itemHandled = acq != null && acq.similarTransaction(null);
		if (itemHandled ? this.state == State.REMOVED
				: this.state == State.FILLED) {
			return null;
		}

		acq = this.group.acquirer;
		final boolean groupHandled = acq != null
				&& acq.similarTransaction(null);
		ResourceTreeEntry<TFacade, ?, ?> treeEntry = this
				.findTreeEntry(groupHandled);
		if (treeEntry == null) {
			return null;
		}
		treeEntry = treeEntry.parent;
		if (treeEntry == null || treeEntry.parent == null) {
			return null;
		}

		//
		// treeEntry �� treeEntry.newPlace ָ���Ӧ����ͬһ�� resourceItem��
		//
		// if (handled && treeEntry.newPlace != null) {
		// treeEntry = treeEntry.newPlace;
		// }

		ResourceItem<TFacade, ?, ?> item = treeEntry.resourceItem;
		acq = item.acquirer;
		itemHandled = acq != null && acq.similarTransaction(null);
		if (itemHandled ? item.state == State.REMOVED
				: item.state == State.FILLED) {
			return null;
		}
		return item;
	}

	@SuppressWarnings("unchecked")
	public final <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
			Class<TSubFacade> subTokenFacadeClass)
			throws IllegalArgumentException {
		if (subTokenFacadeClass == null) {
			throw new NullArgumentException("subTokenFacadeClass");
		}

		if (subTokenFacadeClass == this.group.resourceService.facadeClass) {
			return (ResourceTokenLink<TSubFacade>) this.getChildren();
		}

		Acquirer<?, ?> acq = this.acquirer;
		boolean itemHandled = acq != null && acq.similarTransaction(null);
		if (itemHandled ? this.state == State.REMOVED
				: this.state == State.FILLED) {
			return null;
		}

		//
		// XXX Ŀǰ�в�֧�ָ�����Դ
		//
		// if (this.subResourceGroups != null) {
		// for (ResourceGroup<?, ?, ?> group : this.subResourceGroups) {
		// if (group.resourceService.facadeClass == subTokenFacadeClass) {
		// return ((ResourceGroup<TSubFacade, ?, ?>) group)
		// .getResourceIndex(0).lockNextOf(null);
		// }
		// }
		// }

		ResourceReferenceStorage storage = null;
		ResourceReferenceEntry rre = null;
		synchronized (this) {
			for (storage = this.references; storage != null; storage = storage.next) {
				if (storage.reference.refFacadeClass == subTokenFacadeClass) {
					rre = storage.first;
					break;
				}
			}
		}

		if (rre == null) {
			return null;
		}

		acq = storage.acquirer;
		boolean storageHandled = acq != null && acq.similarTransaction(null);
		while (rre != null
				&& (storageHandled ? rre.state == ResourceReferenceEntry.State.REMOVED
						: rre.state == ResourceReferenceEntry.State.NEW)) {
			rre = rre.next;
		}
		return rre;
	}

	@SuppressWarnings("unchecked")
	public final <TSuperFacade> ResourceItem<TSuperFacade, ?, ?> getSuperToken(
			Class<TSuperFacade> superTokenFacadeClass)
			throws IllegalArgumentException {
		if (superTokenFacadeClass == null) {
			throw new NullArgumentException("superTokenFacadeClass");
		}

		Acquirer<?, ?> acq = this.acquirer;
		if (acq != null && acq.similarTransaction(null) ? this.state == State.REMOVED
				: this.state == State.FILLED) {
			return null;
		}

		if (superTokenFacadeClass == this.group.resourceService.facadeClass) {
			return (ResourceItem<TSuperFacade, ?, ?>) this.getParent();
		}

		ResourceItem<?, ?, ?> os = this.group.ownerResource;
		if (os != null
				&& os.group.resourceService.facadeClass == superTokenFacadeClass) {
			acq = os.acquirer;
			if (acq != null && acq.similarTransaction(null) ? os.state == State.REMOVED
					: os.state == State.FILLED) {
				return null;
			}
			return (ResourceItem<TSuperFacade, ?, ?>) os;
		}

		synchronized (this) {
			ResourceReferenceEntry<TFacade, ?, ?> rre;
			ResourceItem<?, ?, ?> item;
			for (ResourceEntry<TFacade, ?, ?> entry = this.ownerEntries; entry != null; entry = entry.nextSibling) {
				rre = entry.asReferenceEntry();
				if (rre != null) {
					item = ((ResourceReferenceStorage<TFacade>) rre.holder).owner;
					if (item.group.resourceService.facadeClass == superTokenFacadeClass) {
						acq = item.acquirer;
						if (acq != null && acq.similarTransaction(null) ? item.state == State.REMOVED
								: item.state == State.FILLED) {
							return null;
						}
						return (ResourceItem<TSuperFacade, ?, ?>) item;
					}
				}
			}
		}
		return null;
	}

	// /////////////////////////////////////////////////////
	// ��ID��Ϊ���Ĺ�����֧�֣�ȫ����Դ�������
	volatile ResourceItem<?, ?, ?> nextInIDHashTable;

	// -----------------����Ȩ�����----------------------------------

	final ResourceReferenceStorage<?> getReferences() {
		return this.references;
	}

	/**
	 * @return rootNode���������ṹ�еľ��Լ��Ρ�
	 */
	final int fillTree(Operation<? super TFacade> operation,
			TreeNodeImpl<TFacade> rootTreeNode, TransactionImpl transaction) {
		if (rootTreeNode == null) {
			throw new NullArgumentException("rootTreeNode");
		}
		TImpl resource = this.getResource(transaction);
		ResourceTreeEntry<TFacade, TImpl, TKeysHolder> treeEntry = this
				.findTreeEntry(transaction.findResGroupHandle(this.group));
		if (treeEntry == null) {
			return 0;
		}
		rootTreeNode.setElement(resource);
		return this.group.lockFillTreeNode(operation, transaction,
				rootTreeNode, treeEntry);
	}

	final Authority getAuthority(Operation<? super TFacade> operation,
			ContextImpl<?, ?, ?> context, boolean operationAuthority) {
		this.group.resourceService.beforeAccessAuthorityResource(context);
		try {
			if (operationAuthority) {
				return context.getCurrentUserOperationAuthorityChecker()
						.getAuthority(operation, this);
			} else {
				return context.getCurrentUserAccreditAuthorityChecker()
						.getAuthority(operation, this);
			}
		} finally {
			this.group.resourceService.endAccessAuthorityResource(context);
		}
	}

	final boolean validateAuthority(Operation<? super TFacade> operation,
			ContextImpl<?, ?, ?> context, boolean operationAuthority) {
		this.group.resourceService.beforeAccessAuthorityResource(context);
		try {
			if (operationAuthority) {
				return context.getCurrentUserOperationAuthorityChecker()
						.hasAuthority(operation, this);
			} else {
				return context.getCurrentUserAccreditAuthorityChecker()
						.hasAuthority(operation, this);
			}
		} finally {
			this.group.resourceService.endAccessAuthorityResource(context);
		}
	}

	final boolean validateAuthorityInternal(
			Operation<? super TFacade> operation, ContextImpl<?, ?, ?> context,
			boolean operationAuthority) {
		if (operationAuthority) {
			return context.getCurrentUserOperationAuthorityChecker()
					.hasAuthority(operation, this);
		} else {
			return context.getCurrentUserAccreditAuthorityChecker()
					.hasAuthority(operation, this);
		}
	}

	final Authority getAuthority(final ContextImpl<?, ?, ?> context,
			final OperationEntry operationEntry, final long[] acl) {
		return this.internalGetAuthority(operationEntry, acl);
	}

	final boolean validateAuthority(final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl) {
		final Acquirer<?, ?> acq = this.group.acquirer;
		final boolean groupHandled = acq != null
				&& acq.similarTransaction(transaction);
		if (this.tryGetAuthRefrence(transaction) != null) {
			return this.internalValidateAuthority_Reference(transaction,
					operationEntry, acl, groupHandled);
		} else {
			final ResourceTreeEntry<?, ?, ?> treeEntry = this
					.findTreeEntry(groupHandled);
			if (treeEntry != null) {
				return this.internalValidateAuthority_Tree(transaction,
						operationEntry, acl, treeEntry, groupHandled);
			} else {
				final boolean defaultAuth = this.group.resourceService
						.getDefaultAuth();
				return this.internalValidateAuthority_List(transaction,
						operationEntry, acl, defaultAuth);
			}
		}
	}

	/**
	 * ��������Ȩ�޹�����Դ�б�ʱ����
	 */
	final boolean validateAuthority(final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			final boolean defaultAuth) {
		final Acquirer<?, ?> acq = this.group.acquirer;
		final boolean groupHandled = acq != null
				&& acq.similarTransaction(transaction);
		if (this.tryGetAuthRefrence(transaction) != null) {
			return this.internalValidateAuthority_Reference(transaction,
					operationEntry, acl, groupHandled);
		} else {
			final ResourceTreeEntry<?, ?, ?> treeEntry = this
					.findTreeEntry(groupHandled);
			if (treeEntry != null) {
				return this.internalValidateAuthority_Tree(transaction,
						operationEntry, acl, treeEntry, groupHandled);
			} else {
				return this.internalValidateAuthority_List(transaction,
						operationEntry, acl, defaultAuth);
			}
		}
	}

	private final Authority internalGetAuthority(OperationEntry opEntry,
			long[] userACL) {
		int result = ACLHelper.getAuthCode(userACL, this.id) & opEntry.authMask;
		if (result != 0) {
			if (result == opEntry.allowAuthCode) {
				return Authority.ALLOW;
			} else {
				return Authority.DENY;
			}
		}
		return Authority.UNDEFINE;
	}

	private final Authority internalGetAuthority(OperationEntry opEntry,
			long[][] roleACLs) {
		boolean allowed = false;
		for (int index = 1, size = roleACLs.length; index < size; index++) {
			long[] acl = roleACLs[index];
			int result = ACLHelper.getAuthCode(acl, this.id) & opEntry.authMask;
			if (result != 0) {
				if (result == opEntry.allowAuthCode) {
					allowed = true;
				} else {
					return Authority.DENY;
				}
			}
		}
		return allowed ? Authority.ALLOW : Authority.UNDEFINE;
	}

	private final Authority internalGetGroupAuthority(OperationEntry opEntry,
			long[] userACL) {
		int result = ACLHelper.getAuthCode(userACL, this.group.id)
				& opEntry.authMask;
		if (result != 0) {
			if (result == opEntry.allowAuthCode) {
				return Authority.ALLOW;
			} else {
				return Authority.DENY;
			}
		}
		return Authority.UNDEFINE;
	}

	private final Authority internalGetGroupAuthority(OperationEntry opEntry,
			long[][] roleACLs) {
		boolean allowed = false;
		for (int index = 1, size = roleACLs.length; index < size; index++) {
			long[] acl = roleACLs[index];
			int result = ACLHelper.getAuthCode(acl, this.group.id)
					& opEntry.authMask;
			if (result != 0) {
				if (result == opEntry.allowAuthCode) {
					allowed = true;
				} else {
					return Authority.DENY;
				}
			}
		}
		return allowed ? Authority.ALLOW : Authority.UNDEFINE;
	}

	/**
	 * �����ڹ���ͬ����Դ��
	 */
	final boolean internalValidateAuthority_Item(final OperationEntry opEntry,
			final long[][] acl, final boolean defaultAuth) {
		Authority result = this.internalGetAuthority(opEntry, acl[0]);
		if (result != Authority.UNDEFINE) {
			if (result == Authority.ALLOW) {
				return true;
			} else {
				return false;
			}
		}
		result = this.internalGetAuthority(opEntry, acl);
		if (result == Authority.ALLOW) {
			return true;
		} else if (result == Authority.DENY) {
			return false;
		} else {
			return defaultAuth;
		}
	}

	final boolean internalValidateAuthority_List(TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			final boolean defaultAuth) {
		Authority result = this.internalGetAuthority_List(operationEntry, acl);
		if (result == Authority.DENY) {
			return false;
		} else if (result == Authority.ALLOW) {
			return true;
		}
		return defaultAuth;
	}

	private final Authority internalGetAuthority_List(
			final OperationEntry opEntry, final long[][] acl) {
		Authority result = this.internalGetAuthority(opEntry, acl[0]);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		result = this.internalGetAuthority(opEntry, acl);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		result = this.internalGetGroupAuthority(opEntry, acl[0]);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		result = this.internalGetGroupAuthority(opEntry, acl);
		return result;
	}

	private final boolean internalValidateAuthority_Tree(
			final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			final ResourceTreeEntry<?, ?, ?> treeEntry,
			final boolean groupHandled) {
		Authority result = this.internalGetAuthority_Tree(transaction,
				operationEntry, acl, treeEntry, groupHandled);
		if (result == Authority.DENY) {
			return false;
		} else if (result == Authority.ALLOW) {
			return true;
		}
		return this.group.resourceService.getDefaultAuth();
	}

	@SuppressWarnings("unchecked")
	private final Authority internalGetAuthority_Tree(
			final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			ResourceTreeEntry<?, ?, ?> treeEntry, final boolean groupHandled) {
		Authority result;
		ResourceTreeEntry<?, ?, ?> lastEntry = null;
		for (; treeEntry != null; treeEntry = treeEntry.resourceItem
				.findTreeEntry(groupHandled).parent) {
			if (treeEntry.resourceItem == null) {
				if (lastEntry != null) {
					ResourceReferenceStorage _authReference = lastEntry.resourceItem
							.tryGetAuthRefrence(transaction);
					if (_authReference != null) {
						return _authReference.first.resourceItem
								.internalGetAuthority_Reference(
										transaction,
										_authReference.reference.refOperationMap[operationEntry.index],
										acl, groupHandled);
					}
				}
				break;
			}
			result = treeEntry.resourceItem.internalGetAuthority(
					operationEntry, acl[0]);
			if (result != Authority.UNDEFINE) {
				return result;
			}
			result = treeEntry.resourceItem.internalGetAuthority(
					operationEntry, acl);
			if (result == Authority.UNDEFINE) {
				lastEntry = treeEntry;
				continue;
			} else {
				return result;
			}
		}
		result = this.internalGetGroupAuthority(operationEntry, acl[0]);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		result = this.internalGetGroupAuthority(operationEntry, acl);
		return result;
	}

	private final boolean internalValidateAuthority_Reference(
			final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			final boolean groupHandled) {
		Authority result = this.internalGetAuthority_Reference(transaction,
				operationEntry, acl, groupHandled);
		if (result == Authority.DENY) {
			return false;
		} else if (result == Authority.ALLOW) {
			return true;
		}
		return this.group.resourceService.getDefaultAuth();
	}

	private final Authority internalGetAuthority_Reference(
			final TransactionImpl transaction,
			final OperationEntry operationEntry, final long[][] acl,
			final boolean groupHandled) {
		Authority result = this.internalGetAuthority(operationEntry, acl[0]);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		result = this.internalGetAuthority(operationEntry, acl);
		if (result != Authority.UNDEFINE) {
			return result;
		}
		if (this.tryGetAuthRefrence(transaction) != null) {
			return this.authReference.first.resourceItem
					.internalGetAuthority_Reference(
							transaction,
							this.authReference.reference.refOperationMap[operationEntry.index],
							acl, groupHandled);
		}
		final ResourceTreeEntry<?, ?, ?> treeEntry = this
				.findTreeEntry(groupHandled);
		if (treeEntry != null) {
			return this.internalGetAuthority_Tree(transaction, operationEntry,
					acl, treeEntry.parent, groupHandled);
		}
		return Authority.UNDEFINE;
	}

	final int generateAuthorityInfo(final TransactionImpl transaction,
			final long[][] acl) {
		int authInfo = 0;
		int index = 0;
		final OperationEntry[] operationEntrys = this.group.resourceService.authorizableResourceProvider.operations;
		final Acquirer<?, ?> acq = this.group.acquirer;
		final boolean groupHandled = acq == null ? false : (transaction
				.belongs(acq));
		if (this.tryGetAuthRefrence(transaction) != null) {
			for (OperationEntry operationEntry : operationEntrys) {
				if (this.internalValidateAuthority_Reference(transaction,
						operationEntry, acl, groupHandled)) {
					authInfo |= (1 << index);
				}
				index++;
			}
		} else {
			final ResourceTreeEntry<?, ?, ?> treeEntry = this
					.findTreeEntry(false);
			if (treeEntry != null) {
				for (OperationEntry operationEntry : operationEntrys) {
					if (this.internalValidateAuthority_Tree(transaction,
							operationEntry, acl, treeEntry, groupHandled)) {
						authInfo |= (1 << index);
					}
					index++;
				}
			} else {
				final boolean defaultAuth = this.group.resourceService
						.getDefaultAuth();
				for (OperationEntry operationEntry : operationEntrys) {
					if (this.internalValidateAuthority_List(transaction,
							operationEntry, acl, defaultAuth)) {
						authInfo |= (1 << index);
					}
					index++;
				}
			}
		}
		return authInfo;
	}

	final <TRefFacade> void removeReference(
			Operation<? super TRefFacade> operation,
			TransactionImpl transaction,
			ResourceItem<TRefFacade, ?, ?> reference) {
		if (reference.validateAuthority(operation, transaction
				.getCurrentContext(), true)) {
			this.removeReference(transaction, reference);
		} else {
			throw new NoAccessAuthorityException((this.impl.getClass()
					.toString() + "()"), operation);
		}
	}

	@SuppressWarnings("unchecked")
	ResourceReferenceStorage tryGetAuthRefrence(TransactionImpl transaction) {
		if (this.authReference == null
				|| this.authReference.first.state == ResourceReferenceEntry.State.DISPOSED) {
			return null;
		}
		if (transaction.findResRefStorageHandle(this.authReference) == null
				&& this.authReference.first.state == ResourceReferenceEntry.State.NEW) {
			return null;
		}
		return this.authReference;
	}

	@SuppressWarnings("unchecked")
	ResourceReferenceStorage authReference;

	// -----------------����Ȩ�����----------------------------------

	// -----------------���¼�Ⱥ���----------------------------------

	ResourceItem(ResourceGroup<TFacade, TImpl, TKeysHolder> group, long id,
			TImpl resource, TKeysHolder keys) {
		try {
			if (resource == null || keys == null) {
				throw new NullPointerException();
			}
			if (!group.resourceService.implClass.isInstance(resource)) {
				throw new IllegalArgumentException("��Դʵ�����Ͳ���");
			}
			if (!group.resourceService.facadeClass.isInstance(resource)) {
				throw new IllegalArgumentException("��Դδʵ����۽ӿ�");
			}
			if (!group.resourceService.keysClass.isInstance(keys)) {
				throw new IllegalArgumentException("��Դδ��ʵ�ּ���ṹ");
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		this.group = group;
		group.resourceService.site.globalResourceContainer
				.resourceItemCreatedWithID(this, id);
		this.id = id;
		this.impl = resource;
		this.keys = keys;
		this.state = State.FILLED;
	}

	final void clusterModifyResource(final TImpl impl,
			final TKeysHolder keysHolder) {
		this.ensureTempValues();
		this.tempValues.newImpl = impl;
		this.tempValues.newKeys = keysHolder;
		this.tempValues.copyForModification = impl;
		this.state = ResourceItem.State.MODIFIED;
	}

	final void tryAddReferenceStorageTo(final NClusterResourceInitTask task) {
		ResourceReferenceStorage<?> reference = this.references;
		if (reference == null) {
			return;
		}
		while (reference != null) {
			if (reference.reference.refResourceService.kind.inCluster) {
				ResourceReferenceEntry<?, ?, ?> entry = reference.first;
				if (entry != null) {
					final ReferenceStorage storage = task.addReferenceStorage(
							this.id, entry.resourceItem.group.id);
					do {
						storage.addReferenceItem(entry.resourceItem.id);
						entry = entry.next;
					} while (entry != null);
				}
			}
			reference = reference.next;
		}
	}

	// -----------------���ϼ�Ⱥ���----------------------------------

}
