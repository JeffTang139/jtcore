package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.NClusterResourceLockTask.LockAction;
import org.eclipse.jt.core.impl.NClusterResourceUpdateTask.UpdateAction;
import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.invoke.AsyncState;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.resource.ResourceToken;


final class TransactionImpl {

	public static final int INVALID_TRANSACTION_ID = 0;

	final Site site;
	final int id;
	final boolean isLocal;

	private ContextImpl<?, ?, ?> currentContext;

	TransactionImpl(Site site, int id, boolean isLocal) {
		this.site = site;
		this.id = id;
		this.isLocal = isLocal;
	}

	final void bindContext(ContextImpl<?, ?, ?> currentContext) {
		this.currentContext = currentContext;
	}

	public final boolean onCurrentThread() {
		return this.currentContext != null
				&& this.currentContext.thread == Thread.currentThread();
	}

	final ExceptionCatcher getExceptionCatcher() {
		return this.site.application.catcher;
	}

	final ContextImpl<?, ?, ?> getCurrentContext() {
		return this.currentContext;
	}

	/**
	 * �����Ƿ����ڸ�����
	 * 
	 * @param acquirer
	 * @return
	 */
	public boolean belongs(Acquirer<?, ?> acquirer) {
		return acquirer != null && acquirer.getHolder().transaction == this;
	}

	private ResourceAcquirerHolder<ResourceHandleImpl<?, ?, ?>> resHandles;

	/**
	 * ֻ���ⲿ����ʹ�ô˷������ڲ�����ʹ�ã�
	 */
	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder> ResourceHandleImpl<TFacade, TImpl, TKeysHolder> lockResource(
			ResourceDemandFor demandFor, ResourceToken<TFacade> resourceToken) {
		if (resourceToken == null) {
			throw new NullPointerException();
		}
		ResourceItem<TFacade, ?, ?> item = (ResourceItem) resourceToken;
		/*
		 * ��ѯResourceHandle����
		 */
		ResourceHandleImpl resourceHandle = this.findResourceHandle(item,
				demandFor);
		if (resourceHandle != null) {
			return resourceHandle;
		}

		do // ȷ��ResourceItem����Ч��
		{
			if (this.resHandles == null) {
				this.resHandles = new ResourceAcquirerHolder<ResourceHandleImpl<?, ?, ?>>(
						this);
			}
			resourceHandle = new ResourceHandleImpl(this.resHandles, item,
					demandFor, this.currentContext.getDepth());
			resourceHandle.isExternalLock = true;
			if (item.state == State.RESOLVED) {
				break;
			}
			resourceHandle.release();
			throw new IllegalStateException(item.state.toString()); // XXX
		} while (true);

		// ����������
		this.addResourceHandle(resourceHandle);
		return resourceHandle;
	}

	@SuppressWarnings("unchecked")
	static final GetResCallBack REMOVE_RESOURCE = new GetResCallBack() {
		public Object call(TransactionImpl transaction,
				ResourceDemandFor demandFor, ResourceItem item,
				ResourceGroup group, byte resourceIndexIndex, Object key1,
				Object key2, Object key3) {
			if (null != demandFor) {
				throw new IllegalArgumentException();
			}
			if (item == null) {
				return null;
			}
			return item.group.lockRemove(transaction, item);
		}
	};

	static interface GetResCallBack<TResult> {
		@SuppressWarnings("unchecked")
		TResult call(TransactionImpl transaction, ResourceDemandFor demandFor,
				ResourceItem item, ResourceGroup group,
				byte resourceIndexIndex, Object key1, Object key2, Object key3);
	}

