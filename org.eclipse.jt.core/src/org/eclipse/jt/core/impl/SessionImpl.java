package org.eclipse.jt.core.impl;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.Login;
import org.eclipse.jt.core.LoginState;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.exception.SituationReentrantException;
import org.eclipse.jt.core.exception.SessionDisposedException.SessionDisposedKind;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.spi.application.RemoteInfoSPI;
import org.eclipse.jt.core.spi.application.Session;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceSequenceValueCollector;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.type.GUID;


final class SessionImpl implements Login, Session, RemoteInfoSPI {

	/**
	 * 创建会话
	 * 
	 * @param asSituation
	 *            是否做为情景上下文（运行UI主线程）
	 * @throws SessionDisposedException
	 *             会话已经过期
	 * @throws SituationReentrantException
	 *             如果是情景上下文，则报告情景重入异常（已经存在正在运行未退出的UI主线程）
	 */
	public final <TUserData> ContextImpl<?, ?, ?> newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException {
		return this.newContext(asSituation ? ContextKind.SITUATION
				: ContextKind.NORMAL);
	}

	/**
	 * 获得登陆的状态
	 * 
	 * @return
	 */
	public final LoginState getState() {
		return this.state;
	}

	public final long getID() {
		return this.id;
	}

	public final long getVerificationCode() {
		return this.verificationCode;
	}

	@Override
	public final String toString() {
		return String.format("{LOGIN [ID:%s, USER:%s, STATE:%s]}", this.id,
				this.user, this.state);
	}

	public final User getUser() {
		return this.user;
	}

	final IInternalUser internalGetUser() {
		return this.user;
	}

	/**
	 * 标记当会话中还有正在运行的上下文时忽略释放请求
	 */
	final static long IGNORE_IF_HAS_CONTEXT = -1l;

	/**
	 * 请求释放会话
	 * 
	 * @param timeout
	 *            释放会话的延迟，0表示马上释放<br>
	 *            当该值==IGNORE_IF_HAS_CONTEXT时表示如果还有正在运行的上下文则忽略该请求并返回false
	 * @return
	 */
	final boolean internalDispose(long timeout) {
		synchronized (this) {
			if (timeout == IGNORE_IF_HAS_CONTEXT && this.contexts != null) {
				return false;
			}
			switch (this.state) {
			case DISPOSED:
				return false;
			case DISPOSING:
				final long oldDisposeTimeout = this.themeTimeOrDisposeTimeout;
				if (oldDisposeTimeout != Long.MAX_VALUE) {
					final long disposeTimeout = System.currentTimeMillis()
							+ timeout;
					if (disposeTimeout < this.themeTimeOrDisposeTimeout) {
						this.themeTimeOrDisposeTimeout = disposeTimeout;
					}
				}
				return false;// 由会话清理线程负责释放
			default:
				this.state = LoginState.DISPOSING;
				if (timeout == 0 || this.contexts == null) {
					// 避免被重复清理
					this.themeTimeOrDisposeTimeout = Long.MAX_VALUE;
				} else {
					final long now = System.currentTimeMillis();
					this.heartbeatTimeOrDisposingTime = now;
					this.themeTimeOrDisposeTimeout = now + timeout;
					return false;// 由会话清理线程负责释放
				}
			}
		}
		this.doDispose();
		return true;
	}

