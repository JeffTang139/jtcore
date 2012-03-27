package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.RemoteLoginInfo;
import org.eclipse.jt.core.RemoteLoginLife;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.TreeNodeFilter;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.auth.RoleAuthorityChecker;
import org.eclipse.jt.core.auth.UserAuthorityChecker;
import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.da.RecordSet;
import org.eclipse.jt.core.def.info.ErrorInfoDefine;
import org.eclipse.jt.core.def.info.HintInfoDefine;
import org.eclipse.jt.core.def.info.InfoDefine;
import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.def.info.ProcessInfoDefine;
import org.eclipse.jt.core.def.info.WarningInfoDefine;
import org.eclipse.jt.core.def.model.ModelScriptContext;
import org.eclipse.jt.core.def.model.ModelScriptEngine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.ModifyStatementDefine;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.def.query.StatementDeclarator;
import org.eclipse.jt.core.def.query.StatementDeclare;
import org.eclipse.jt.core.def.query.StatementDefine;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDefine;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.exception.AbortException;
import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.DisposedException;
import org.eclipse.jt.core.exception.EndProcessException;
import org.eclipse.jt.core.exception.NoAccessAuthorityException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.exception.SituationReentrantException;
import org.eclipse.jt.core.impl.ResourceItem.State;
import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceIndexInfo;
import org.eclipse.jt.core.impl.ResourceServiceBase.RestResourceIndexInfo;
import org.eclipse.jt.core.impl.ServiceBase.CaseTester;
import org.eclipse.jt.core.impl.SpaceNode.AsyncEvent;
import org.eclipse.jt.core.impl.TransactionImpl.GetResCallBack;
import org.eclipse.jt.core.info.Info;
import org.eclipse.jt.core.info.InfoInterrupt;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncResult;
import org.eclipse.jt.core.invoke.AsyncResultList;
import org.eclipse.jt.core.invoke.AsyncState;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.invoke.TaskState;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResult;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResultList;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.misc.SortUtil;
import org.eclipse.jt.core.resource.CategorialResourceModifier;
import org.eclipse.jt.core.resource.ResourceContext;
import org.eclipse.jt.core.resource.ResourceHandle;
import org.eclipse.jt.core.resource.ResourceStub;
import org.eclipse.jt.core.resource.ResourceToken;
import org.eclipse.jt.core.resource.ResourceService.WhenExists;
import org.eclipse.jt.core.service.AsyncInfo;
import org.eclipse.jt.core.service.NativeDeclaratorResolver;
import org.eclipse.jt.core.service.ServiceInvoker;
import org.eclipse.jt.core.spi.application.ContextSPI;
import org.eclipse.jt.core.spi.application.SessionDisposeEvent;
import org.eclipse.jt.core.testing.TestContext;
import org.eclipse.jt.core.type.GUID;


/**
 * 上下文实现类
 * 
 * @author Jeff Tang
 * 
 */