	@SuppressWarnings("unchecked")
	static final GetResCallBack<ResourceItem> FIND_RESOURCE = new GetResCallBack<ResourceItem>() {
		public ResourceItem call(TransactionImpl transaction,
				ResourceDemandFor demandFor, ResourceItem item,
				ResourceGroup group, byte resourceIndexIndex, Object key1,
				Object key2, Object key3) {
			if (ResourceDemandFor.READ != demandFor && demandFor != null) {
				throw new IllegalArgumentException();
			}
			return item;
		}
	};
	static final GetResCallBack<Object> INVALID_RESOURCE = new GetResCallBack<Object>() {
		@SuppressWarnings("unchecked")
		public Object call(TransactionImpl transaction,
				ResourceDemandFor demandFor, ResourceItem item,
				ResourceGroup group, byte resourceIndexIndex, Object key1,
				Object key2, Object key3) {
			if (ResourceDemandFor.INVALID != demandFor
					&& ResourceDemandFor.INVALID_DELAY != demandFor) {
				throw new IllegalArgumentException();
			}
			if (item == null) {
				return null;
			}

			boolean flag = false;
			/*
			 * ��ѯResourceHandle����
			 */
			ResourceHandleImpl<?, ?, ?> resourceHandle = transaction
					.findResourceHandle(item, demandFor);
			if (resourceHandle == null) {
				flag = true;
				if (transaction.resHandles == null) {
					transaction.resHandles = new ResourceAcquirerHolder<ResourceHandleImpl<?, ?, ?>>(
							transaction);
				}
				resourceHandle = new ResourceHandleImpl(transaction.resHandles,
						item, demandFor, transaction.currentContext.getDepth());
			}

			/*
			 * ʹ��ԴʧЧ
			 */
			ResourceItem.State temp = item.state;
			try {
				group.invalidResource(transaction.currentContext,
						resourceHandle);
			} catch (RuntimeException e) {
				if (flag) {
					resourceHandle.release();
				}
				item.state = temp;
				throw e;
			}

			if (flag) {
				transaction.addResourceHandle(resourceHandle);
			}
			return null;
		}
	};
	@SuppressWarnings("unchecked")
	static final GetResCallBack<ResourceItem> GET_RESOURCE = new GetResCallBack<ResourceItem>() {
		public ResourceItem call(TransactionImpl transaction,
				ResourceDemandFor demandFor, ResourceItem item,
				ResourceGroup group, byte resourceIndexIndex, Object key1,
				Object key2, Object key3) {
			if (ResourceDemandFor.READ != demandFor && demandFor != null) {
				throw new IllegalArgumentException();
			}
			if (item == null) {
				throw new MissingObjectException();
			}
			return item;
		}
	};

	@SuppressWarnings("unchecked")
	static final GetResCallBack<ResourceHandleImpl> MODIFY_RESOURCE = new GetResCallBack<ResourceHandleImpl>() {
		public ResourceHandleImpl call(TransactionImpl transaction,
				ResourceDemandFor demandFor, ResourceItem item,
				ResourceGroup group, byte resourceIndexIndex, Object key1,
				Object key2, Object key3) {
			if (ResourceDemandFor.MODIFY != demandFor) {
				throw new IllegalArgumentException();
			}
			if (item == null) {
				throw new MissingObjectException();
			}

			/*
			 * ��ѯResourceHandle����
			 */
			ResourceHandleImpl resourceHandle = transaction.findResourceHandle(
					item, demandFor);
			if (resourceHandle == null) {
				do // ȷ��ResourceItem����Ч��
				{
					if (resourceHandle != null) {
						resourceHandle.release();
						item = group.getResourceIndex(resourceIndexIndex)
								.lockGet(key1, key2, key3, transaction);
						if (item == null) {
							throw new MissingObjectException();
						}
					}
					resourceHandle = transaction.lockResourceItem(item,
							demandFor);
				} while (item.impl == null);
				// ����������
				transaction.addResourceHandle(resourceHandle);
			}

			return resourceHandle;
		}
	};

	final void postModifiedResource(Object modifiedResource) {
		if (modifiedResource == null) {
			throw new NullArgumentException("modifiedResource");
		}
		if (this.resHandles != null) {
			this.resHandles.postModifiedResource(this, modifiedResource);
		} else {
			throw new IllegalStateException("��Դ���޸�״̬��������");
		}
	}

	final boolean hasResHandleAboutGroup(ResourceGroup<?, ?, ?> group) {
		return this.resHandles == null ? false : this.resHandles
				.hasResHandleAboutGroup(group);
	}

	/**
	 * ���������в��һ������Դ���
	 * 
	 * @param resourceItem
	 *            ��Դ����
	 * @param demandFor
	 *            ��������
	 * @return ������Դ���
	 */
	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder> ResourceHandleImpl<TFacade, TImpl, TKeysHolder> findResourceHandle(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceDemandFor demandFor) {
		if (this.resHandles != null && !this.resHandles.isEmpty()) {
			ResourceHandleImpl found = this.resHandles
					.getAcquirer(resourceItem);
			if (found != null) {
				if (demandFor != null) {
					demandFor.modeCompatible(found);
				}
				return found;
			}
		}
		return null;
	}