	/**
	 * 尝试销毁，会保护系统会话
	 */
	public final void dispose(long timeout) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("不支持销毁系统会话！");
		}
		this.internalDispose(timeout < 0l ? 0l : timeout);
	}

	final IInternalUser changeUser(User newUser) {
		if (newUser == null) {
			throw new NullArgumentException("newUser");
		}
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("系统会话不支持切换用户");
		}
		synchronized (this) {
			switch (this.state) {
			case DISPOSING:
			case DISPOSED:
				throw new SessionDisposedException(SessionDisposedKind.NORMAL);
			}
			final IInternalUser oldUser = this.user;
			this.internalSetUser(newUser);
			return oldUser;
		}
	}

	// -------------------------------以下权限相关--------------------------------------

	private final void internalSetUser(User user) {
		if (user instanceof IInternalUser) {
			if (user instanceof UserProxy) {
				final UserProxy userProxy = (UserProxy) user;
				this.user = userProxy;
				this.currentOrgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
			} else {
				this.user = (IInternalUser) user;
				this.currentOrgID = null;
			}
			this.state = (InternalUser.anonymUser == user) ? LoginState.ANONYNOUS
					: LoginState.LOGIN;
		} else {
			throw new IllegalArgumentException("不支持的用户类型");
		}
	}

	public final void setUserCurrentOrg(GUID orgID) {
		if (this.user.supportAuthority()) {
			if (orgID == null) {
				orgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
			}
			this.currentOrgID = orgID;
		} else {
			throw new UnsupportedOperationException("当前用户不支持权限操作");
		}
	}

	public final GUID getUserCurrentOrg() {
		if (this.user.supportAuthority()) {
			return this.currentOrgID;
		} else {
			throw new UnsupportedOperationException("当前用户不支持权限操作");
		}
	}

	// -------------------------------以上权限相关--------------------------------------
	// --

	/**
	 * 获取Application
	 * 
	 * @return
	 */
	public final ApplicationImpl getApplication() {
		return this.application;
	}

	/**
	 * 判断会话是否过期需要销毁，仅针对普通会话而言
	 */
	final boolean itsTimeToDispose(final long now) {
		if (this.kind != SessionKind.NORMAL) {
			return false;
		}
		switch (this.state) {
		case DISPOSED:
			return false;
		case DISPOSING:
			return now >= this.themeTimeOrDisposeTimeout;
		default:
			final int hearbeatTimeoutSec = this.heartbeatTimeoutSec;
			if (hearbeatTimeoutSec > 0
					&& (now - this.heartbeatTimeOrDisposingTime) > hearbeatTimeoutSec * 1000) {
				return true;
			}
			final int sessionTimeoutMin = this.sessionTimeoutMinutes;
			if (sessionTimeoutMin > 0
					&& (now - this.themeTimeOrDisposeTimeout) > sessionTimeoutMin * 60000) {
				return true;
			}
			return false;
		}
	}

	public final SessionKind getKind() {
		return this.kind;
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
	final ApplicationImpl application;
	/**
	 * 登录ID
	 */
	final long id;
	/**
	 * 验证码
	 */
	final long verificationCode;
	/**
	 * 用户
	 */
	private IInternalUser user;
	/**
	 * 权限相关,用户当前组织映射
	 */
	GUID currentOrgID;
	/**
	 * 状态
	 */
	private volatile LoginState state;
	private volatile ContextImpl<?, ?, ?> contexts;
	final ResourceGroupMap resources = new ResourceGroupMap();
	/**
	 * 资源类别
	 */
	final SessionKind kind;
	/**
	 * 创建时间
	 */
	final long createTime;

	/**
	 * 最后一次主线程活动时间或销毁超时时间
	 */
	private long themeTimeOrDisposeTimeout;
	/**
	 * 上次心跳时间或开始销毁时间
	 */
	private long heartbeatTimeOrDisposingTime;
	/**
	 * 心跳延期时间（秒），五分钟
	 */
	private int heartbeatTimeoutSec = DEFAULT_HEARTBEAT_SECs;

	public final int getHeartbeatTimeoutSec() {
		return this.heartbeatTimeoutSec;
	}

	public final void setHeartbeatTimeoutSec(int heartbeatTimeoutSec) {
		this.heartbeatTimeoutSec = heartbeatTimeoutSec > 0 ? heartbeatTimeoutSec
				: 0;
	}

	/**
	 * 会话超时时间（秒），三十分钟
	 */
	private int sessionTimeoutMinutes;

	public final int getSessionTimeoutMinutes() {
		return this.sessionTimeoutMinutes;
	}

	public final void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
		this.sessionTimeoutMinutes = sessionTimeoutMinutes > 0 ? sessionTimeoutMinutes
				: 0;
	}

	public synchronized final long getLastInteractiveTime() {
		LoginState state = this.state;
		switch (state) {
		case DISPOSING:
		case DISPOSED:
			return this.heartbeatTimeOrDisposingTime;
		default:
			return this.themeTimeOrDisposeTimeout;
		}
	}

	final void contextCreated(ContextImpl<?, ?, ?> context)
			throws SessionDisposedException, SituationReentrantException {
		ContextKind contextKind = context.kind;
		if (contextKind == ContextKind.DISPOSER) {
			return;
		}
		synchronized (this) {
			this.checkNotDisposedNoSync();
			switch (contextKind) {
			case SITUATION:
				if (this.kind == SessionKind.SYSTEM
						|| this.themeContext != null) {
					throw new SituationReentrantException();
				}
				this.themeContext = context;
				this.heartbeatTimeOrDisposingTime = this.themeTimeOrDisposeTimeout = context.createTime;
				break;
			case NORMAL:
				this.heartbeatTimeOrDisposingTime = context.createTime;
				break;
			}
			ContextImpl<?, ?, ?> contexts = this.contexts;
			context.nextInSession = contexts;
			if (contexts != null) {
				contexts.prevInSession = context;
			}
			this.contexts = context;
		}
	}

	final void contextDisposed(ContextImpl<?, ?, ?> context) {
		if (context.kind == ContextKind.DISPOSER) {
			return;
		}
		synchronized (this) {
			if (this.themeContext == context) {
				this.themeContext = null;
			}
			final ContextImpl<?, ?, ?> nextInSession = context.nextInSession;
			final ContextImpl<?, ?, ?> prevInSession = context.prevInSession;
			if (prevInSession != null) {
				prevInSession.nextInSession = nextInSession;
				if (nextInSession != null) {
					nextInSession.prevInSession = prevInSession;
					context.nextInSession = null;
				}
				context.prevInSession = null;
			} else {
				if (nextInSession != null) {
					nextInSession.prevInSession = null;
					context.nextInSession = null;
				}
				this.contexts = nextInSession;
			}
		}
	}

	/**
	 * 重置会话，用于参数同步时重置系统会话
	 */
	final void reset(ExceptionCatcher catcher) {
		this.resources.reset(catcher, false);
		if (this.themeRootSituation != null) {
			try {
				this.themeRootSituation.internalClose();
				this.themeRootSituation = new SituationImpl(this);
			} catch (Throwable e) {
				catcher.catchException(e, this);
			}
		}
		this.themeData = null;
		this.pendingTail = null;
		this.locate = Locale.getDefault();
	}

	final void doDispose() {
		// this.state == LoginState.DISPOSING
		this.application.sessionManager.remove(this);
		ContextImpl<?, ?, ?> disposer = null;
		try {
			final PerformanceValueCollectorContainerImpl collectorContainer = this.collectorContainer;
			if (collectorContainer != null) {
				this.collectorContainer = null;
				try {
					disposer = collectorContainer.dispose(this, disposer);
				} catch (Throwable e) {
					// 忽略
				}
			}
			final Site site = this.application.getDefaultSite();
			if (site != null) {
				// XXX 通知所有的站点
				disposer = site.sessionDisposing(this, disposer);
			}
		} catch (Throwable e) {
			// 忽略
		} finally {
			if (disposer != null) {
				try {
					disposer.dispose();
				} catch (Throwable e) {
					// 忽略
				}
			}
		}
		synchronized (this) {
			this.state = LoginState.DISPOSED;
			ContextImpl<?, ?, ?> context = this.contexts;
			if (context != null) {
				this.contexts = null;
				do {
					final ContextImpl<?, ?, ?> next = context.nextInSession;
					context.nextInSession = null;// helpGC
					try {
						context.cancel();
					} catch (Throwable e) {
						// 忽略
					}
					context = next;
				} while (context != null);
			}
		}
		try {
			this.resources.reset(this.application.catcher, true);
		} catch (Throwable e) {
			// 忽略
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param application
	 */
	SessionImpl(ApplicationImpl application, long id, SessionKind kind,
			User user, Object themeData, SessionImpl nextInHashTable,
			int sessionTimeoutMinutes) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		if (application == null) {
			throw new NullArgumentException("application");
		}
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
		this.kind = kind;
		this.id = id;
		this.verificationCode = GUID.randomLong();
		this.application = application;
		this.nextInHashTable = nextInHashTable;
		this.internalSetUser(user);
		this.createTime = this.heartbeatTimeOrDisposingTime = this.themeTimeOrDisposeTimeout = System
				.currentTimeMillis();
		if (kind == SessionKind.NORMAL) {
			this.themeRootSituation = new SituationImpl(this);
		}
	}

	final ContextImpl<?, ?, ?> newContext(SpaceNode occorAt, ContextKind kind)
			throws SessionDisposedException, SituationReentrantException {
		return new ContextImpl<Object, Object, Object>(this, occorAt, kind,
				occorAt.site.newTransaction());
	}

	final ContextImpl<?, ?, ?> newContext(ContextKind kind)
			throws SessionDisposedException, SituationReentrantException {
		final Site site = this.application.getDefaultSite();
		return new ContextImpl<Object, Object, Object>(this, site, kind, site
				.newTransaction());
	}

	final ContextImpl<?, ?, ?> newContext(TransactionImpl transaction,
			ContextKind kind) throws SessionDisposedException,
			SituationReentrantException {
		return new ContextImpl<Object, Object, Object>(this, transaction.site,
				kind, transaction);
	}

	// ///////////////////////////////////////////////////////////

	/**
	 * 主线的根情景
	 */
	private SituationImpl themeRootSituation;

	public final SituationImpl resetSituation() {
		this.usingSituation();
		if (this.themeRootSituation != null) {
			this.themeRootSituation.internalClose();
			return this.themeRootSituation = new SituationImpl(this);
		} else {
			throw new UnsupportedOperationException("非普通会话不支持重置情景对象");
		}
	}

	/**
	 * 主线的当前上下文
	 */
	volatile ContextImpl<?, ?, ?> themeContext;

	/**
	 * 使用情景上下文
	 */
	final ContextImpl<?, ?, ?> usingSituation() {
		ContextImpl<?, ?, ?> context = this.themeContext;
		if (context == null || context.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("非界面线程禁止访问情景接口");
		}
		return context;
	}

	/**
	 * 主线数据
	 */
	private Object themeData;

	// //////////////////////////////////////////////////

	public final Object getData() {
		return this.themeData;
	}

	public final SituationImpl getSituation() {
		return this.themeRootSituation;
	}

	public final Object setData(Object data) {
		Object old = this.themeData;
		this.themeData = data;
		return old;
	}

	public final RemoteInfoSPI getRemoteInfo() {
		return this;
	}

	// ////////////////////////////
	SessionImpl nextInHashTable;
	// ///////////////////////////////////

	/**
	 * 消息队列的尾部
	 */
	private PendingMessageImpl<?> pendingTail;

	/**
	 * 从等待消息列队中移除<br>
	 * 调用该方法前必须锁定当前theme对象
	 */
	final PendingMessageImpl<?> removePendingMessageNoSync(
			PendingMessageImpl<?> pending) {
		if (pending.prev == pending) {
			if (this.pendingTail == pending) {
				this.pendingTail = null;
			}
		} else {
			pending.prev.next = pending.next;
			pending.next.prev = pending.prev;
			if (this.pendingTail == pending) {
				this.pendingTail = pending.prev;
			}
		}
		return pending;
	}

	private final void checkNotDisposedNoSync() {
		switch (this.state) {
		case DISPOSING:
		case DISPOSED:
			throw new SessionDisposedException(SessionDisposedKind.NORMAL);
		case LOGIN:
			switch (this.user.getState()) {
			case DISABLE:
			case DISPOSED:
				this.dispose(1);
				throw new SessionDisposedException(
						SessionDisposedKind.USERINVALID);
			}
		}
	}

	final boolean disposingOrDisposed() {
		final LoginState s = this.state;
		return s == LoginState.DISPOSED || s == LoginState.DISPOSING;
	}

	final void addPendingMessage(PendingMessageImpl<?> pending) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("系统会话不支持此调用");
		}
		synchronized (this) {
			this.checkNotDisposedNoSync();
			PendingMessageImpl<?> tail = this.pendingTail;
			if (tail == null) {
				pending.next = pending;
				pending.prev = pending;
			} else {
				pending.prev = tail;
				pending.next = tail.next;
				tail.next = pending;
				pending.next.prev = pending;
			}
			this.pendingTail = pending;
		}
	}

	final boolean handlePendingMessage() {
		PendingMessageImpl<?> one;
		synchronized (this) {
			if (this.pendingTail == null) {
				return false;
			}
			one = this.removePendingMessageNoSync(this.pendingTail.next);
		}
		one.removeSelfFromSender();
		one.handle();
		return true;
	}

	// //////////////////////////
	private String remoteAddr = "";
	private String remoteHost = "";

	public final String getRemoteAddr() {
		return this.remoteAddr;
	}

	public final String getRemoteHost() {
		return this.remoteHost;
	}

	public final void setRemoteAddr(String addr) {
		if (addr == null) {
			throw new NullArgumentException("addr");
		}
		this.remoteAddr = addr;
	}

	public final void setRemoteHost(String host) {
		if (host == null) {
			throw new NullArgumentException("host");
		}
		this.remoteHost = host;
	}

	// //////////////////// 本地化 //////////////////////
	Locale locate = Locale.getDefault();

	public final void setLocale(Locale locale) {
		if (locale == null) {
			throw new NullArgumentException("locale");
		}
		this.locate = locale;
	}

	public final Locale getLocale() {
		return this.locate;
	}

	// ///////////////////////////////////
	// //////////////性能监控//////////////
	private volatile PerformanceValueCollectorContainerImpl collectorContainer;
	private static final AtomicReferenceFieldUpdater<SessionImpl, PerformanceValueCollectorContainerImpl> pvccUpdater = AtomicReferenceFieldUpdater
			.newUpdater(SessionImpl.class,
					PerformanceValueCollectorContainerImpl.class,
					"collectorContainer");

	public final PerformanceValueCollector<?> findPerformanceValueCollector(
			PerformanceIndexDefine performanceIndexDefine) {
		if (performanceIndexDefine == null) {
			throw new NullArgumentException("performanceIndexDefine");
		}
		final PerformanceValueCollectorContainerImpl collectorContainer;
		if (performanceIndexDefine.isUnderSession()) {
			collectorContainer = this.collectorContainer;
		} else {
			collectorContainer = this.application.sessionManager
					.getSystemSession().collectorContainer;
		}
		return collectorContainer == null ? null : this.collectorContainer
				.findCollector(performanceIndexDefine);
	}

	/**
	 * 是否需要清理性能监控值收集器
	 */
	final boolean pmv_CollectOld(long maxAge) {
		if (this.disposingOrDisposed()) {
			return false;
		}
		final PerformanceValueCollectorContainerImpl collectorContainer = this.collectorContainer;
		return collectorContainer != null
				&& collectorContainer.collectOld(maxAge);
	}

	final void pmv_GC() {
		if (this.disposingOrDisposed()) {
			return;
		}
		final PerformanceValueCollectorContainerImpl collectorContainer = this.collectorContainer;
		if (collectorContainer != null) {
			try {
				collectorContainer.gc(this);
			} catch (Throwable e) {
				this.application.catcher.catchException(e, this);
			}
		}
	}

	final void pmv_UpdateValues(PerformanceValueRequestEntry requests,
			GUID monitorID) {
		if (requests == null) {
			throw new NullArgumentException("requests");
		}
		if (this.disposingOrDisposed()) {
			return;
		}
		PerformanceValueCollectorContainerImpl collectorContainer;
		for (;;) {
			collectorContainer = this.collectorContainer;
			if (collectorContainer == null) {
				collectorContainer = new PerformanceValueCollectorContainerImpl();
				if (pvccUpdater.compareAndSet(this, null, collectorContainer)) {
					break;
				}
			} else {
				break;
			}
		}
		collectorContainer.updateValues(this, monitorID, requests);
	}

	volatile PerformanceSequenceValueCollector<Object, PMV_SimpleSql> pmv_SimpleSql;
	volatile PerformanceSequenceValueCollector<Object, PMV_Sql> pmv_Sql;
	// //////////////性能监控//////////////
	// ///////////////////////////////////
}