final class ContextImpl<TFacadeM, TImplM extends TFacadeM, TKeysHolderM>
		implements ResourceContext<TFacadeM, TImplM, TKeysHolderM>,
		CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM>, ContextSPI,
		NativeDeclaratorResolver {

	/**
	 * 上下文对应的事务
	 */
	final TransactionImpl transaction;

	/**
	 * 是否在同一个事务中
	 * 
	 * @param transaction
	 * @return
	 */
	final boolean inSameTransaction(Acquirer<?, ?> acquirer) {
		return this.transaction == acquirer.getHolder().transaction;
	}

	/**
	 * 会话中的下一个
	 */
	ContextImpl<?, ?, ?> nextInSession;
	/**
	 * 会话中的前一个
	 */
	ContextImpl<?, ?, ?> prevInSession;

	final static ContextImpl<?, ?, ?> toContext(Context anInterface) {
		if (anInterface instanceof ContextImpl<?, ?, ?>) {
			return (ContextImpl<?, ?, ?>) anInterface;
		} else if (anInterface instanceof SituationImpl) {
			return ((SituationImpl) anInterface).usingSituation();
		} else if (anInterface == null) {
			return null;
		} else {
			throw new IllegalArgumentException("无效的Context");
		}
	}

	/**
	 * 登陆信息对象
	 */
	public final SessionImpl getLogin() {
		this.checkValid();
		return this.session;
	}

	public final float getResistance() {
		this.checkValid();
		return 0;
	}

	/**
	 * 抛出的异常对象
	 * 
	 * @param throwable
	 *            需要抛出的异常对象
	 */
	public final RuntimeException throwThrowable(Throwable throwable) {
		return Utils.tryThrowException(throwable);
	}

	public final void updateSpace(String spacePath, char spaceSeparator) {
		this.checkValid();
		this.occorAt = this.session.application.getDefaultSite()
				.tryLocateSpace(spacePath, spaceSeparator);
	}

	public final SiteState getSiteState() {
		return this.occorAt.site.state;
	}

	public final GUID getSiteID() {
		return this.occorAt.site.id;
	}

	public final int getSiteSimpleID() {
		return this.occorAt.site.asSimpleID();
	}

	public final <TFacade> void ensureResourceInited(Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		this.makeSureResourceInited(facadeClass, this.getCategory());
	}

	/**
	 * 确保指定类型的资源的服务已经初始化
	 */
	final void makeSureResourceInited(Class<?> facadeClass, Object category) {
		ResourceServiceBase<?, ?, ?> service = this.occorAtResourceService;
		if (service == null || facadeClass != service.facadeClass) {
			service = this.occorAt.findResourceService(facadeClass, this
					.getInvokeeQueryMode());
			if (service == null) {
				return;
			}
		}
		service.ensureResourceGroup(category, this);
	}

	/**
	 * 根据资源外观类型找到相应的资源管理器
	 * 
	 * @param facadeClass
	 *            资源外观类
	 * @param category
	 *            资源类别
	 * @param demandFor
	 *            请求模式
	 * @return 返回资源管理器
	 */
	final ResourceIndexInfo findResourceIndexInfo(Class<?> facadeClass,
			Object key1, Object key2, Object key3, Object[] otherKeys) {
		ResourceServiceBase<?, ?, ?> service = this.occorAtResourceService;
		if (service == null || facadeClass != service.facadeClass) {
			service = this.occorAt.findResourceService(facadeClass, this
					.getInvokeeQueryMode());
			if (service == null) {
				return null;
			}
		}
		return service.findIndexInfo(null, key1, key2, key3, otherKeys);
		//
		// 目前的调用者都是在外面进行判断和处理
		// 返回null表示没有资源的相关定义
		//
		// if (resourceIndexInfo == null) {
		// throw ServiceInvokeeBase.noResourceException(facadeClass,
		// key1,
		// key2, key3, otherKeys);
		// }
	}

	final ResourceIndexInfo findResourceIndexInfoByKeyClass(
			Class<?> facadeClass, Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, Class<?>[] otherKeyClasses) {
		ResourceServiceBase<?, ?, ?> service = this.occorAtResourceService;
		if (service == null || facadeClass != service.facadeClass) {
			service = this.occorAt.findResourceService(facadeClass, this
					.getInvokeeQueryMode());
			if (service == null) {
				return null;
			}
		}
		return service.findIndexInfoByKeyClass(null, key1Class, key2Class,
				key3Class, otherKeyClasses);
	}

	/**
	 * 根据资源外观类型找某指定资源管理器下的相应子资源管理器
	 * 
	 * @param holder
	 *            是fromResourceService的上级ResourceService
	 * @param facadeClass
	 *            资源外观类
	 * @param category
	 *            资源类别
	 * @param demandFor
	 *            请求模式
	 * @return 返回资源管理器
	 */
	private final ResourceIndexInfo findSubResourceIndexInfo(
			final ResourceServiceBase<?, ?, ?> holder, Class<?> facadeClass,
			Object key1, Object key2, Object key3, Object[] otherKeys) {
		ResourceServiceBase<?, ?, ?> service = this.occorAtResourceService;
		if (service == null || facadeClass != service.facadeClass) {
			service = this.occorAt.findResourceService(facadeClass, this
					.getInvokeeQueryMode());
			if (service == null) {
				return null;
			}
		}
		ResourceIndexInfo rii = service.subFindIndexInfo(holder, key1, key2,
				key3, otherKeys);
		if (rii == null) {
			throw ServiceInvokeeBase.noResourceException(facadeClass, key1,
					key2, key3, otherKeys);
		}
		return rii;
	}

	/**
	 * 查找holderService之下的，facadeClass类型的上级资源管理器对应的信息。
	 * 
	 * 指定的键值，从holderService的下一级开始，到facadeClass类型的资源的上一级结束。
	 */
	private ResourceIndexInfo findSubParentResourceIndexInfo(
			ResourceServiceBase<?, ?, ?> holderService, Class<?> facadeClass,
			Object key1, Object key2, Object key3, Object[] otherKeys) {
		ResourceServiceBase<?, ?, ?> service = this.occorAtResourceService;
		if (service == null || facadeClass != service.facadeClass) {
			service = this.occorAt.findResourceService(facadeClass, this
					.getInvokeeQueryMode());
			if (service == null) {
				return null;
			}
		}
		service = service.ownerResourceService;
		if (service == null) {
			return null;
		}
		ResourceServiceBase<?, ?, ?> fromResourceService;
		if (holderService != null) {
			fromResourceService = service;
			while (fromResourceService != null
					&& fromResourceService.ownerResourceService != holderService) {
				fromResourceService = fromResourceService.ownerResourceService;
			}
			if (fromResourceService == null) {
				return null;
			}
		} else {
			fromResourceService = null;
		}
		ResourceIndexInfo rii = service.findIndexInfo(fromResourceService,
				key1, key2, key3, otherKeys);
		if (rii == null) {
			throw ServiceInvokeeBase.noResourceException(facadeClass, key1,
					key2, key3, otherKeys);
		}
		return rii;
	}

	final <TResult> TResult internalFindResource(Operation<?> operation,
			GetResCallBack<TResult> callback, ResourceDemandFor demandFor,
			Class<?> facadeClass, Object category, Object key1, Object key2,
			Object key3, Object[] otherKeys) {
		this.checkValid();
		ResourceIndexInfo rii = this.findResourceIndexInfo(facadeClass, key1,
				key2, key3, otherKeys);
		if (rii == null) {
			return null;
		}
		return this.internalFindResource(operation, rii, callback, demandFor,
				category, key1, key2, key3, otherKeys);
	}

	/**
	 * 查找holderItem下的类型为facadeClass的子资源。
	 * 
	 * 提供的键值从holderItem的下一级开始。
	 * 
	 * @param holderItem
	 */
	final <TResult> TResult findSubResource(GetResCallBack<TResult> callback,
			ResourceItem<?, ?, ?> holderItem, ResourceDemandFor demandFor,
			Class<?> facadeClass, Object category, Object key1, Object key2,
			Object key3, Object[] otherKeys) {
		ResourceIndexInfo rii = this.findSubResourceIndexInfo(
				holderItem.group.resourceService, facadeClass, key1, key2,
				key3, otherKeys);
		return this.findSubResource(holderItem.getSubResourceGroup(
				rii.fromResourceService.facadeClass, this), rii, callback,
				demandFor, category, key1, key2, key3, otherKeys);
	}

	/**
	 * 在holderItem下查找facadeClass类型的资源的上级资源。
	 * 
	 * 指定的键值从holderItem的下一级资源开始，到facadeClass类型的资源的上一级结束。
	 * 
	 * @param holderItem
	 */
	final <TResult> TResult findSubParentResource(
			GetResCallBack<TResult> callback, ResourceItem<?, ?, ?> holderItem,
			ResourceDemandFor demandFor, Class<?> facadeClass, Object category,
			Object key1, Object key2, Object key3, Object[] otherKeys) {
		ResourceIndexInfo rii = this.findSubParentResourceIndexInfo(
				holderItem.group.resourceService, facadeClass, key1, key2,
				key3, otherKeys);
		return this.findSubResource(holderItem.getSubResourceGroup(
				rii.fromResourceService.facadeClass, this), rii, callback,
				demandFor, category, key1, key2, key3, otherKeys);
	}

	@SuppressWarnings("unchecked")
	final <TResult> TResult internalFindResource(Operation<?> operation,
			ResourceIndexInfo rii, GetResCallBack<TResult> callback,
			ResourceDemandFor demandFor, Object category, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		if (rii == null) {
			throw new NullPointerException();
		}
		final ResourceServiceBase resourceService = rii.fromResourceService;
		ResourceGroup group = resourceService.ensureResourceGroup(category,
				this);
		if (group == null) {
			return null;
		}
		ResourceItem resourceItem = null;
		RestResourceIndexInfo rrii = rii;
		int k = 0;
		int otherKeysCount = otherKeys != null ? otherKeys.length : 0;
		do {
			if (ResourceDemandFor.INVALID == demandFor
					|| ResourceDemandFor.INVALID_DELAY == demandFor) {
				resourceItem = group.getResourceIndex(rrii.resourceIndexIndex)
						.lockFind(this, key1, key2, key3);
			} else {
				resourceItem = group.getResourceIndex(rrii.resourceIndexIndex)
						.lockGet(key1, key2, key3, this.transaction);
			}
			if (resourceItem == null) {
				return null;
			}
			byte keyCount = rrii.keyCount;
			if (rrii.subIndexInfo == null) {
				break;
			}
			group = resourceItem.getSubResourceGroup(rrii.subResourceIndex,
					this);
			rrii = rrii.subIndexInfo;
			switch (keyCount) {
			case 1:
				key1 = key2;
				key2 = key3;
				if (k < otherKeysCount) {
					key3 = otherKeys[k++];
				}
				continue;
			case 2:
				key1 = key3;
				if (k < otherKeysCount) {
					key2 = otherKeys[k++];
				}
				if (k < otherKeysCount) {
					key3 = otherKeys[k++];
				}
				continue;
			case 0:
				continue;
			case 3:
				if (k < otherKeysCount) {
					key1 = otherKeys[k++];
				}
				if (k < otherKeysCount) {
					key2 = otherKeys[k++];
				}
				if (k < otherKeysCount) {
					key3 = otherKeys[k++];
				}
				continue;
			}
			throw new UnsupportedOperationException();
		} while (true);
		if (operation == null
				|| resourceItem.validateAuthority(operation, this, true)) {
			return callback.call(this.transaction, demandFor, resourceItem,
					group, rrii.resourceIndexIndex, key1, key2, key3);
		}
		// 构造异常信息，并抛出无访问权限异常
		String resourceDescription = resourceItem.impl.getClass().toString()
				+ "(" + key1 + ", " + key2 + ", " + key3;
		if (otherKeys != null) {
			for (Object key : otherKeys) {
				resourceDescription += ", " + key;
			}
		}
		resourceDescription += ")";
		throw new NoAccessAuthorityException(resourceDescription, operation);
	}

	@SuppressWarnings("unchecked")
	private final <TResult> TResult findSubResource(ResourceGroup group,
			ResourceIndexInfo rii, GetResCallBack<TResult> callback,
			ResourceDemandFor demandFor, Object category, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		if (group != null && rii != null) {
			ResourceItem resourceItem = null;
			RestResourceIndexInfo rrii = rii;
			int k = 0;
			int otherKeysCount = otherKeys != null ? otherKeys.length : 0;
			do {
				if (ResourceDemandFor.INVALID == demandFor
						|| ResourceDemandFor.INVALID_DELAY == demandFor) {
					resourceItem = group.getResourceIndex(
							rrii.resourceIndexIndex).lockFind(this, key1, key2,
							key3);
				} else {
					resourceItem = group.getResourceIndex(
							rrii.resourceIndexIndex).lockGet(key1, key2, key3,
							this.transaction);
				}
				if (resourceItem == null) {
					return null;
				}
				byte keyCount = rrii.keyCount;
				if (rrii.subIndexInfo == null) {
					break;
				}
				group = resourceItem.getSubResourceGroup(rrii.subResourceIndex,
						this);
				rrii = rrii.subIndexInfo;
				switch (keyCount) {
				case 1:
					key1 = key2;
					key2 = key3;
					if (k < otherKeysCount) {
						key3 = otherKeys[k++];
					}
					continue;
				case 2:
					key1 = key3;
					if (k < otherKeysCount) {
						key2 = otherKeys[k++];
					}
					if (k < otherKeysCount) {
						key3 = otherKeys[k++];
					}
					continue;
				case 0:
					continue;
				case 3:
					if (k < otherKeysCount) {
						key1 = otherKeys[k++];
					}
					if (k < otherKeysCount) {
						key2 = otherKeys[k++];
					}
					if (k < otherKeysCount) {
						key3 = otherKeys[k++];
					}
					continue;
				}
				throw new UnsupportedOperationException();
			} while (true);
			return callback.call(this.transaction, demandFor, resourceItem,
					group, rrii.resourceIndexIndex, key1, key2, key3);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	final <TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2, TKey3> void loadLockedResource(
			final ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> provider,
			final ResourceHandleImpl<TFacade, TImpl, TKeysHolder> handle,
			final TKey1 key1, final TKey2 key2, final TKey3 key3) {
		final SpaceNode occorAtSave = handle.res.group.resourceService
				.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			if (key1 == null) {
				provider.provide(this, new ResourceItemAdapter(
						this.transaction, handle.res));
			} else if (key2 == null) {
				provider.provide(this, new ResourceItemAdapter(
						this.transaction, handle.res), key1);
			} else if (key3 == null) {
				provider.provide(this, new ResourceItemAdapter(
						this.transaction, handle.res), key1, key2);
			} else {
				provider.provide(this, new ResourceItemAdapter(
						this.transaction, handle.res), key1, key2, key3);
			}
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	// ///////////////////////////////////////////////////////
	// /////// 任务处理
	// ///////////////////////////////////////////////////////
	/**
	 * 处理任务
	 * 
	 * @param task
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	final void serviceHandleTask(Task<?> task, ServiceInvokeeBase methodHandler) {
		final ServiceBase<?> handlerService = methodHandler.getService();
		final SpaceNode occorAtSave = handlerService.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		TaskState exceptionState = TaskState.PREPARERROR;
		try {
			Utils.taskAccessor.setTaskState(task, TaskState.PREPARING);
			methodHandler.prepare(this, task);
			Utils.taskAccessor.setTaskState(task, TaskState.PREPARED);
			List<Task<?>> subTasks = Utils.taskAccessor.getSubTasks(task);
			if (subTasks != null) {
				for (int i = 0, c = subTasks.size(); i < c; i++) {
					Task<?> subTask = subTasks.get(i);
					this.serviceHandleTask(subTask, handlerService.space
							.getTaskHandler(subTask.getClass(), subTask
									.getMethod(), this.getInvokeeQueryMode()));
				}
			}
			Utils.taskAccessor.setTaskState(task, TaskState.PROCESSING);
			exceptionState = TaskState.PROCESSERROR;
			methodHandler.handle(this, task);
			if (this.exception != null) {
				throw this.exception;
			}
			Utils.taskAccessor.setTaskState(task, TaskState.PROCESSED);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			Utils.taskAccessor.setTaskState(task, exceptionState);
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	/**
	 * 处理某任务
	 * 
	 * @param task
	 *            待处理的任务
	 * @param method
	 *            任务的方法
	 * @param tranMode
	 *            任务的事务模式
	 * @throws Throwable
	 *             处理错误异常
	 */
	public final <TMethod extends Enum<TMethod>> void handle(
			Task<TMethod> task, TMethod method) throws DeadLockException {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		this.checkValid();
		@SuppressWarnings("unchecked")
		ServiceInvokeeBase methodHandler = this.occorAt.getTaskHandler(task
				.getClass(), method, this.getInvokeeQueryMode());
		method = Utils.taskAccessor.setTaskMethod(task, method);
		try {
			this.serviceHandleTask(task, methodHandler);
		} finally {
			Utils.taskAccessor.setTaskMethod(task, method);
		}
	}

	/**
	 * 处理某任务
	 * 
	 * @param task
	 *            待处理的任务
	 * @param method
	 *            任务的方法
	 * @throws Throwable
	 *             处理错误异常
	 */
	public final void handle(SimpleTask task) throws DeadLockException {
		this.handle(task, None.NONE);
	}

	/**
	 * 异步处理任务
	 */
	@SuppressWarnings("unchecked")
	private final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> doAsyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		if (task == null || method == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		ServiceInvokeeBase methodHandler = this.occorAt.getTaskHandler(task
				.getClass(), method, this.getInvokeeQueryMode());
		method = Utils.taskAccessor.setTaskMethod(task, method);
		Utils.taskAccessor.setTaskState(task, TaskState.PREPARING);
		return new AsyncTaskImpl<TTask, TMethod>(this.session, this.occorAt,
				task, methodHandler, info);

	}

	/**
	 * 异步处理任务
	 */
	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method) {
		return this.doAsyncHandle(task, method, null);
	}

	/**
	 * 异步处理简单任务
	 */
	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task) {
		return this.doAsyncHandle(task, None.NONE, null);
	}

	/**
	 * 空事件句柄
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private static final class EmptyEventHandle implements Waitable,
			AsyncHandle {

		public final int fetchInfos(List<Info> to) {
			return 0;
		}

		public final void waitStop(long timeout) throws InterruptedException {
		}

		public final void cancel() {
		}

		public final Throwable getException() {
			return null;
		}

		public final float getProgress() {
			return 1f;
		}

		public final AsyncState getState() {
			return AsyncState.FINISHED;
		}
	}

	private static EmptyEventHandle emptyEventHandle = new EmptyEventHandle();

	private final InvokeeQueryMode getInvokeeQueryMode() {
		if (this.depth == 0 && (this.session.kind == SessionKind.REMOTE)) {
			return InvokeeQueryMode.FROM_OTHER_SITE;
		} else {
			return InvokeeQueryMode.IN_SITE;
		}
	}

	private static void checkEvent(Event event) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		if (event instanceof SessionDisposeEvent) {
			throw new UnsupportedOperationException("不支持该类别的事件调用");
		}
	}

	/**
	 * 触发事件，事件永远是异步处理的，即该方法一经调用马上返回
	 * 
	 * @param event
	 *            事件对象
	 */
	public final AsyncHandle occur(Event event) {
		checkEvent(event);
		this.checkValid();
		EventListenerChain chain = this.occorAt.collectEvent(event.getClass(),
				this.getInvokeeQueryMode());
		if (chain != null) {
			return new AsyncEvent(this.session, this.occorAt, chain, event);
		} else {
			return emptyEventHandle;
		}
	}

	/**
	 * @return 返回在独立事务情况下每步是否发生异常
	 */
	@SuppressWarnings("unchecked")
	final boolean processEvents(EventListenerChain chain, Event event,
			boolean withinTrans) {
		final short invokeDepthSave1 = this.invokeDepth;
		final float contextProgressSave1 = this.contextProgress;
		float progressQuotietySave1 = this.beginContextInvoke();
		final SpaceNode occorAtSave1 = this.occorAt;
		boolean hasException = false;
		try {
			if (chain == null) {
				return true;
			}
			float step = 1f / chain.getChainSize();
			do {
				this.setNextStep(step);
				final ServiceInvokeeBase invokee = chain.eventListener;
				final SpaceNode occorAtSave = invokee.getService()
						.updateContextSpace(this);
				final short invokeDepthSave = this.invokeDepth;
				final float contextProgressSave = this.contextProgress;
				float progressQuotietySave = this.beginContextInvoke();
				try {
					invokee.occur(this, event);
				} catch (Throwable e) {
					this.exception = e;
					if (withinTrans) {
						progressQuotietySave = -progressQuotietySave;// 标记出过错
						throw Utils.tryThrowException(e);
					} else {
						this.catcher.catchException(e, event);
						hasException = true;
					}
				} finally {
					this.endContextInvoke(occorAtSave, contextProgressSave,
							progressQuotietySave, invokeDepthSave);
					if (!withinTrans) {
						this.resolveTrans();
					}
				}
				chain = chain.next;
			} while (chain != null);
		} catch (Throwable e) {
			progressQuotietySave1 = -progressQuotietySave1;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave1, contextProgressSave1,
					progressQuotietySave1, invokeDepthSave1);
		}
		return hasException;
	}

	public final boolean dispatch(Event event) {
		checkEvent(event);
		this.checkValid();
		EventListenerChain chain = this.occorAt.collectEvent(event.getClass(),
				this.getInvokeeQueryMode());
		if (chain == null) {
			return false;
		}
		this.processEvents(chain, event, true);
		return true;
	}

	/**
	 * 等待异步处理的全部结束
	 */
	public final void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		this.waitFor(0, one, others);
	}

	/**
	 * 异步处理简单任务
	 */
	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info) {
		if (info == null) {
			throw new NullPointerException();
		}
		return this.doAsyncHandle(task, None.NONE, info);
	}

	/**
	 * 异步处理任务
	 */
	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		if (info == null) {
			throw new NullPointerException();
		}
		return this.doAsyncHandle(task, method, info);
	}

	public final void dispose() {
		if (!this.disposed) {
			if (this.thread != Thread.currentThread()) {
				throw new UnsupportedOperationException("不允许销毁其他现成创建的上下文");
			}
			this.depth = 0;
			this.invokeDepth = 0;
			try {
				this.resolveTrans();
			} catch (Throwable e) {
				// 忽略
			}
			try {
				this.disposeDBAdapters();
			} catch (Throwable e) {
				// 忽略
			}
			try {
				this.disposeModelScriptContexts();
			} catch (Throwable e) {
				// 忽略
			}
			this.disposed = true;
			final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = this.session.application.contextLocal;
			ContextImpl<?, ?, ?> top = contextLocal.get();
			if (top == this) {
				contextLocal.set(this.upperInThread);
			} else {
				ContextImpl<?, ?, ?> upper = top.upperInThread;
				for (;;) {
					if (upper == null) {
						break;
					} else if (upper == this) {
						top.upperInThread = this.upperInThread;
						break;
					} else {
						top = upper;
					}
				}
			}
			this.upperInThread = null;
			this.session.contextDisposed(this);
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	// //////////////////以下是内部方法/////////////////////////////////////////////
	// ////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	/**
	 * 会话当前所处的深度，包括调用与过程造成的深度增加
	 */
	private short depth;

	/**
	 * 会话当前所处的深度，包括调用与过程造成的深度增加
	 */
	final short getDepth() {
		return this.depth;
	}

	/**
	 * 当前调用深度
	 */
	private short invokeDepth;

	/**
	 * 处理中抛出的异常
	 */
	private Throwable exception;

	final <TResult, TKey1, TKey2, TKey3> TResult serviceProvideResult(
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultProvider == null) {
			throw new NullPointerException();
		}
		final SpaceNode occorAtSave = resultProvider.getService()
				.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			TResult result;
			if (key1 == null) {
				result = resultProvider.provide(this);
			} else if (key2 == null) {
				result = resultProvider.provide(this, key1);
			} else if (key3 == null) {
				result = resultProvider.provide(this, key1, key2);
			} else {
				result = resultProvider.provide(this, key1, key2, key3);
			}
			if (this.exception != null) {
				throw this.exception;
			}
			return result;
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	/**
	 * 获取列表结果
	 */
	final <TResult, TKey1, TKey2, TKey3> void serviceProvideList(
			List<TResult> resultList,
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultListProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultListProvider == null && resultList == null) {
			throw new NullPointerException();
		}
		final SpaceNode occorAtSave = resultListProvider.getService()
				.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			if (key1 == null) {
				resultListProvider.provide(this, resultList);
			} else if (key2 == null) {
				resultListProvider.provide(this, key1, resultList);
			} else if (key3 == null) {
				resultListProvider.provide(this, key1, key2, resultList);
			} else {
				resultListProvider.provide(this, key1, key2, key3, resultList);
			}
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	public final <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs) {
		return this.occorAt.newObjectInNode(clazz, this, aditionalArgs);
	}

	public final <TDeclarator extends DeclaratorBase> TDeclarator resolveDeclarator(
			Class<TDeclarator> declaratorClass, Object... aditionalArgs) {
		if (declaratorClass == null) {
			throw new NullPointerException();
		}
		final TDeclarator declarator;
		synchronized (DeclaratorBase.class) {
			DeclaratorBase.newInstanceByCore = this;
			try {
				declarator = this.occorAt.newObjectInNode(declaratorClass,
						this, aditionalArgs);
			} finally {
				DeclaratorBase.newInstanceByCore = null;
			}
		}
		declarator.tryDeclareUseRef(this);
		if (TableDeclarator.class.isAssignableFrom(declaratorClass)) {
			try {
				this.getDBAdapter().syncTable(
						((TableDefineImpl) ((TableDeclarator) declarator)
								.getDefine()));
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
		return declarator;
	}

	@SuppressWarnings("unchecked")
	final void disposeService(ServiceBase service) throws Throwable {
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			service.dispose(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	final void initService(ServiceBase service) throws Throwable {
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			try {
				service.resolveNativeDeclarator(this, this);
			} catch (Throwable e) {
				this.exception = e;
				throw e;
			} finally {
				this.resolveTrans();
			}
			service.init(this);
			if (this.exception != null) {
				throw this.exception;
			}
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	final void initResources(ResourceGroup group) {
		if (group.resourceService.needInitResource) {
			final SpaceNode occorAtSave = group.resourceService
					.updateContextSpace(this);
			final short invokeDepthSave = this.invokeDepth;
			final float contextProgressSave = this.contextProgress;
			float progressQuotietySave = this.beginContextInvoke();
			try {
				group.resourceService.initResources(this,
						new ResourceGroupAdapter(this, group));
				if (this.exception != null) {
					throw this.exception;
				}
			} catch (Throwable e) {
				progressQuotietySave = -progressQuotietySave;
				throw Utils.tryThrowException(e);
			} finally {
				this.endContextInvoke(occorAtSave, contextProgressSave,
						progressQuotietySave, invokeDepthSave);
			}
		}
	}

	@SuppressWarnings("unchecked")
	final boolean startMoniter(PerformanceValueCollectorImpl collector)
			throws Throwable {
		final ServiceBase<?> service = collector.provider.getService();
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			return collector.provider.startMonitor(this, collector);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	final void stopMoniter(PerformanceValueCollectorImpl collector)
			throws Throwable {
		final ServiceBase<?> service = collector.provider.getService();
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			collector.provider.stopMonitor(this, collector);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	final void updateMoniter(PerformanceValueCollectorImpl collector)
			throws Throwable {
		final ServiceBase<?> service = collector.provider.getService();
		final SpaceNode occorAtSave = service.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			collector.provider.update(this, collector);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;
			throw this.exception = e;
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
			this.resolveTrans();
		}
	}

	@SuppressWarnings("unchecked")
	public final IInternalUser changeLoginUser(User user) {
		switch (this.session.kind) {
		case SYSTEM:
			throw new UnsupportedOperationException("系统会话不支持切换用户");
		case NORMAL:
			if (this.kind != ContextKind.SITUATION) {
				throw new UnsupportedOperationException("普通会话只支持在主线程中切换用户");
			}
			break;
		}
		if (user instanceof CoreAuthUserEntity) {
			user = new UserProxy((ResourceItem<?, CoreAuthUserEntity, ?>) this
					.getResourceToken(User.class, ((CoreAuthUserEntity) user)
							.getID()));

		} else if (user.getState() == ActorState.DISABLE) {
			if (user == User.debugger) {
				throw new IllegalStateException("调试账号已被禁用");
			} else {
				throw new IllegalStateException("账号[" + user.getName()
						+ "]已被禁用");
			}
		}
		this.currentOrgID = null;
		this.resetACLCache();
		return this.session.changeUser(user);
	}

	final long createTime;
	/**
	 * 会话
	 */
	final SessionImpl session;
	// 创建请求的线程
	final Thread thread;
	// 当前被调用者
	SpaceNode occorAt;
	// 当前资源管理器
	ResourceServiceBase<TFacadeM, TImplM, TKeysHolderM> occorAtResourceService;

	// 异常收集器
	final ExceptionCatcher catcher;

	public static final void internalWaitFor(long timeout, AsyncHandle one,
			AsyncHandle[] others) throws InterruptedException {
		if (timeout > 0) {
			long endT = System.currentTimeMillis() + timeout;
			if (one instanceof Waitable) {
				((Waitable) one).waitStop(timeout);
			}
			if (others != null) {
				for (AsyncHandle other : others) {
					if (other instanceof Waitable
							&& (timeout = endT - System.currentTimeMillis()) > 0) {
						((Waitable) other).waitStop(timeout);
					}
				}
			}
		} else {
			if (one instanceof Waitable) {
				((Waitable) one).waitStop(0);
			}
			if (others != null) {
				for (AsyncHandle other : others) {
					if (other instanceof Waitable) {
						((Waitable) other).waitStop(0);
					}
				}
			}
		}
	}

	/**
	 * 等待异步处理的全部结束
	 * 
	 * @param nanosTimeout
	 *            超时纳秒数，0代表永远不超时
	 */
	public final void waitFor(long timeout, AsyncHandle one,
			AsyncHandle... others) throws InterruptedException {
		this.checkValid();
		internalWaitFor(timeout, one, others);
	}

	// final LocalCluster localCluster;

	final ContextKind kind;

	public final ContextKind getKind() {
		return this.kind;
	}

	/**
	 * 上下文栈
	 */
	private ContextImpl<?, ?, ?> upperInThread;

	ContextImpl(SessionImpl session, SpaceNode occorAt, ContextKind kind,
			TransactionImpl transaction) throws SessionDisposedException,
			SituationReentrantException {
		if (session == null) {
			throw new NullArgumentException("session");
		}
		if (occorAt == null) {
			throw new NullArgumentException("occorAt");
		}
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		occorAt.site.state.checkContextKind(session.kind, kind);
		this.session = session;
		occorAt.updateContextSpace(this);
		final ApplicationImpl application = session.application;
		this.catcher = application.catcher;
		this.thread = Thread.currentThread();
		this.createTime = System.currentTimeMillis();
		this.progressQuotiety = 1f;
		switch (this.kind = kind) {
		case TRANSIENT:
		case INITER:
			this.contextProgressNextStep = 1f;
			break;
		}
		this.currentOrgID = session.currentOrgID;
		this.transaction = transaction;
		session.contextCreated(this);
		final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = application.contextLocal;
		this.upperInThread = contextLocal.get();
		contextLocal.set(this);
		transaction.bindContext(this);
	}

	// ////////////////////////
	// ////进度相关
	// ////////////////////////
	float progress;
	// 进度系数
	private float progressQuotiety;
	// 当前上下文下一步完成的位置
	private float contextProgressNextStep;
	// 当前上下文的进度
	private float contextProgress;
	// 正在被外界取消
	private volatile boolean canceling;
	// 正在执行的语句，用于取消上下文时使用。
	volatile Statement processingStatement;
	@SuppressWarnings("unchecked")
	private static final AtomicReferenceFieldUpdater<ContextImpl, Statement> processingStatementSetter = AtomicReferenceFieldUpdater
			.newUpdater(ContextImpl.class, Statement.class,
					"processingStatement");

	/**
	 * 外部线程通知内部终止
	 */
	final void cancel() {
		this.canceling = true;
		if (this.thread != Thread.currentThread()) {
			// 终止正在处理的数据库请求
			final Statement processingStatement = processingStatementSetter
					.getAndSet(this, null);
			if (processingStatement != null) {
				try {
					processingStatement.cancel();
				} catch (Throwable e) {
					this.catcher.catchException(e, this);
				}
			}
			// 终止其他等待，诸如异步等待，网络连接等待等
			try {
				this.thread.interrupt();
			} catch (Throwable e) {
				this.catcher.catchException(e, this);
			}
		}
	}

	private boolean disposed;

	/**
	 * 进入子过程
	 * 
	 * @return progressQuotiety
	 */
	private final float enterFrame() {
		float progressQuotietySave = this.progressQuotiety;
		this.progressQuotiety *= this.contextProgressNextStep;
		this.contextProgress = 0;
		this.contextProgressNextStep = 0;
		this.depth++;
		return progressQuotietySave;
	}

	/**
	 * 进入子调用
	 * 
	 * @return progressQuotiety
	 */
	private final float beginContextInvoke() {
		float f = this.enterFrame();
		this.invokeDepth = this.depth;
		return f;
	}

	private final void endContextInvoke(SpaceNode occorAtSave,
			float contextProgressSave, float progressQuotietySave,
			short invokeDepthSave) {
		try {
			while (this.depth > this.invokeDepth) {
				this.endProcess();
			}
		} finally {
			this.depth = this.invokeDepth;// 防止上面的循环中出现异常
			this.leaveFrame(contextProgressSave, progressQuotietySave);
			occorAtSave.updateContextSpace(this);
			this.invokeDepth = invokeDepthSave;
			if (invokeDepthSave == 0
					&& this.exception instanceof AbortException) {
				// 在最外层针对AbortException回滚事务
				this.resolveTrans();
			}
		}
	}

	/**
	 * 离开子过程
	 * 
	 * @param occorAtSave
	 *            如果为空，表示是process否则为invoke
	 */
	private final void leaveFrame(float contextProgressSave,
			float progressQuotietySave) {
		final short depth = this.depth;
		// XXX !
		// if (this.resHandles != null && !this.resHandles.isEmpty() &&
		// false) {
		// ResourceHandleImpl<?, ?, ?> handle;
		// for (Iterator<ResourceHandleImpl<?, ?, ?>> i = this.resHandles
		// .allAcquirers(); i.hasNext();) {
		// handle = i.next();
		// if (handle.depth >= this.depth) {
		// i.remove();
		// handle.dispose(this.catcher);
		// }
		// }
		// }
		DBAdapterImpl dbAdapter = this.lastDBAdapter;
		final DBAdapterImpl lastDBAdapter = dbAdapter;
		if (dbAdapter != null) {
			do {
				dbAdapter.unuseOldAccessor(depth);
				dbAdapter = dbAdapter.nextInContext;
			} while (dbAdapter != lastDBAdapter);
		}
		if (progressQuotietySave > 0f) {// 成功返回
			float p = this.progress + (1 - this.contextProgress)
					* this.progressQuotiety;
			this.progress = p <= 1 ? p : 0.9999f;
			this.contextProgress = contextProgressSave + this.progressQuotiety
					/ progressQuotietySave;
			this.contextProgressNextStep = 0f;
		} else if (progressQuotietySave < 0f) {// 有错误
			progressQuotietySave = -progressQuotietySave;
			float contextProgressStep = this.progressQuotiety
					/ progressQuotietySave;
			// 出错后的部分放入下一步中，如果用户越过异常则进度会记入下一次
			this.contextProgressNextStep = contextProgressStep
					* (1 - this.contextProgress);
			// 出错后的调整上下文进度到出错位置
			this.contextProgress = contextProgressSave + contextProgressStep
					* this.contextProgress;
		} else {// 0子进度为零
			this.contextProgress = contextProgressSave;
			this.contextProgressNextStep = 0f;
		}
		this.progressQuotiety = progressQuotietySave;
		this.depth--;
	}

	// //////////////////////////////////////////
	// ///// 接口实现
	// //////////////////////////////////////////

	public final <TFacade> ResourceHandle<TFacade> lockResourceS(
			ResourceToken<TFacade> resourceToken) {
		this.checkValid();
		return this.transaction.lockResource(ResourceDemandFor.READ,
				resourceToken);
	}

	public final <TFacade> ResourceHandle<TFacade> lockResourceU(
			ResourceToken<TFacade> resourceToken) {
		this.checkValid();
		return this.transaction.lockResource(
				ResourceDemandFor.READ_THEN_MODIFY, resourceToken);
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源，如果返回空，查询结果。允许返回空（null）值。
	// //////////////////////////////////////////////////////////////////////
	// ////
	public final <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalFind(null, facadeClass, null, null, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFind(null, facadeClass, key, null, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFind(null, facadeClass, key1, key2, null, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFind(null, facadeClass, key1, key2, key3, null);
	}

	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| keys[0] == null) {
			throw new NullPointerException();
		}
		return this.internalFind(null, facadeClass, key1, key2, key3, keys);
	}

	@SuppressWarnings("unchecked")
	final <TFacade> TFacade internalFind(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object[] otherKeys) {
		this.checkValid();
		ResourceIndexInfo rii = this.findResourceIndexInfo(facadeClass, key1,
				key2, key3, otherKeys);
		if (rii != null) {
			ResourceItem<TFacade, ?, ?> resourceItem;
			try {
				resourceItem = this.internalFindResource(operation, rii,
						TransactionImpl.FIND_RESOURCE, null, None.NONE, key1,
						key2, key3, otherKeys);
			} catch (NoAccessAuthorityException e) {
				// 找到资源，但是没有访问权限
				return null;
			}
			if (resourceItem != null) {
				return resourceItem.getResource(this.transaction);
			} else if (operation != null) {
				// operation不为空时，resourceItem为空，在此直接返回
				return null;
			}
			// 如果没有查到，允许下面有机会提供数据
			//
			// else {
			// return null;
			// }
		}
		if (otherKeys == null || otherKeys.length == 0) {
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> provider = this.occorAt
					.findResultProvider(facadeClass, key1, key2, key3, mode);
			if (provider != null) {
				return this.serviceProvideResult(provider, key1, key2, key3);
			} else {
				return this.occorAt.tryFindResult(facadeClass, key1, key2,
						key3, mode);
			}
		} else {
			return null;
			//
			// 该方法允许返回空，那些不允许返回空的方法调用此方法时已对空结果做了处理
			//
			// throw
			// ServiceInvokeeBase.noResourceException(facadeClass,
			// key1,
			// key2, key3, otherKeys);
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源，如果返回空，查询结果。
	// 不允许返回空（null）值，如果最终结果为空，抛出MissingObjectException异常。
	// //////////////////////////////////////////////////////////////////////
	// ////
	public final <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(null, facadeClass, null, null, null,
				null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass
					+ "]类的无键（单实例）对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(null, facadeClass, key, null, null,
				null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(null, facadeClass, key1, key2, null,
				null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(null, facadeClass, key1, key2, key3,
				null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(null, facadeClass, key1, key2, key3,
				otherKeys);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + ", ...]对象");
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源记号， 允许返回空（null）值。
	// //////////////////////////////////////////////////////////////////////
	// ////

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceQuerier#findResourceToken(java
	 * .lang .Class, java.lang.Object, java.lang.Object, java.lang.Object,
	 * java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalFindResource(null, TransactionImpl.FIND_RESOURCE,
				null, facadeClass, None.NONE, key1, key2, key3, otherKeys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceQuerier#findResourceToken(java
	 * .lang .Class, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalFindResource(null, TransactionImpl.FIND_RESOURCE,
				null, facadeClass, None.NONE, key1, key2, key3, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceQuerier#findResourceToken(java
	 * .lang .Class, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalFindResource(null, TransactionImpl.FIND_RESOURCE,
				null, facadeClass, None.NONE, key1, key2, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceQuerier#findResourceToken(java
	 * .lang .Class, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceItem<TFacade, ?, ?> findResourceToken(
			Class<TFacade> facadeClass, Object key) {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFindResource(null, TransactionImpl.FIND_RESOURCE,
				null, facadeClass, None.NONE, key, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceQuerier#findResourceToken(java
	 * .lang .Class)
	 */
	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalFindResource(null, TransactionImpl.FIND_RESOURCE,
				null, facadeClass, None.NONE, null, null, null, null);
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源记号， 不允许返回空（null）值。
	// 如果最终结果为空，抛出MissingObjectException异常。
	// //////////////////////////////////////////////////////////////////////
	// ////

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result = this.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass, None.NONE,
				key1, key2, key3, otherKeys);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + ", ...]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3) {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}

		ResourceToken<TFacade> result = this.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass, None.NONE,
				key1, key2, key3, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key1, Object key2) {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}

		ResourceToken<TFacade> result = this.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass, None.NONE,
				key1, key2, null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass, Object key) {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}

		ResourceToken<TFacade> result = this.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass, None.NONE,
				key, null, null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Class<TFacade> facadeClass) {
		if (facadeClass == null) {
			throw new NullPointerException();
		}

		ResourceToken<TFacade> result = this.internalFindResource(null,
				TransactionImpl.FIND_RESOURCE, null, facadeClass, None.NONE,
				null, null, null, null);
		;
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass
					+ "]类的无键（单实例）资源");
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询引用资源列表，不允许返回空（null）值。
	// 如果最终结果为空，则返回空的列表。
	// //////////////////////////////////////////////////////////////////////
	// ////

	@SuppressWarnings("unchecked")
	private final <TFacade, THolderFacade> List<TFacade> internalGetResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		this.checkValid();
		DnaArrayList<TFacade> list = new DnaArrayList<TFacade>();
		((ResourceItem<THolderFacade, ?, ?>) holderToken).fillReferences(
				operation, facadeClass, list, this.transaction, filter,
				sortComparator);
		return list;
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken) {
		if (facadeClass == null || holderToken == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(null, facadeClass,
				holderToken, null, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (facadeClass == null || holderToken == null || filter == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(null, facadeClass,
				holderToken, filter, sortComparator);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		if (facadeClass == null || holderToken == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(null, facadeClass,
				holderToken, filter, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		if (facadeClass == null || holderToken == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(null, facadeClass,
				holderToken, null, sortComparator);

	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// 查询资源列表，如果返回空，查询结果列表。
	// 不允许返回空（null）值，如果最终结果为空，则返回空的列表。
	// //////////////////////////////////////////////////////////////////////
	// ////

	/*
	 * 填充资源列表，返回的数字表示查询出的结果集在过滤前的元素个数。-1表示没有定义相应（类别）的资源。
	 */
	@SuppressWarnings("unchecked")
	final <TFacade> int tryFillResourceList(
			Operation<? super TFacade> operation,
			final DnaArrayList<TFacade> list, Class<TFacade> facadeClass,
			Object category, Filter<? super TFacade> filter,
			Comparator<? super TFacade> comparator, Object key1, Object key2,
			Object key3, Object[] otherKeys) {
		if (list == null) {
			throw new NullPointerException();
		}
		ResourceGroup group;
		ResourceIndex index;
		ResourceServiceBase resourceService;
		if (key1 == null) {
			resourceService = this.occorAtResourceService;
			if (resourceService == null
					|| facadeClass != resourceService.facadeClass) {
				resourceService = this.occorAt.findResourceService(facadeClass,
						this.getInvokeeQueryMode());
				if (resourceService == null) {
					return -1;
				}
			}
			if ((group = resourceService.ensureResourceGroup(category, this)) == null) {
				return -1;
			}
		} else {
			ResourceIndexInfo rii = this.findSubParentResourceIndexInfo(null,
					facadeClass, key1, key2, key3, otherKeys);
			if (rii == null) {
				return -1;
			}
			ResourceItem parent = this.internalFindResource(null, rii,
					TransactionImpl.FIND_RESOURCE, null, category, key1, key2,
					key3, otherKeys);
			if (parent == null) {
				return 0;
			}
			group = parent.getSubResourceGroup(facadeClass, this);
			resourceService = group.resourceService;
		}
		if (operation != null) {
			// TODO 是否要支持进行默认排序
			group.lockFillList(operation, list, this.transaction);
			if (list.size() != 0) {
				return resourceService.processList(list, filter, comparator,
						false);
			}
		} else {
			index = group.getResourceIndex((byte) 0);
			if (!index.isEmpty()) {
				final boolean hasDefaultSorted = index.lockFillResources(list,
						this.transaction);
				return resourceService.processList(list, filter, comparator,
						hasDefaultSorted);
			}
		}
		return 0;
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass)
			throws UnsupportedOperationException {
		if (resultClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, null, null, null,
				null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key) throws UnsupportedOperationException {
		if (resultClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, null, key, null,
				null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, null, key1, key2,
				null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, null, key1, key2,
				key3, null);
	}

	final <TFacade> List<TFacade> internalGetList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		this.checkValid();
		DnaArrayList<TFacade> list = new DnaArrayList<TFacade>();
		int ls;
		fillList: {
			if ((ls = this.tryFillResourceList(operation, list, resultClass,
					None.NONE, filter, sortComparator, key1, key2, key3,
					otherKeys)) > 0
					|| operation != null) {
				return list; // may be empty.
			}
			// list 现在仍然为空

			if (otherKeys != null && otherKeys.length != 0) {
				throw ServiceInvokeeBase.noResourceListException(resultClass,
						key1, key2, key3, otherKeys);
			}
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> listProvider = this.occorAt
					.findResultListProvider(resultClass, key1, key2, key3, mode);
			if (listProvider != null) {
				this.serviceProvideList(list, listProvider, key1, key2, key3);
				if ((ls = list.size()) > 0) {
					break fillList;
				}
			}
			if (this.occorAt.tryFillList(list, resultClass, key1, key2, key3,
					mode)) {
				ls = list.size();
			} else if (ls < 0) {
				throw ServiceInvokeeBase.noListProviderException(resultClass,
						key1, key2, key3);
			}
		}
		if (ls > 0) {
			if (filter != null) {
				int acceptedCount = 0;
				TFacade item;
				for (int i = 0; i < ls; i++) {
					item = list.get(i);
					if (filter.accept(item)) {
						if (acceptedCount != i) {
							list.set(acceptedCount, item);
						}
						acceptedCount++;
					}
				}
				if (acceptedCount == 0 && ls > 0) {
					list.clear();
					ls = 0;
				} else {
					list.removeTail(acceptedCount);
					ls = acceptedCount;
				}
			}
			if (ls > 0 && sortComparator != null) {
				SortUtil.sort(list, sortComparator);
			}
		}
		return list;
	}

	private final <TFacade> List<TFacade> internalGetResourceList(
			Class<TFacade> resultClass, Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys)
			throws UnsupportedOperationException {
		this.checkValid();
		DnaArrayList<TFacade> list = new DnaArrayList<TFacade>();
		if (this.tryFillResourceList(null, list, resultClass, None.NONE,
				filter, sortComparator, key1, key2, key3, otherKeys) >= 0) {
			return list;
		} else {
			throw ServiceInvokeeBase.noResourceListException(resultClass, key1,
					key2, key3, otherKeys);
		}
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, null, null, key1,
				key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, filter, null, key1,
				key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, null, key1,
				key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, null, key1,
				key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (resultClass == null || key == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, null, key, null,
				null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (resultClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, null, null,
				null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, sortComparator,
				key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, sortComparator,
				key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (resultClass == null || key == null || filter == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, sortComparator,
				key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (resultClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, sortComparator,
				null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| sortComparator == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, null, sortComparator,
				key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, sortComparator,
				key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (resultClass == null || key1 == null || key2 == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, sortComparator,
				key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (resultClass == null || key == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, sortComparator,
				key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (resultClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, null, sortComparator,
				null, null, null, null);
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	@SuppressWarnings("unchecked")
	private final <TResult, TKey1, TKey2, TKey3> AsyncResultImpl<TResult, TKey1, TKey2, TKey3> internalAsyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkValid();
		ServiceInvokeeBase resultProvider = this.occorAt.findResultProvider(
				resultClass, key1, key2, key3, this.getInvokeeQueryMode());
		if (resultProvider == null) {
			throw ServiceInvokeeBase.noResultProviderException(resultClass,
					key1, key2, key3);
		}
		return new AsyncResultImpl<TResult, TKey1, TKey2, TKey3>(this.session,
				this.occorAt, resultClass, key1, key2, key3, resultProvider);
	}

	public final <TResult> AsyncResult<TResult> asyncGet(
			Class<TResult> resultClass) {
		if (resultClass == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGet(resultClass, null, null, null);
	}

	public final <TResult, TKey> OneKeyOverlappedResult<TResult, TKey> asyncGet(
			Class<TResult> resultClass, TKey key) {
		if (resultClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGet(resultClass, key, null, null);
	}

	public final <TResult, TKey1, TKey2> TwoKeyOverlappedResult<TResult, TKey1, TKey2> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		if (resultClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGet(resultClass, key1, key2, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGet(resultClass, key1, key2, key3);
	}

	@SuppressWarnings("unchecked")
	private final <TResult, TKey1, TKey2, TKey3> AsyncResultListImpl<TResult, TKey1, TKey2, TKey3> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkValid();
		ServiceInvokeeBase resultListProvider = this.occorAt
				.findResultListProvider(resultClass, key1, key2, key3, this
						.getInvokeeQueryMode());
		if (resultListProvider == null) {
			throw ServiceInvokeeBase.noListProviderException(resultClass, key1,
					key2, key3);
		}
		return new AsyncResultListImpl<TResult, TKey1, TKey2, TKey3>(
				this.session, this.occorAt, resultClass, key1, key2, key3,
				resultListProvider);

	}

	public final <TResult> AsyncResultList<TResult> asyncGetList(
			Class<TResult> resultClass) {
		if (resultClass == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGetList(resultClass, null, null, null);
	}

	public final <TResult, TKey1> OneKeyOverlappedResultList<TResult, TKey1> asyncGetList(
			Class<TResult> resultClass, TKey1 key) {
		if (resultClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGetList(resultClass, key, null, null);
	}

	public final <TResult, TKey1, TKey2> TwoKeyOverlappedResultList<TResult, TKey1, TKey2> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		if (resultClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGetList(resultClass, key1, key2, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		if (resultClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalAsyncGetList(resultClass, key1, key2, key3);
	}

	// ======================================================================
	// ===
	// Methods from ResourcesModifier
	// ----------------------------------------------------------------------
	// ---

	public final void invalidResource() throws DeadLockException {
		this.internalInvalidResource(null, null, null, null, null);
	}

	public final <TKey> void invalidResource(TKey key) throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key, null, null, null);
	}

	public final <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, null, null);
	}

	public final <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1,
			TKey2 key2, TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, key3, null);
	}

	public final <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1,
			TKey2 key2, TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(null, key1, key2, key3, keys);
	}

	private final <TKey1, TKey2, TKey3> void internalInvalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		this.internalFindResource(operation, TransactionImpl.INVALID_RESOURCE,
				ResourceDemandFor.INVALID,
				this.occorAtResourceService.facadeClass, None.NONE, key1, key2,
				key3, keys);
	}

	public final TImplM modifyResource() throws DeadLockException {
		return this.internalModifyResource(null, null, null, null, null);
	}

	public final <TKey> TImplM modifyResource(TKey key)
			throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key, null, null, null);
	}

	public final <TKey1, TKey2> TImplM modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, null, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1,
			TKey2 key2, TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, key3, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM modifyResource(TKey1 key1,
			TKey2 key2, TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(null, key1, key2, key3, keys);
	}

	@SuppressWarnings("unchecked")
	private final <TKey1, TKey2, TKey3> TImplM internalModifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		ResourceHandleImpl<?, TImplM, ?> handle = this.internalFindResource(
				operation, TransactionImpl.MODIFY_RESOURCE,
				ResourceDemandFor.MODIFY,
				this.occorAtResourceService.facadeClass, None.NONE, key1, key2,
				key3, keys);
		if (handle == null || handle.res == null) {
			throw new MissingObjectException(ServiceInvokeeBase
					.noResourceException(
							this.occorAtResourceService.facadeClass, key1,
							key2, key3, keys).getMessage());
		}
		return this.internalModifyLockedResource(handle);
	}

	@SuppressWarnings("unchecked")
	final TImplM internalModifyLockedResource(
			ResourceHandleImpl<?, TImplM, ?> handle) {
		ResourceItem<?, TImplM, ?> item = handle.res;
		Assertion.ASSERT(item.state != State.EMPTY
				&& item.state != State.DISPOSED, "不应出现的状态" + item.state);
		if (item.state == State.REMOVED) {
			throw new MissingObjectException("资源已经被删除了");
		}
		item.ensureTempValues();
		if (item.state == State.RESOLVED || item.state == State.FILLED) {
			item.tempValues.copyForModification = (TImplM) new OBJAContext()
					.assign(item.impl, null,
							item.group.resourceService.implStruct);
		} else if (item.state == State.MODIFIED) {
			item.tempValues.copyForModification = (TImplM) new OBJAContext()
					.assign(item.tempValues.newImpl, null, null);
		}
		Assertion.ASSERT(item.tempValues.copyForModification != null);
		return item.tempValues.copyForModification;
	}

	public final void abort() throws AbortException {
		this.checkValid();
		AbortException ae;
		if (this.exception != null) {
			this.exception = ae = new AbortException(this.exception);
		} else {
			this.exception = ae = new AbortException();
		}
		if (this.depth == 0) {// 在最外层回滚事务
			this.resolveTrans();
		}
		throw ae;
	}

	/**
	 * 克隆资源
	 */
	@SuppressWarnings("unchecked")
	final TImplM internalCloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token, TImplM dest, Object category) {
		if (token == null) {
			throw new NullPointerException();
		} else if (token instanceof ResourceItem) {
			ResourceItem<TFacadeM, TImplM, TKeysHolderM> resourceItem = (ResourceItem<TFacadeM, TImplM, TKeysHolderM>) token;
			if (operation == null
					|| resourceItem.validateAuthority(operation, this, true)) {
				if (!resourceItem.group.category.equals(category)) {
					throw new IllegalArgumentException("资源类别有误");
				}
				OBJAContext obja = new OBJAContext();
				final ResourceServiceBase resourceService = resourceItem.group.resourceService;
				return (TImplM) obja.assign(resourceItem.impl, dest,
						resourceService.implStruct);
			} else {
				throw new NoAccessAuthorityException((resourceItem.impl
						.getClass().toString() + "()"), operation);
			}
		} else if (token instanceof ResourceTokenMissing) {
			return null;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 克隆资源
	 * 
	 * @param tryReuse
	 *            尝试被重用的实例（减少对象创建成本）
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token,
			TImplM tryReuse) {
		return this.internalCloneResource(null, token, tryReuse, null);
	}

	/**
	 * 克隆资源
	 */
	public final TImplM cloneResource(ResourceToken<TFacadeM> token) {
		return this.internalCloneResource(null, token, null, null);
	}

	public final void postModifiedResource(TImplM modifiedResource) {
		this.transaction.postModifiedResource(modifiedResource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceContext#removeResource()
	 */
	public final TImplM removeResource() throws DeadLockException {
		return this.internalRemoveResource(null, null, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#removeResource(java.lang.
	 * Object)
	 */
	public final <TKey> TImplM removeResource(TKey key)
			throws DeadLockException {
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#removeResource(java.lang.
	 * Object, java.lang.Object)
	 */
	public final <TKey1, TKey2> TImplM removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#removeResource(java.lang.
	 * Object, java.lang.Object, java.lang.Object)
	 */
	public final <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1,
			TKey2 key2, TKey3 key3) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, key3, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#removeResource(java.lang.
	 * Object, java.lang.Object, java.lang.Object, java.lang.Object[])
	 */
	public final <TKey1, TKey2, TKey3> TImplM removeResource(TKey1 key1,
			TKey2 key2, TKey3 key3, Object... keys) throws DeadLockException {
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(null, key1, key2, key3, keys);
	}

	@SuppressWarnings("unchecked")
	private final <TKey1, TKey2, TKey3> TImplM internalRemoveResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] keys) throws DeadLockException {
		TImplM resource = (TImplM) this.internalFindResource(operation,
				TransactionImpl.REMOVE_RESOURCE, null,
				this.occorAtResourceService.facadeClass, None.NONE, key1, key2,
				key3, keys);
		if (resource != null && operation != null) {
			// FIXME 删除相关资源的授权信息
			throw new UnsupportedOperationException();
		}
		return resource;
	}

	@SuppressWarnings("unchecked")
	public final ResourceToken putResource(TImplM resource) {
		if (resource == null) {
			throw new NullPointerException();
		}
		return this.internalPutResource(None.NONE, null, resource,
				(TKeysHolderM) resource, WhenExists.REPLACE);
	}

	public final ResourceItem<TFacadeM, TImplM, TKeysHolderM> putResource(
			TImplM resource, TKeysHolderM keys) {
		if (resource == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalPutResource(None.NONE, null, resource, keys,
				WhenExists.REPLACE);
	}

	public final ResourceItem<TFacadeM, TImplM, TKeysHolderM> putResource(
			TImplM resource, TKeysHolderM keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		return this
				.internalPutResource(None.NONE, null, resource, keys, policy);
	}

	/*
	 * @SuppressWarnings("unchecked") final ResourceItem<TFacadeM, TImplM,
	 * TKeysHolderM> internalPutResource( Object category, TImplM resource,
	 * TKeysHolderM keys, WhenExists policy) { this.checkValid();
	 * ResourceServiceBase<?, ?, ?> resourceService =
	 * this.occorAtResourceService; if (resourceService == null ||
	 * !resourceService.implClass.isInstance(resource)) { throw new
	 * IllegalStateException(); } ResourceGroup group =
	 * resourceService.ensureResourceGroup(category, this); if (group == null) {
	 * throw new IllegalStateException(); } return group.putResource(this,
	 * resource, keys, policy); }
	 */

	@SuppressWarnings("unchecked")
	final ResourceItem<TFacadeM, TImplM, TKeysHolderM> internalPutResource(
			Object category, ResourceToken<TFacadeM> treeParent,
			TImplM resource, TKeysHolderM keys, WhenExists policy) {
		this.checkValid();
		ResourceServiceBase<?, ?, ?> resourceService = this.occorAtResourceService;
		if (resourceService == null
				|| !resourceService.implClass.isInstance(resource)) {
			throw new IllegalStateException();
		}
		ResourceGroup group = resourceService.ensureResourceGroup(category,
				this);
		if (group == null) {
			throw new IllegalStateException();
		}
		return group.putResource(this.transaction,
				(ResourceItem<TFacadeM, TImplM, TKeysHolderM>) treeParent,
				resource, keys, policy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourcePutter#putResource(org.eclipse.jt.
	 * core.resource.ResourceToken, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public final ResourceItem<TFacadeM, TImplM, TKeysHolderM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource) {
		if (resource == null) {
			throw new NullPointerException();
		}
		return this.internalPutResource(None.NONE, treeParent, resource,
				(TKeysHolderM) resource, WhenExists.REPLACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourcePutter#putResource(org.eclipse.jt.
	 * core.resource.ResourceToken, java.lang.Object, java.lang.Object)
	 */
	public final ResourceItem<TFacadeM, TImplM, TKeysHolderM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys) {
		if (resource == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalPutResource(None.NONE, treeParent, resource, keys,
				WhenExists.REPLACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#putResource(org.eclipse.jt
	 * .core.resource.ResourceToken, java.lang.Object, java.lang.Object,
	 * org.eclipse.jt.core.resource.ResourceService.WhenExists)
	 */
	public final ResourceItem<TFacadeM, TImplM, TKeysHolderM> putResource(
			ResourceToken<TFacadeM> treeParent, TImplM resource,
			TKeysHolderM keys, WhenExists policy) {
		if (resource == null || keys == null || policy == null) {
			throw new NullPointerException();
		}
		return this.internalPutResource(None.NONE, treeParent, resource, keys,
				policy);
	}

	@SuppressWarnings("unchecked")
	public final void putResource(ResourceToken<TFacadeM> treeParent,
			ResourceToken<TFacadeM> child) {
		if (child == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		ResourceItem<TFacadeM, TImplM, TKeysHolderM> treeParentItem = (ResourceItem<TFacadeM, TImplM, TKeysHolderM>) treeParent;
		ResourceItem<TFacadeM, TImplM, TKeysHolderM> childItem = (ResourceItem<TFacadeM, TImplM, TKeysHolderM>) child;
		childItem.group
				.putResource(this.transaction, treeParentItem, childItem);
	}

	@SuppressWarnings("unchecked")
	final void putResourceAndCommit(ResourceToken<TFacadeM> treeParent,
			ResourceToken<TFacadeM> child) {
		if (child == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		ResourceItem<TFacadeM, TImplM, TKeysHolderM> treeParentItem = (ResourceItem<TFacadeM, TImplM, TKeysHolderM>) treeParent;
		ResourceItem<TFacadeM, TImplM, TKeysHolderM> childItem = (ResourceItem<TFacadeM, TImplM, TKeysHolderM>) child;
		childItem.group.putResourceAndCommit(treeParentItem, childItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jt.core.resource.ResourceContext#putReference(org.eclipse.jt.core.resource.ResourceToken,
	 * org.eclipse.jt.core.resource.ResourceToken)
	 */
	@SuppressWarnings("unchecked")
	public final <THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		((ResourceItem<THolderFacade, ?, ?>) holder).putReference(
				this.transaction, (ResourceItem<TFacadeM, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void putResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		((ResourceItem<TFacadeM, ?, ?>) holder).putReference(this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	/**
	 * 仅供资源初始化过程使用。
	 */
	@SuppressWarnings("unchecked")
	final <THolderFacade> void putResourceReferenceAndCommit(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		((ResourceItem<THolderFacade, ?, ?>) holder)
				.putReferenceAndCommit((ResourceItem<TFacadeM, ?, ?>) reference);
	}

	/**
	 * 仅供资源初始化过程使用。
	 */
	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void putResourceReferenceAndCommitBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		((ResourceItem<TFacadeM, ?, ?>) holder)
				.putReferenceAndCommit((ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jt.core.resource.ResourceContext#removeReference(org.eclipse.jt.core.resource.ResourceToken,
	 * org.eclipse.jt.core.resource.ResourceToken)
	 */
	@SuppressWarnings("unchecked")
	public final <THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<THolderFacade, ?, ?>) holder).removeReference(
				this.transaction, (ResourceItem<TFacadeM, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<TFacadeM, ?, ?>) holder).removeReference(
				this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	/**
	 * 仅供资源初始化过程使用。
	 */
	@SuppressWarnings("unchecked")
	final <THolderFacade> void removeResourceReferenceAndCommit(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<THolderFacade, ?, ?>) holder)
				.removeReferenceAndCommit((ResourceItem<TFacadeM, ?, ?>) reference);
	}

	/**
	 * 仅供资源初始化过程使用。
	 */
	@SuppressWarnings("unchecked")
	final <TReferenceFacade> void removeResourceReferenceAndCommitBy(
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<TFacadeM, ?, ?>) holder)
				.removeReferenceAndCommit((ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <THolderFacade> void clearResourceReferences(
			ResourceToken<THolderFacade> holder, boolean absolutely) {
		this.checkValid();
		((ResourceItem<THolderFacade, ?, ?>) holder).clearReferences(
				this.transaction, this.occorAtResourceService.facadeClass,
				absolutely);
	}

	// ////////////////////////////////////////////////////////////////////
	// ////////// 数据库适配器
	// ////////////////////////////////////////////////////////////////////
	final DBAdapterImpl getDBAdapter() throws SQLException {
		this.checkValid();
		return this.getDBAdapterNoCheck(this.occorAt.getDataSourceRef());
	}

	final DBAdapterImpl getDBAdapterNoCheck(DataSourceRef dataSourceRef)
			throws SQLException {
		DBAdapterImpl lastDBAdapter = this.lastDBAdapter;
		if (lastDBAdapter != null) {
			if (lastDBAdapter.dataSourceRef == dataSourceRef) {
				return lastDBAdapter;
			}

			for (DBAdapterImpl dbAdapter = lastDBAdapter.nextInContext; dbAdapter != lastDBAdapter; dbAdapter = dbAdapter.nextInContext) {
				if (dbAdapter.dataSourceRef == dataSourceRef) {
					return this.lastDBAdapter = dbAdapter;
				}
			}
			return this.lastDBAdapter = lastDBAdapter.nextInContext = new DBAdapterImpl(
					this, dataSourceRef, lastDBAdapter.nextInContext);

		} else {
			return this.lastDBAdapter = new DBAdapterImpl(this, dataSourceRef,
					null);
		}
	}

	/**
	 * 数据库适配器(环)
	 */
	private DBAdapterImpl lastDBAdapter;

	final DBLang getDBLang() throws SQLException {
		return this.occorAt.getDataSourceRef().getLang();
	}

	/**
	 * 提交或者回滚事务
	 * 
	 * @param catcher
	 */
	public final Throwable resolveTrans() {
		boolean commitTrans = this.exception == null;
		DBAdapterImpl dbAdapter = this.lastDBAdapter;
		if (dbAdapter != null) {
			do {
				try {
					dbAdapter.resolveTranse(commitTrans);
				} catch (Throwable e) {
					this.catcher.catchException(e, dbAdapter);
					commitTrans = false;
				}
				dbAdapter = dbAdapter.nextInContext;
			} while (dbAdapter != this.lastDBAdapter);
		}
		if (this.transaction.isLocal) {
			try {
				this.transaction.resetTrans(commitTrans);
			} catch (Throwable e) {
				e.printStackTrace();
				throw Utils.tryThrowException(e);
			}
		}
		Throwable e = this.exception;
		this.exception = null;
		return e;
	}

	/**
	 * 剔除ResourceItem上的冗余的IndexEntry。<br/>
	 * 这些IndexEntry的键值与所指向的资源的键值不符。
	 */
	@SuppressWarnings( { "unchecked", "unused" })
	private static void removeRedundantIndexEntry(ResourceItem resourceItem) {
		synchronized (resourceItem) {
			ResourceIndexEntry indexE = null;
			ResourceEntry entry = resourceItem.ownerEntries, last = null;
			while (entry != null) {
				if ((indexE = entry.asIndexEntry()) != null
				// FIXME 下面的判断有上层实现参与 ， 可能出现异常 ， 导致事务提交失败
						&& !indexE.keysEqual(resourceItem.keys)) {
					if (last == null) {
						resourceItem.ownerEntries = entry.nextSibling;
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

	public final void exception(Throwable exception) {
		if (exception != null) {
			this.exception = exception;
		}
	}

	private final void disposeDBAdapters() {
		final DBAdapterImpl lastDBAdapter = this.lastDBAdapter;
		if (lastDBAdapter != null) {
			DBAdapterImpl dbAdapter = lastDBAdapter;
			do {
				dbAdapter.close();
				DBAdapterImpl last = dbAdapter;
				dbAdapter = dbAdapter.nextInContext;
				last.nextInContext = null;// help GC
			} while (dbAdapter != lastDBAdapter);
			this.lastDBAdapter = null;
		}
	}

	// /////////////////////////////////////////
	// /////////// DBAdapter
	// /////////////////////////////////////////

	public final GUID newRECID() {
		return this.occorAt.site.application.newRECID();
	}

	public final long newRECVER() {
		return this.occorAt.site.application.newRECVER();
	}

	public final QueryStatementImpl newQueryStatement() {
		return new QueryStatementImpl("runtime");
	}

	public final QueryStatementDeclare newQueryStatement(
			QueryStatementDefine sample) {
		return ((QueryStatementImpl) sample).clone();
	}

	public final InsertStatementImpl newInsertStatement(TableDefine table) {
		this.checkValid();
		return new InsertStatementImpl("runtime", (TableDefineImpl) table);
	}

	public final InsertStatementImpl newInsertStatement(TableDeclarator table) {
		return this.newInsertStatement(table.getDefine());
	}

	public final DeleteStatementImpl newDeleteStatement(TableDefine table) {
		this.checkValid();
		return new DeleteStatementImpl("runtime", (TableDefineImpl) table);
	}

	public final DeleteStatementImpl newDeleteStatement(TableDeclarator table) {
		return this.newDeleteStatement(table.getDefine());
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table) {
		this.checkValid();
		return new UpdateStatementImpl("runtime", (TableDefineImpl) table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDefine table,
			String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new UpdateStatementImpl(name, (TableDefineImpl) table);
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table) {
		return this.newUpdateStatement(table.getDefine());
	}

	public final UpdateStatementImpl newUpdateStatement(TableDeclarator table,
			String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new UpdateStatementImpl(name, (TableDefineImpl) table
				.getDefine());
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass) {
		if (entityClass == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		return new MappingQueryStatementImpl("runtime", DataTypeBase
				.getStaticStructDefine(entityClass));
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			Class<?> entityClass, String name) {
		if (entityClass == null) {
			throw new NullPointerException();
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new MappingQueryStatementImpl(name, DataTypeBase
				.getStaticStructDefine(entityClass));
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			EntityTableDeclarator<?> table) {
		if (table == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		MappingQueryStatementImpl sample = (MappingQueryStatementImpl) table
				.getMappingQueryDefine();
		return new MappingQueryStatementImpl(sample.name, sample
				.getMappingTarget());
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.checkValid();
		return new MappingQueryStatementImpl("runtime",
				(StructDefineImpl) model);
	}

	public final MappingQueryStatementImpl newMappingQueryStatement(
			StructDefine model, String name) {
		if (model == null) {
			throw new NullPointerException();
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("名称");
		}
		this.checkValid();
		return new MappingQueryStatementImpl(name, (StructDefineImpl) model);
	}

	public final DBCommandProxy prepareStatement(StatementDefine statement) {
		try {
			return this.getDBAdapter().prepareStatement((IStatement) statement);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final DBCommandProxy prepareStatement(CharSequence dnaSql) {
		try {
			return this.getDBAdapter().prepareStatement(
					this.parseStatement(dnaSql));
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 解析D&ASql
	 * 
	 * @param dnaSql
	 *            D&ASql文本，可以是String/StringBuilder/StringBuffer
	 * @return 解析后的语句对象
	 */
	public final IStatement parseStatement(CharSequence dnaSql) {
		return DNASql.parseDefine(CharSequenceReader.newReader(dnaSql), this,
				IStatement.class);
	}

	public final <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz) {
		return DNASql.parseDefine(CharSequenceReader.newReader(dnaSql), this,
				clz);
	}

	public final DBCommandProxy prepareStatement(
			StatementDeclarator<?> statement) {
		return this.prepareStatement(statement.getDefine());
	}

	public final RecordSetImpl openQuery(QueryStatementDefine query,
			Object... argValues) {
		try {
			return this.getDBAdapter().openQuery(query, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final RecordSetImpl openQuery(QueryStatementDeclarator query,
			Object... argValues) {
		return this.openQuery(query.getDefine(), argValues);
	}

	public final RecordSet openQueryLimit(QueryStatementDefine query,
			long offset, long rowCount, Object... argValues) {
		try {
			return this.getDBAdapter().openQueryLimit(query, offset, rowCount,
					argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues) {
		try {
			return this.getDBAdapter().openQueryLimit(query.getDefine(),
					offset, rowCount, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues) {
		try {
			this.getDBAdapter().iterateQuery(query, action, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues) {
		try {
			this.getDBAdapter().iterateQuery(query.getDefine(), action,
					argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}

	}

	public final void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		try {
			this.getDBAdapter().iterateQueryLimit(query, offset, rowCount,
					action, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues) {
		try {
			this.getDBAdapter().iterateQueryLimit(query.getDefine(), offset,
					rowCount, action, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int rowCountOf(QueryStatementDefine query, Object... argValues) {
		try {
			return (int) this.getDBAdapter().rowCountOf(
					(QueryStatementImpl) query, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int rowCountOf(QueryStatementDeclarator query,
			Object... argValues) {
		return this.rowCountOf(query.getDefine(), argValues);
	}

	public final long rowCountOfL(QueryStatementDefine query,
			Object... argValues) {
		try {
			return this.getDBAdapter().rowCountOf((QueryStatementImpl) query,
					argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final long rowCountOfL(QueryStatementDeclarator query,
			Object... argValues) {
		return this.rowCountOfL(query.getDefine(), argValues);
	}

	public final Object executeScalar(QueryStatementDefine query,
			Object... argValues) {
		try {
			return this.getDBAdapter().executeScalar(
					(QueryStatementImpl) query, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final Object executeScalar(QueryStatementDeclarator query,
			Object... argValues) {
		return this.executeScalar(query.getDefine(), argValues);
	}

	public final int executeUpdate(ModifyStatementDefine statement,
			Object... argValues) {
		try {
			return this.getDBAdapter().executeUpdate(statement, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues) {
		return this.executeUpdate(statement.getDefine(), argValues);
	}

	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues) {
		try {
			this.getDBAdapter().executeUpdate(procedure.getDefine(), argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues) {
		try {
			this.getDBAdapter().executeUpdate(procedure, argValues);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}

	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child) {
		try {
			this.getDBAdapter().hierarchyMoveTo(hierarchy, parent, child);
		} catch (SQLException e) {
			Utils.tryThrowException(e);
		}
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child, GUID... others) {
		try {
			this.getDBAdapter().hierarchyMoveTo(hierarchy, parent, child,
					others);
		} catch (SQLException e) {
			Utils.tryThrowException(e);
		}
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			List<GUID> children) {
		try {
			this.getDBAdapter().hierarchyMoveTo(hierarchy, parent, children);
		} catch (SQLException e) {
			Utils.tryThrowException(e);
		}
	}

	public final void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			Iterable<GUID> children) {
		try {
			this.getDBAdapter().hierarchyMoveTo(hierarchy, parent, children);
		} catch (SQLException e) {
			Utils.tryThrowException(e);
		}
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm) {
		try {
			return this.getDBAdapter().newORMAccessor(
					(MappingQueryStatementImpl) orm.getDefine());
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final ORMAccessorProxy<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery) {
		try {
			return this.getDBAdapter().newORMAccessor(
					(MappingQueryStatementImpl) mappingQuery);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine query) {
		MappingQueryStatementImpl mq = (MappingQueryStatementImpl) query;
		if (mq.mapping.soClass != entityClass) {
			throw new IllegalArgumentException("实体类型与ORM定义不符");
		}
		try {
			return this.getDBAdapter().newORMAccessor(mq);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final <TEntity> ORMAccessorProxy<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table) {
		if (table == null) {
			throw new NullPointerException();
		}
		try {
			return this.getDBAdapter().newORMAccessor(
					(MappingQueryStatementImpl) table.getMappingQueryDefine());
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final <TElement, TElementMeta extends NamedFactoryElement> TElement newElement(
			Context userContext,
			NamedFactoryElementGather<TElement, TElementMeta> factory,
			TElementMeta meta, Object[] adArgs) {
		if (factory == null) {
			throw new NullArgumentException("factory");
		}
		if (meta == null) {
			throw new NullArgumentException("meta");
		}
		final SpaceNode occorAtSave = meta.space.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			return factory.doNewElement(userContext, meta, adArgs);
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	// /////////////////////////////////////////
	// /////////// 进展通知器
	// /////////////////////////////////////////

	public final float getNextStep() {
		return this.contextProgressNextStep;
	}

	/**
	 * 设置下一步的步长，同时增加进度
	 * 
	 * @param step
	 *            下一步的步长
	 */
	public final float setNextStep(float nextStep) {
		this.checkValid();
		float contextProgress;
		final float lastStep = this.contextProgressNextStep;
		if (lastStep > 0f) {
			contextProgress = this.contextProgress += lastStep;
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		} else {
			contextProgress = this.contextProgress;
		}
		if (nextStep <= 0f) {
			this.contextProgressNextStep = 0f;
		} else {
			final float rest = 1f - contextProgress;
			if (nextStep > rest) {
				this.contextProgressNextStep = rest;
			} else {
				this.contextProgressNextStep = nextStep;
			}
		}
		return contextProgress;
	}

	public final float setNextPartialProgress(float nextProgress) {
		this.checkValid();
		float contextProgress;
		final float lastStep = this.contextProgressNextStep;
		if (lastStep > 0f) {
			contextProgress = this.contextProgress += lastStep;
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		} else {
			contextProgress = this.contextProgress;
		}
		if (nextProgress >= 1f) {
			this.contextProgressNextStep = 1f - contextProgress;
		} else if (nextProgress <= contextProgress) {
			this.contextProgressNextStep = 0f;
		} else {
			this.contextProgressNextStep = nextProgress - contextProgress;
		}
		return contextProgress;
	}

	/**
	 * 获得当前上下文处理的进度
	 * 
	 * @return 返回当前上下文处理的进度
	 */
	public final float getPartialProgress() {
		this.checkValid();
		return this.contextProgress;
	}

	public final float setPartialProgress(float progress) {
		this.checkValid();
		final float lastContextProgress = this.contextProgress;
		float newProgress = lastContextProgress + this.contextProgressNextStep;
		if (newProgress >= 1f) {
			newProgress = 1f;
		} else if (newProgress < progress) {
			if (progress > 1f) {
				newProgress = 1f;
			} else {
				newProgress = progress;
			}
		}
		this.contextProgress = newProgress;
		this.contextProgressNextStep = 0f;
		float lastStep = newProgress - lastContextProgress;
		if (lastStep > 0) {
			float progressQuotiety = this.progressQuotiety;
			if (progressQuotiety > 0) {
				final float p = this.progress + lastStep * progressQuotiety;
				this.progress = p < 0.9999f ? p : 0.9999f;
			}
		}
		return newProgress;
	}

	public final float getRestPartialProgress() {
		this.checkValid();
		return 1f - this.contextProgress - this.contextProgressNextStep;
	}

	/**
	 * 获得当前请求处理的总进度
	 * 
	 * @return 返回当前请求处理的总进度
	 */
	public final float getTotalProgress() {
		this.checkValid();
		return this.progress;
	}

	/**
	 * 获得当前上下文整个处理所占当前请求整个处理的比例。
	 * 
	 * @return 返回当前上下文整个处理所占当前请求整个处理的比例。
	 */
	public final float getPartialProgressQuotiety() {
		this.checkValid();
		return this.progressQuotiety;
	}

	/**
	 * 报告Hint信息
	 * 
	 */
	public final void reportHint(HintInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.HINT, null, null, null, null,
				0);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, null, null,
				null, 0);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, null,
				null, 0);
	}

	/**
	 * 报告Hint信息
	 */

	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, param3,
				null, 0);
	}

	/**
	 * 报告Hint信息
	 */
	public final void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.HINT, param1, param2, param3,
				others, 0);
	}

	/**
	 * 报告Error信息
	 * 
	 */
	public final void reportError(ErrorInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.ERROR, null, null, null, null,
				0);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, null, null,
				null, 0);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, null,
				null, 0);
	}

	/**
	 * 报告Error信息
	 */

	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, param3,
				null, 0);
	}

	/**
	 * 报告Error信息
	 */
	public final void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.ERROR, param1, param2, param3,
				others, 0);
	}

	/**
	 * 报告Done信息
	 * 
	 */
	public final void reportWarning(WarningInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.WARNING, null, null, null,
				null, 0);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, null, null,
				null, 0);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2, null,
				null, 0);
	}

	/**
	 * 报告Done信息
	 */

	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2,
				param3, null, 0);
	}

	/**
	 * 报告Done信息
	 */
	public final void reportWarning(WarningInfoDefine infoDefine,
			Object param1, Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.WARNING, param1, param2,
				param3, others, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine) {
		this.internalReport(infoDefine, InfoKind.PROCESS, null, null, null,
				null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, null, null,
				null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2, null,
				null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2,
				param3, null, 0);
	}

	public final void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalReport(infoDefine, InfoKind.PROCESS, param1, param2,
				param3, others, 0);
	}

	/**
	 * 尝试记录日志
	 */
	private final boolean tryLogInfo(InfoImpl info) {
		if (info.define.isNeedLog()) {
			this.occorAt.site.logManager.log(this.session, info);
			return true;
		}
		return false;
	}

	public final void endProcess() {
		this.checkValid();
		final InfoImpl lastInfo = this.lastInfo;
		final short invokeDepth = this.invokeDepth;
		final ProcessInfoImpl process = lastInfo != null ? lastInfo
				.finishRealProcess(invokeDepth) : null;
		if (process == null) {
			throw new EndProcessException();
		}
		this.lastInfo = process;
		this.leaveFrame(process.contextProgressSave,
				process.progressQuotietySave);
		this.tryLogInfo(process);
	}

	/**
	 * 当前信息环的尾部
	 */
	private InfoImpl lastInfo;

	final int fetchInfos(List<Info> to) {
		return 0;
	}

	/**
	 * 内部信息报告方法
	 */
	final void internalReport(InfoDefine infoDefineIntf, InfoKind kind,
			Object param1, Object param2, Object param3, Object[] others,
			int otherOffset) {
		this.checkValid();
		InfoDefineImpl infoDefine = (InfoDefineImpl) infoDefineIntf;
		if (infoDefine == null) {
			throw new NullArgumentException("infoDefine");
		}
		if (kind != infoDefine.kind) {
			throw new IllegalArgumentException("infoDefine 类型不符");
		}
		final InfoImpl newInfo;
		final InfoImpl lastInfo = this.lastInfo;
		final ProcessInfoImpl process = lastInfo == null ? null : lastInfo
				.getRealProcess();
		if (kind == InfoKind.PROCESS) {
			final float contextProgressSave = this.contextProgress;
			final float progressQuotietySave = this.enterFrame();
			this.lastInfo = newInfo = new ProcessInfoImpl(infoDefine, process,
					param1, param2, param3, others, otherOffset,
					contextProgressSave, progressQuotietySave, this.depth);
			if (process == null) {
				newInfo.insertAfter(lastInfo);
			}
		} else {
			this.lastInfo = newInfo = new InfoImpl(infoDefine, process, param1,
					param2, param3, others, otherOffset);
			if (process == null) {
				newInfo.insertAfter(lastInfo);
			}
			this.tryLogInfo(newInfo);
			if (kind == InfoKind.ERROR) {
				throw new InfoInterrupt(newInfo);
			}
		}
	}

	/**
	 * 返回查询情况的代码：<br/>
	 * -1 表示没有定义相应的资源服务；<br/>
	 * 0 表示原始查询结果为空；<br/>
	 * >0 表示原始查询结果不为空。
	 */
	@SuppressWarnings("unchecked")
	final <TFacade> int internalFillResTreeNodeFromGroup(
			Operation<? super TFacade> operation, TreeNodeImpl<TFacade> root,
			Class<TFacade> facadeClass, Object category,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (root == null) {
			throw new NullArgumentException("root");
		}
		ResourceServiceBase<?, ?, ?> resourceService = this.occorAtResourceService;
		if (resourceService == null
				|| facadeClass != resourceService.facadeClass) {
			resourceService = this.occorAt.findResourceService(facadeClass,
					this.getInvokeeQueryMode());
		}
		if (resourceService != null) {
			ResourceGroup group = resourceService.ensureResourceGroup(category,
					this);
			if (group != null) {
				int absoluteLevel;
				if (operation == null) {
					absoluteLevel = group.fillTree(root, this.transaction);

				} else {
					absoluteLevel = group.lockFillTree(operation, root,
							this.transaction);
				}
				final int result = root.getChildCount();
				root.filterAndSortRecursively(filter, absoluteLevel, 0,
						sortComparator);
				return result;
			}
		}
		return -1;
	}

	final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNodeFromGroup(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		this.checkValid();
		TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		final int code = this.internalFillResTreeNodeFromGroup(operation, root,
				facadeClass, None.NONE, filter, sortComparator);
		final boolean resDefined = code >= 0;
		if (code > 0 || operation != null) {
			return root;
		}
		this.internalFillTreeNodeNoResource(root, facadeClass, filter,
				sortComparator, null, null, null, resDefined);
		return root;
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, null,
				sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, filter,
				sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(null, facadeClass, filter,
				null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException {
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null, null,
				key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || sortComparator == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (key == null || facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || filter == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (key1 == null || key2 == null || filter == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Object key) throws UnsupportedOperationException {
		if (key == null || filter == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || key3 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {

		if (key1 == null || key2 == null || key3 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (key1 == null || key2 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> facadeClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (key == null || filter == null || sortComparator == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(null, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	private final <TFacade> void internalFillTreeNodeNoResource(
			TreeNodeImpl<TFacade> root, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, boolean isResource) {
		int absoluteLevel = 0;
		fillTree: {
			final InvokeeQueryMode mode = this.getInvokeeQueryMode();
			ServiceInvokeeBase<TFacade, Context, Object, Object, Object> provider = this.occorAt
					.findTreeNodeProvider(facadeClass, key1, key2, key3, mode);
			if (provider != null) {
				absoluteLevel = this.serviceProvideTree(root, provider, key1,
						key2, key3);
				if (!root.isEmpty()) {
					break fillTree;
				}
			}
			absoluteLevel = this.occorAt.tryFillTree(root, facadeClass, key1,
					key2, key3, mode);
			if (absoluteLevel < 0 && !isResource) {
				throw ServiceInvokeeBase.noTreeProviderException(facadeClass,
						key1, key2, key3);
			} else if (root.isEmpty()) {
				return;
			}
		}
		root.filterAndSortRecursively(filter, absoluteLevel, 0, sortComparator);
	}

	/**
	 * 返回查询情况的代码：<br/>
	 * -1 表示没有定义相应的资源服务；<br/>
	 * 0 表示原始查询结果为空；<br/>
	 * >0 表示原始查询结果不为空。
	 */
	@SuppressWarnings("unchecked")
	final <TFacade> int internalFillResTreeNodeFromItem(
			Operation<? super TFacade> operation, TreeNodeImpl<TFacade> root,
			Class<TFacade> facadeClass, Object category,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		if (root == null) {
			throw new NullPointerException();
		}
		ResourceIndexInfo rii = this.findResourceIndexInfo(facadeClass, key1,
				key2, key3, otherKeys);
		if (rii != null) {
			ResourceItem<TFacade, ?, ?> item = this.internalFindResource(null,
					rii, TransactionImpl.FIND_RESOURCE, null, category, key1,
					key2, key3, otherKeys);
			if (item != null) {
				int absoluteLevel;
				if (operation == null) {
					absoluteLevel = item.fillTree(root, this.transaction);

				} else {
					absoluteLevel = item.fillTree(operation, root,
							this.transaction);
				}
				int result = root.getElement() == null ? root.getChildCount()
						: 1;
				root.filterAndSortRecursively(filter, absoluteLevel, 0,
						sortComparator);
				return result;
			}
			return 0;
		}
		return -1;
	}

	final <TFacade> TreeNodeImpl<TFacade> internalGetTreeNodeFromItem(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		this.checkValid();
		TreeNodeImpl<TFacade> root = new TreeNodeImpl<TFacade>(null, null);
		final int code = this.internalFillResTreeNodeFromItem(operation, root,
				facadeClass, None.NONE, filter, sortComparator, key1, key2,
				key3, otherKeys);
		boolean resDefined = code >= 0;
		if (code > 0 || operation != null) {
			return root;
		}
		if (otherKeys == null || otherKeys.length == 0) {
			this.internalFillTreeNodeNoResource(root, facadeClass, filter,
					sortComparator, key1, key2, key3, resDefined);
		} else {
			throw ServiceInvokeeBase.noResourceTreeException(facadeClass, key1,
					key2, key3, otherKeys);
		}
		return root;
	}

	/**
	 * 获取列表结果
	 */
	private final <TResult, TKey1, TKey2, TKey3> int serviceProvideTree(
			TreeNode<TResult> root,
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> treeNodeProvider,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		if (treeNodeProvider == null) {
			throw new NullPointerException();
		}
		final SpaceNode occorAtSave = treeNodeProvider.getService()
				.updateContextSpace(this);
		final short invokeDepthSave = this.invokeDepth;
		final float contextProgressSave = this.contextProgress;
		float progressQuotietySave = this.beginContextInvoke();
		try {
			int absoluteLevel = 0;
			if (key1 == null) {
				absoluteLevel = treeNodeProvider.provide(this, root);
			} else if (key2 == null) {
				absoluteLevel = treeNodeProvider.provide(this, key1, root);
			} else if (key3 == null) {
				absoluteLevel = treeNodeProvider
						.provide(this, key1, key2, root);
			} else {
				absoluteLevel = treeNodeProvider.provide(this, key1, key2,
						key3, root);
			}
			if (this.exception != null) {
				throw this.exception;
			}
			return absoluteLevel;
		} catch (Throwable e) {
			progressQuotietySave = -progressQuotietySave;// 标记出过错
			throw Utils.tryThrowException(this.exception = e);
		} finally {
			this.endContextInvoke(occorAtSave, contextProgressSave,
					progressQuotietySave, invokeDepthSave);
		}
	}

	public final boolean isValid() {
		return !(this.thread != Thread.currentThread() || this.disposed || this.canceling);
	}

	public final void checkValid() {
		if (this.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("不允许通过其他线程访问上下文");
		}
		if (this.disposed) {
			throw new DisposedException("上下文已经被销毁");
		}
		if (this.canceling) {
			throw Utils.tryThrowException(new InterruptedException());
		}
	}

	public final boolean isCanceling() {
		if (this.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("不允许通过其他线程访问上下文");
		}
		if (this.disposed) {
			throw new DisposedException("上下文已经被销毁");
		}
		return this.canceling;
	}

	public final void throwIfCanceling() {
		if (this.isCanceling()) {
			throw Utils.tryThrowException(new InterruptedException());
		}
	}

	public final Object getCategory() {
		return None.NONE;
	}

	public final void setCategory(Object category) {
		throw new UnsupportedOperationException("上下文不支持设置资源类别");
	}

	public final CategorialResourceModifier<TFacadeM, TImplM, TKeysHolderM> usingResourceCategory(
			Object category) {
		if (category == null) {
			throw new NullArgumentException("category");
		}
		if (category == None.NONE) {
			return this;
		}
		return new CategorialResContextAdapter<TFacadeM, TImplM, TKeysHolderM>(
				this, category);
	}

	// /////////////////////////////////////////////////////////////////////
	// ///// Script
	// /////////////////////////////////////////////////////////////////////
	private final void disposeModelScriptContexts() {
		ModelScriptContextHolder lastMSContext = this.lastMSContext;
		if (lastMSContext != null) {
			ModelScriptContextHolder msContext = lastMSContext;
			do {
				msContext.msContext.release();
				ModelScriptContextHolder last = msContext;
				msContext = msContext.nextInContext;
				last.nextInContext = null;// help GC
			} while (msContext != lastMSContext);
			this.lastDBAdapter = null;
		}
	}

	private static class ModelScriptContextHolder {
		final ModelScriptEngine<?> engine;
		final ModelScriptContext<?> msContext;

		ModelScriptContextHolder(ModelScriptContext<?> msContext,
				ModelScriptContextHolder nextInContext) {
			this.engine = msContext.getEngine();
			this.msContext = msContext;
			this.nextInContext = nextInContext == null ? this : nextInContext;
		}

		ModelScriptContextHolder nextInContext;
	}

	private ModelScriptContextHolder lastMSContext;

	final ModelScriptContext<?> getScriptContext(ModelScriptEngine<?> engine) {
		if (engine == null) {
			throw new NullArgumentException("engine");
		}
		ModelScriptContextHolder lastMSContext = this.lastMSContext;
		if (lastMSContext != null) {
			if (lastMSContext.engine != engine) {

				for (ModelScriptContextHolder msContext = lastMSContext.nextInContext; msContext != lastMSContext; msContext = msContext.nextInContext) {
					if (msContext.engine == engine) {
						this.lastMSContext = msContext;
						return msContext.msContext;
					}
				}
				lastMSContext = this.lastMSContext = lastMSContext.nextInContext = new ModelScriptContextHolder(
						engine.allocContext(this), lastMSContext.nextInContext);
			}
		} else {
			lastMSContext = this.lastMSContext = new ModelScriptContextHolder(
					engine.allocContext(this), null);
		}
		return lastMSContext.msContext;
	}

	/**
	 * 找不到返回null
	 */
	final ModelScriptContext<?> tryGetScriptContext(String language) {
		ModelScriptEngine<?> engine = this.session.application.mseManager
				.findEngine(language);
		return engine == null ? null : this.getScriptContext(engine);
	}

	// ////////////////
	// // test
	// ////////////////
	@SuppressWarnings("unchecked")
	final void testCase(TestContext testContext, CaseTester tester)
			throws Throwable {
		this.resolveTrans();
		SpaceNode old = tester.getService().updateContextSpace(this);
		try {
			tester.testCase(this, testContext);
		} finally {
			this.resolveTrans();
			old.updateContextSpace(this);
		}
	}

	// /////////////////////
	// // 远程调用
	// //////////////////////
	public final ServiceInvoker usingRemoteInvoker(
			RemoteLoginInfo remoteLoginInfo) {
		if (!(remoteLoginInfo instanceof RemoteLoginInfoImpl)) {
			throw new UnsupportedOperationException("不支持的远程登录信息类型");
		}
		RemoteLoginInfoImpl rli = (RemoteLoginInfoImpl) remoteLoginInfo;
		if (rli.isToSelf()) {
			return this;
		}
		return new RemoteServiceInvokerImpl(this,
				(RemoteLoginInfoImpl) remoteLoginInfo);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host, int port) {
		return this.allocRemoteLoginInfo(host, port,
				InternalUser.anonymUser.name, "", RemoteLoginLife.TRANS);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password) {
		return this.allocRemoteLoginInfo(host, port, user, password,
				RemoteLoginLife.TRANS);
	}

	/**
	 * 获取远程调用信息
	 */
	public final RemoteLoginInfoImpl allocRemoteLoginInfo(String host,
			int port, String user, String password, RemoteLoginLife life) {
		NetNodeInfo cni = this.session.application.getNetNodeInfo(host, port);
		// TODO 是否需要缓存LoginInfo?
		return new RemoteLoginInfoImpl(cni, user, password, life);
	}

	// /////////////////本地化//////////////////////////////
	public final Locale getLocale() {
		return this.session.locate;
	}

	final String internalLocalize(InfoDefine info, Object p1, Object p2,
			Object p3, Object[] others) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		this.checkValid();
		return ((InfoDefineImpl) info).formatMessage(this.session.locate, p1,
				p2, p3, others);
	}

	final void internalLocalize(InfoDefine info, Appendable to, Object p1,
			Object p2, Object p3, Object[] others) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		if (to == null) {
			throw new NullArgumentException("to");
		}
		this.checkValid();
		((InfoDefineImpl) info).formatMessage(this.session.locate, to, p1, p2,
				p3, others);

	}

	public final String localize(InfoDefine info) {
		return this.internalLocalize(info, null, null, null, null);
	}

	public final String localize(InfoDefine info, Object param1) {
		return this.internalLocalize(info, param1, null, null, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2) {
		return this.internalLocalize(info, param1, param2, null, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3) {
		return this.internalLocalize(info, param1, param2, param3, null);
	}

	public final String localize(InfoDefine info, Object param1, Object param2,
			Object param3, Object... others) {
		return this.internalLocalize(info, param1, param2, param3, others);
	}

	public final void localize(Appendable to, InfoDefine info) {
		this.internalLocalize(info, to, null, null, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1) {
		this.internalLocalize(info, to, param1, null, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2) {
		this.internalLocalize(info, to, param1, param2, null, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3) {
		this.internalLocalize(info, to, param1, param2, param3, null);
	}

	public final void localize(Appendable to, InfoDefine info, Object param1,
			Object param2, Object param3, Object... others) {
		this.internalLocalize(info, to, param1, param2, param3, others);
	}

	// --------------------------以下权限相关-----------------------------------

	GUID currentOrgID;

	private UserAuthorityCheckerImpl currentUserOperationAuthorityChecker;

	private UserAuthorityCheckerImpl currentUserAccreditAuthorityChecker;

	final UserAuthorityCheckerImpl getCurrentUserOperationAuthorityChecker() {
		if (this.currentUserOperationAuthorityChecker == null) {
			this.currentUserOperationAuthorityChecker = ((UserAuthorityCheckerImpl) this
					.newUserAuthorityChecker(this.session.getUser(),
							this.currentOrgID, true));
		}
		return this.currentUserOperationAuthorityChecker;
	}

	final UserAuthorityCheckerImpl getCurrentUserAccreditAuthorityChecker() {
		if (this.currentUserAccreditAuthorityChecker == null) {
			this.currentUserAccreditAuthorityChecker = ((UserAuthorityCheckerImpl) this
					.newUserAuthorityChecker(this.session.getUser(),
							this.currentOrgID, false));
		}
		return this.currentUserAccreditAuthorityChecker;
	}

	final void resetACLCache() {
		this.currentUserOperationAuthorityChecker = null;
		this.currentUserAccreditAuthorityChecker = null;
	}

	public final void setUserCurrentOrg(GUID orgID) {
		if (this.session.internalGetUser().supportAuthority()) {
			if (orgID == null) {
				orgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
			}
			if (!orgID.equals(this.currentOrgID)) {
				this.resetACLCache();
			}
			this.currentOrgID = orgID;
		} else {
			throw new UnsupportedOperationException("当前用户不支持权限操作");
		}
	}

	public final GUID getUserCurrentOrg() {
		return this.currentOrgID;
	}

	public final <TResFacade> Authority getAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		final ResourceItem<TResFacade, ?, ?> resourceItem = ContextImpl
				.checkResourceToken(resource);
		if (resourceItem == null) {
			return Authority.DENY;
		}
		return resourceItem.getAuthority(operation, this, true);
	}

	public final <TResFacade> boolean hasAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		final ResourceItem<TResFacade, ?, ?> resourceItem = ContextImpl
				.checkResourceToken(resource);
		if (resourceItem == null) {
			return false;
		}
		return resourceItem.validateAuthority(operation, this, true);
	}

	public final <TResFacade> Authority getAccreditAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		final ResourceItem<TResFacade, ?, ?> resourceItem = ContextImpl
				.checkResourceToken(resource);
		if (resourceItem == null) {
			return Authority.DENY;
		}
		return resourceItem.getAuthority(operation, this, false);
	}

	public final <TResFacade> boolean hasAccreditAuthority(
			Operation<? super TResFacade> operation,
			ResourceStub<TResFacade> resource) {
		final ResourceItem<TResFacade, ?, ?> resourceItem = ContextImpl
				.checkResourceToken(resource);
		if (resourceItem == null) {
			return false;
		}
		return resourceItem.validateAuthority(operation, this, false);
	}

	@SuppressWarnings("unchecked")
	private static final <TResFacade> ResourceItem<TResFacade, ?, ?> checkResourceToken(
			ResourceStub<TResFacade> resourceStub) {
		final ResourceToken<TResFacade> resourceToken = ContextImpl
				.checkResourceStub(resourceStub);
		if (resourceToken instanceof ResourceItem) {
			return ((ResourceItem<TResFacade, ?, ?>) resourceToken);
		} else if (resourceToken instanceof ResourceTokenMissing) {
			return null;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private static final <TResFacade> ResourceToken<TResFacade> checkResourceStub(
			ResourceStub<TResFacade> resourceStub) {
		if (resourceStub == null) {
			throw new NullArgumentException("resourceStub");
		}
		if (resourceStub instanceof ResourceHandle<?>) {
			return ((ResourceHandle<TResFacade>) resourceStub).getToken();
		} else {
			return (ResourceToken<TResFacade>) resourceStub;
		}
	}

	public final RoleAuthorityChecker newRoleAuthorityChecker(Role role,
			GUID orgID, boolean operationAuthority) {
		final IInternalRole iRole = role instanceof IInternalRole ? (IInternalRole) role
				: this.checkAuthorityRole(this.findResourceToken(Role.class,
						role.getID()));
		if (operationAuthority) {
			return new RoleAuthorityCheckerImpl(this, iRole, orgID, iRole
					.getOperationACLs(this, orgID));
		}
		return new RoleAuthorityCheckerImpl(this, iRole, orgID, iRole
				.getAccreditACLs(this, orgID));
	}

	public final RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority) {
		final IInternalRole iRole = this.checkAuthorityRole(this
				.findResourceToken(Role.class, roleID));
		if (operationAuthority) {
			return new RoleAuthorityCheckerImpl(this, iRole, orgID, iRole
					.getOperationACLs(this, orgID));
		}
		return new RoleAuthorityCheckerImpl(this, iRole, orgID, iRole
				.getAccreditACLs(this, orgID));
	}

	public final UserAuthorityChecker newUserAuthorityChecker(User user,
			GUID orgID, boolean operationAuthority) {
		final IInternalUser iUser = user instanceof IInternalUser ? (IInternalUser) user
				: this.checkAuthorityUser(this.findResourceToken(User.class,
						user.getID()));
		if (operationAuthority) {
			return new UserAuthorityCheckerImpl(this, iUser, orgID, iUser
					.getOperationACLs(this, orgID));
		}
		return new UserAuthorityCheckerImpl(this, iUser, orgID, iUser
				.getAccreditACLs(this, orgID));
	}

	public final UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority) {
		final IInternalUser iUser = this.checkAuthorityUser(this
				.findResourceToken(User.class, userID));
		if (operationAuthority) {
			return new UserAuthorityCheckerImpl(this, iUser, orgID, iUser
					.getOperationACLs(this, orgID));
		}
		return new UserAuthorityCheckerImpl(this, iUser, orgID, iUser
				.getAccreditACLs(this, orgID));
	}

	@SuppressWarnings( { "unchecked" })
	private final IInternalRole checkAuthorityRole(ResourceItem<Role, ?, ?> role) {
		if (role != null && (role.getImpl() instanceof CoreAuthRoleEntity)) {
			return new RoleProxy(
					(ResourceItem<Role, CoreAuthRoleEntity, CoreAuthRoleEntity>) role);
		}
		throw new UnsupportedOperationException("当前角色不支持权限操作");
	}

	@SuppressWarnings( { "unchecked" })
	private final IInternalUser checkAuthorityUser(ResourceItem<User, ?, ?> user) {
		if (user != null && (user.getImpl() instanceof CoreAuthUserEntity)) {
			return new UserProxy(
					(ResourceItem<User, CoreAuthUserEntity, CoreAuthUserEntity>) user);
		}
		throw new UnsupportedOperationException("当前用户不支持权限操作");
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this
				.internalFind(operation, facadeClass, null, null, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalFind(operation, facadeClass, key, null, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this
				.internalFind(operation, facadeClass, key1, key2, null, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this
				.internalFind(operation, facadeClass, key1, key2, key3, null);
	}

	public final <TFacade> TFacade find(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... keys) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| keys[0] == null) {
			throw new NullPointerException();
		}
		return this
				.internalFind(operation, facadeClass, key1, key2, key3, keys);
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass) throws UnsupportedOperationException,
			MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(operation, facadeClass, null, null,
				null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass
					+ "]类的无键（单实例）对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(operation, facadeClass, key, null,
				null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(operation, facadeClass, key1, key2,
				null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(operation, facadeClass, key1, key2,
				key3, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + "]对象");
		}
		return result;
	}

	public final <TFacade> TFacade get(Operation<? super TFacade> operation,
			Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException,
			MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		TFacade result = this.internalFind(operation, facadeClass, key1, key2,
				key3, otherKeys);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + ", ...]对象");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		try {
			return this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, null, null, null, null);
		} catch (NoAccessAuthorityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		try {
			return this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key, null, null, null);
		} catch (NoAccessAuthorityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		try {
			return this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, null, null);
		} catch (NoAccessAuthorityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		try {
			return this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, key3, null);
		} catch (NoAccessAuthorityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> findResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		try {
			return this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, key3, otherKeys);
		} catch (NoAccessAuthorityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result;
		try {
			result = this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, null, null, null, null);
		} catch (NoAccessAuthorityException e) {
			result = null;
		}
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass
					+ "]类的无键（单实例）资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result;
		try {
			result = this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key, null, null, null);
		} catch (NoAccessAuthorityException e) {
			result = null;
		}
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result;
		try {
			result = this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, null, null);
		} catch (NoAccessAuthorityException e) {
			result = null;
		}
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result;
		try {
			result = this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, key3, null);
		} catch (NoAccessAuthorityException e) {
			result = null;
		}
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + "]资源");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> ResourceToken<TFacade> getResourceToken(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws MissingObjectException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		ResourceToken<TFacade> result;
		try {
			result = this.internalFindResource(operation,
					TransactionImpl.FIND_RESOURCE, null, facadeClass,
					None.NONE, key1, key2, key3, otherKeys);
		} catch (NoAccessAuthorityException e) {
			result = null;
		}
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为["
					+ key1 + ", " + key2 + ", " + key3 + ", ...]资源");
		}
		return result;
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null, null, null,
				null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null, null, key,
				null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null, null, key1,
				key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null, null, key1,
				key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, null, null, key1,
				key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(null, resultClass, filter, null, null,
				null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter, null, key,
				null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter, null, key1,
				key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter, null, key1,
				key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, filter, null, key1,
				key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null,
				sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null,
				sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| sortComparator == null || otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, null, sortComparator,
				key1, key2, key3, otherKeys);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter,
				sortComparator, null, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key == null || filter == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter,
				sortComparator, key, null, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetList(operation, resultClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> List<TFacade> getList(
			Operation<? super TFacade> operation, Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (resultClass == null || key1 == null || key2 == null || key3 == null
				|| filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceList(resultClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass, null,
				null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || key1 == null || key2 == null || key3 == null
				|| otherKeys == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass,
				filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null || filter == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || filter == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || filter == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				null, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass, null,
				sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null || facadeClass == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || sortComparator == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || facadeClass == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, null,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || filter == null || sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromGroup(operation, facadeClass,
				filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null || filter == null || sortComparator == null
				|| facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key, null, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, null, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || filter == null
				|| sortComparator == null || facadeClass == null) {
			throw new NullPointerException();
		}
		return this.internalGetTreeNodeFromItem(operation, facadeClass, filter,
				sortComparator, key1, key2, key3, otherKeys);
	}

	public final TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalCloneResource(operation, token, null, null);
	}

	public final TImplM cloneResource(Operation<? super TFacadeM> operation,
			ResourceToken<TFacadeM> token, TImplM tryReuse) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalCloneResource(operation, token, tryReuse, null);
	}

	public final void invalidResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		this.internalInvalidResource(operation, null, null, null, null);
	}

	public final <TKey> void invalidResource(
			Operation<? super TFacadeM> operation, TKey key)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key, null, null, null);
	}

	public final <TKey1, TKey2> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, null, null);
	}

	public final <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, key3, null);
	}

	public final <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		this.internalInvalidResource(operation, key1, key2, key3, keys);
	}

	public final TImplM modifyResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalModifyResource(operation, null, null, null, null);
	}

	public final <TKey> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey key)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key, null, null, null);
	}

	public final <TKey1, TKey2> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, null, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, key3, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM modifyResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalModifyResource(operation, key1, key2, key3, keys);
	}

	public final TImplM removeResource(Operation<? super TFacadeM> operation)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		return this.internalRemoveResource(operation, null, null, null, null);
	}

	public final <TKey> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey key)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key, null, null, null);
	}

	public final <TKey1, TKey2> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, null, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, key3, null);
	}

	public final <TKey1, TKey2, TKey3> TImplM removeResource(
			Operation<? super TFacadeM> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (key1 == null || key2 == null || key3 == null || keys == null) {
			throw new NullPointerException();
		}
		return this.internalRemoveResource(operation, key1, key2, key3, keys);
	}

	/*
	 * public <THolderFacade> void putResourceReference( Operation<? super
	 * TFacadeM> operation, ResourceToken<THolderFacade> holder,
	 * ResourceToken<TFacadeM> reference) { if (operation == null) { throw new
	 * NullArgumentException("operation"); } // TODO
	 * 是否既要验证对holder，又要验证对reference的权限 throw new
	 * UnsupportedOperationException(); }
	 */

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || holderToken == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(operation, facadeClass,
				holderToken, null, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || holderToken == null || filter == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(operation, facadeClass,
				holderToken, filter, null);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Comparator<? super TFacade> sortComparator) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || holderToken == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(operation, facadeClass,
				holderToken, null, sortComparator);
	}

	public final <TFacade, THolderFacade> List<TFacade> getResourceReferences(
			Operation<? super TFacade> operation, Class<TFacade> facadeClass,
			ResourceToken<THolderFacade> holderToken,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (facadeClass == null || holderToken == null || filter == null
				|| sortComparator == null) {
			throw new NullPointerException();
		}
		return this.internalGetResourceReferences(operation, facadeClass,
				holderToken, filter, sortComparator);
	}

	@SuppressWarnings("unchecked")
	public final <THolderFacade> void removeResourceReference(
			Operation<? super TFacadeM> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacadeM> reference) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<THolderFacade, ?, ?>) holder).removeReference(operation,
				this.transaction, (ResourceItem<TFacadeM, ?, ?>) reference);
	}

	@SuppressWarnings("unchecked")
	public final <TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacadeM> holder,
			ResourceToken<TReferenceFacade> reference) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (holder == null || reference == null) {
			throw new NullPointerException();
		}
		((ResourceItem<TFacadeM, ?, ?>) holder).removeReference(operation,
				this.transaction,
				(ResourceItem<TReferenceFacade, ?, ?>) reference);
	}

	// ---------------------------以上权限相关------------------------------

}