	/**
	 * ���������������Դ���
	 * 
	 * @param resourceItem
	 * @param resourceHandle
	 */
	@SuppressWarnings("unchecked")
	final void addResourceHandle(ResourceHandleImpl resourceHandle) {
		if (this.resHandles == null) {
			this.resHandles = new ResourceAcquirerHolder(this);
		}
		this.resHandles.putAcquirer(resourceHandle);
	}

	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder> ResourceHandleImpl<TFacade, TImpl, TKeysHolder> newResourceHandle(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceDemandFor demandFor) {

		if (this.resHandles == null) {
			this.resHandles = new ResourceAcquirerHolder<ResourceHandleImpl<?, ?, ?>>(
					this);
		}
		return new ResourceHandleImpl<TFacade, TImpl, TKeysHolder>(
				(ResourceAcquirerHolder) this.resHandles, resourceItem,
				demandFor, this.currentContext.getDepth());
	}

	/**
	 * ������Դ����Ҽ��뵽Context�����С�
	 * 
	 * @param resourceItem
	 * @param demandFor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder> ResourceHandleImpl<TFacade, TImpl, TKeysHolder> tryHandleItemIntoContextIfNot(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceDemandFor demandFor) {
		ResourceHandleImpl handle = this.findResourceHandle(resourceItem,
				demandFor);
		if (handle != null) {
			return handle;
		}
		handle = lockResourceItem(resourceItem, demandFor);
		this.addResourceHandle(handle);
		return handle;
	}

	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder> ResourceHandleImpl<TFacade, TImpl, TKeysHolder> lockResourceItem(
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceDemandFor demandFor) {
		if (this.resHandles == null) {
			this.resHandles = new ResourceAcquirerHolder<ResourceHandleImpl<?, ?, ?>>(
					this);
		}
		final ResourceHandleImpl handle = new ResourceHandleImpl(
				this.resHandles, resourceItem, demandFor, this.currentContext
						.getDepth());
		return handle;
	}

	final <TFacade, TImpl extends TFacade, TKeysHolder> void tryLockItemInCluster(
			ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle) {
		try {
			if (this.isLocal && handle.isExclusive()) {
				ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem = handle.res;
				if (resourceItem.group.inCluster) {
					final NetClusterImpl cluster = this.site.application
							.getNetCluster();
					if (cluster.haveRemoteNode()) {
						if (!this.tryLockInCluster(cluster,
								NClusterResourceLockTask
										.newLockItemAction(resourceItem.id))) {
							throw new RuntimeException("��Ⱥ����ʧ��");
						}
					}
				}
			}
		} catch (Throwable e) {
			handle.release();
			throw Utils.tryThrowException(e);
		}
	}

	/* ````````````````````````````````````````````````````````````````` */
	/* ResourceGroup ���������� ----------------------------------- ��ʼ */
	private ResourceGroupAcquirerHolder<ResourceGroupHandle> resGroupHandles;

	/**
	 * ���������������Դ����ľ��
	 */
	final void addResGroupHandle(ResourceGroupHandle resGroupHandle) {
		if (this.resGroupHandles == null) {
			this.resGroupHandles = new ResourceGroupAcquirerHolder<ResourceGroupHandle>(
					this);
		}
		this.resGroupHandles.putAcquirer(resGroupHandle);
	}

	/**
	 * ���������в��һ������Դ����ľ��
	 */
	@SuppressWarnings("unchecked")
	final ResourceGroupHandle findResGroupHandle(ResourceGroup resourceGroup) {
		if (this.resGroupHandles != null) {
			return this.resGroupHandles.getAcquirer(resourceGroup);
		}
		return null;
	}

	/**
	 * �����������Ƴ���Դ����ľ��
	 */
	final void removeResGroupHandle(ResourceGroupHandle handle) {
		if (this.resGroupHandles != null && this.resGroupHandles.size() > 0) {
			this.resGroupHandles.removeAcquirer(handle);
		}
	}

	final ResourceGroupHandle tryHandleGroupIntoContextIfNot(
			ResourceGroup<?, ?, ?> group) {
		if (group == null) {
			throw new NullPointerException();
		}
		ResourceGroupHandle handle = this.findResGroupHandle(group);
		if (handle == null) {
			if (this.resGroupHandles == null) {
				this.resGroupHandles = new ResourceGroupAcquirerHolder<ResourceGroupHandle>(
						this);
			}
			handle = new ResourceGroupHandle(this.resGroupHandles, group);
			this.addResGroupHandle(handle);
		}
		return handle;
	}

	final void tryLockGroupInCluster(ResourceGroupHandle handle,
			ResourceGroup<?, ?, ?> group) {
		try {
			if (this.isLocal) {
				final NetClusterImpl cluster = this.site.application
						.getNetCluster();
				if (group.inCluster && cluster.haveRemoteNode()) {
					if (!this.tryLockInCluster(cluster,
							NClusterResourceLockTask
									.newLockGroupAction(group.id))) {
						throw new RuntimeException("��Ⱥ����ʧ��");
					}
				}
			}
		} catch (Throwable e) {
			handle.release();
			throw Utils.tryThrowException(e);
		}
	}

	/* ResourceGroup ���������� ----------------------------------- ���� */
	/* ................................................................. */

	/* ````````````````````````````````````````````````````````````````` */
	/* ResourceReferenceStorage ���������� ------------------------ ��ʼ */
	private ResRefStorageAcquirerHolder<ResRefStorageHandle> resRefStorageHandles;

	/**
	 * ���������������Դ���ô洢�ľ��
	 */
	final void addResRefStorageHandle(ResRefStorageHandle resRefStorageHandle) {
		if (this.resRefStorageHandles == null) {
			this.resRefStorageHandles = new ResRefStorageAcquirerHolder<ResRefStorageHandle>(
					this);
		}
		this.resRefStorageHandles.putAcquirer(resRefStorageHandle);
	}

	/**
	 * ���������в��һ������Դ���ô洢�ľ��
	 */
	@SuppressWarnings("unchecked")
	final ResRefStorageHandle findResRefStorageHandle(
			ResourceReferenceStorage resourceRefStorage) {
		if (this.resRefStorageHandles != null
				&& this.resRefStorageHandles.size() > 0) {
			return this.resRefStorageHandles.getAcquirer(resourceRefStorage);
		}
		return null;
	}

	/**
	 * �����������Ƴ���Դ���ô洢�ľ��
	 */
	final void removeResRefStorageHandle(ResRefStorageHandle handle) {
		if (this.resRefStorageHandles != null
				&& this.resRefStorageHandles.size() > 0) {
			this.resRefStorageHandles.removeAcquirer(handle);
		}
	}

	final ResRefStorageHandle tryHandleResRefStorageIntoContextIfNot(
			ResourceReferenceStorage<?> resRefStorage) {
		if (resRefStorage == null) {
			throw new NullPointerException();
		}
		ResRefStorageHandle handle = this
				.findResRefStorageHandle(resRefStorage);
		if (handle == null) {
			if (this.resRefStorageHandles == null) {
				this.resRefStorageHandles = new ResRefStorageAcquirerHolder<ResRefStorageHandle>(
						this);
			}
			handle = new ResRefStorageHandle(this.resRefStorageHandles,
					resRefStorage);
			this.addResRefStorageHandle(handle);
		}
		return handle;
	}

	final void tryLockReferenceInCluster(final ResRefStorageHandle handle,
			final ResourceReferenceStorage<?> resRefStorage) {
		try {
			if (this.isLocal) {
				final NetClusterImpl cluster = this.site.application
						.getNetCluster();
				if (resRefStorage.owner.group.inCluster
						&& cluster.haveRemoteNode()) {
					if (!this.tryLockInCluster(cluster,
							NClusterResourceLockTask.newLockReferenceAction(
									resRefStorage.owner.id,
									resRefStorage.reference.refFacadeClass))) {
						throw new RuntimeException("��Ⱥ����ʧ��");
					}
				}
			}
		} catch (Throwable e) {
			handle.release();
			throw Utils.tryThrowException(e);
		}
	}

	/* ResourceReferenceStorage ���������� ------------------------ ���� */
	/* ................................................................. */
	final void resetTrans(boolean commitTrans) {
		if (this.isEmpty()) {
			return;
		}
		final ApplicationImpl app = this.site.application;
		if (this.isLocal) {
			final NetClusterImpl cluster = app.getNetCluster();
			if (cluster.haveRemoteNode()) {
				final List<UpdateAction> actions = this
						.biuldClusterResourceUpdateTask().getUpdateActionList();
				if (actions.size() != 0) {
					if (commitTrans
							&& this.tryUpdateInCluster(cluster, actions)) {
						this.tryResovleTransactionInCluster(cluster, true);
					} else {
						commitTrans = false;
						this.tryResovleTransactionInCluster(cluster, false);
					}
				}
			}
		}

		if (commitTrans) {
			if (this.resHandles != null) {
				this.resHandles.commit(app.catcher);
			}

			if (this.resGroupHandles != null) {
				this.resGroupHandles.commit();
			}

			if (this.resRefStorageHandles != null) {
				this.resRefStorageHandles.commit();
			}
		} else {
			if (this.resHandles != null) {
				this.resHandles.rollback(app.catcher);
			}

			if (this.resGroupHandles != null) {
				this.resGroupHandles.rollback();
			}

			if (this.resRefStorageHandles != null) {
				this.resRefStorageHandles.rollback();
			}
		}
	}

	// ---------------------------���¼�Ⱥ���------------------------------

	final NClusterResourceUpdateTask biuldClusterResourceUpdateTask() {
		final NClusterResourceUpdateTask task = new NClusterResourceUpdateTask();
		if (this.resHandles != null) {
			this.resHandles.buildClusterResourceUpdateTask(task);
		}
		if (this.resGroupHandles != null) {
			this.resGroupHandles.buildClusterResourceUpdateTask(task);
		}
		if (this.resRefStorageHandles != null) {
			this.resRefStorageHandles.buildClusterResourceUpdateTask(task);
		}
		return task;
	}

	final boolean isEmpty() {
		return (this.resHandles == null || this.resHandles.isEmpty())
				&& (this.resGroupHandles == null || this.resGroupHandles
						.isEmpty())
				&& (this.resRefStorageHandles == null || this.resRefStorageHandles
						.isEmpty());
	}

	final boolean tryUpdateInCluster(final NetClusterImpl cluster,
			final List<UpdateAction> updateActionList) {
		// ���ͼ�������
		NetNodeImpl netNode = cluster.getFirstNetNode();
		NetSessionImpl netSession;
		NClusterResourceUpdateTask task;
		final ArrayList<AsyncTask<NClusterResourceUpdateTask, None>> taskList = new ArrayList<AsyncTask<NClusterResourceUpdateTask, None>>();
		do {
			task = new NClusterResourceUpdateTask();
			task.addUpdateActionList(updateActionList);
			netSession = netNode.newSession();
			taskList.add(netSession.newRemoteTransactionRequest(task,
					None.NONE, this));
			netNode = netNode.getNextNodeInCluster();
		} while (netNode != null);
		// �ȴ���Ӧ
		try {
			ContextImpl.internalWaitFor(0L, null, taskList
					.toArray(new AsyncTask[taskList.size()]));
			for (AsyncTask<NClusterResourceUpdateTask, None> asyncTask : taskList) {
				if (asyncTask.getState() == AsyncState.FINISHED) {
					if (asyncTask.getTask().getClusterTaskState() == ClusterSynTask.State.HANDLE_SUCCESSED) {
						continue;
					}
					return false;
				}
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	final boolean tryLockInCluster(final NetClusterImpl cluster,
			final LockAction lockAction) {
		// ���ͼ�������
		NetNodeImpl netNode = cluster.getFirstNetNode();
		NetSessionImpl netSession;
		NClusterResourceLockTask task;
		final ArrayList<AsyncTask<NClusterResourceLockTask, None>> taskList = new ArrayList<AsyncTask<NClusterResourceLockTask, None>>();
		do {
			task = new NClusterResourceLockTask();
			task.addLockAction(lockAction);
			netSession = netNode.newSession();
			taskList.add(netSession.newRemoteTransactionRequest(task,
					None.NONE, this));
			netNode = netNode.getNextNodeInCluster();
		} while (netNode != null);
		// �ȴ���Ӧ
		try {
			ContextImpl.internalWaitFor(0L, null, taskList
					.toArray(new AsyncTask[taskList.size()]));
			for (AsyncTask<NClusterResourceLockTask, None> asyncTask : taskList) {
				if (asyncTask.getState() == AsyncState.FINISHED
						&& asyncTask.getTask().getClusterTaskState() == ClusterSynTask.State.HANDLE_SUCCESSED) {
					continue;
				}
				return false;
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	final boolean tryResovleTransactionInCluster(final NetClusterImpl cluster,
			final boolean commit) {
		// ���ͼ�������
		NetNodeImpl netNode = cluster.getFirstNetNode();
		NetSessionImpl netSession;
		NClusterTransactionTask task;
		final ArrayList<AsyncTask<NClusterTransactionTask, None>> taskList = new ArrayList<AsyncTask<NClusterTransactionTask, None>>();
		do {
			task = new NClusterTransactionTask(commit);
			netSession = netNode.newSession();
			taskList.add(netSession.newRemoteTransactionRequest(task,
					None.NONE, this));
			netNode = netNode.getNextNodeInCluster();
		} while (netNode != null);
		// �ȴ���Ӧ
		try {
			ContextImpl.internalWaitFor(0L, null, taskList
					.toArray(new AsyncTask[taskList.size()]));
			for (AsyncTask<NClusterTransactionTask, None> asyncTask : taskList) {
				if (asyncTask.getState() == AsyncState.FINISHED) {
					if (asyncTask.getTask().getClusterTaskState() == ClusterSynTask.State.HANDLE_SUCCESSED) {
						continue;
					}
					return false;
				}
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------------------���ϼ�Ⱥ���------------------------------

}
