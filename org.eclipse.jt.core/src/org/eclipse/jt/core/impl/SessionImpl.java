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
	 * �����Ự
	 * 
	 * @param asSituation
	 *            �Ƿ���Ϊ�龰�����ģ�����UI���̣߳�
	 * @throws SessionDisposedException
	 *             �Ự�Ѿ�����
	 * @throws SituationReentrantException
	 *             ������龰�����ģ��򱨸��龰�����쳣���Ѿ�������������δ�˳���UI���̣߳�
	 */
	public final <TUserData> ContextImpl<?, ?, ?> newContext(boolean asSituation)
			throws SessionDisposedException, SituationReentrantException {
		return this.newContext(asSituation ? ContextKind.SITUATION
				: ContextKind.NORMAL);
	}

	/**
	 * ��õ�½��״̬
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
	 * ��ǵ��Ự�л����������е�������ʱ�����ͷ�����
	 */
	final static long IGNORE_IF_HAS_CONTEXT = -1l;

	/**
	 * �����ͷŻỰ
	 * 
	 * @param timeout
	 *            �ͷŻỰ���ӳ٣�0��ʾ�����ͷ�<br>
	 *            ����ֵ==IGNORE_IF_HAS_CONTEXTʱ��ʾ��������������е�����������Ը����󲢷���false
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
				return false;// �ɻỰ�����̸߳����ͷ�
			default:
				this.state = LoginState.DISPOSING;
				if (timeout == 0 || this.contexts == null) {
					// ���ⱻ�ظ�����
					this.themeTimeOrDisposeTimeout = Long.MAX_VALUE;
				} else {
					final long now = System.currentTimeMillis();
					this.heartbeatTimeOrDisposingTime = now;
					this.themeTimeOrDisposeTimeout = now + timeout;
					return false;// �ɻỰ�����̸߳����ͷ�
				}
			}
		}
		this.doDispose();
		return true;
	}

	/**
	 * �������٣��ᱣ��ϵͳ�Ự
	 */
	public final void dispose(long timeout) {
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("��֧������ϵͳ�Ự��");
		}
		this.internalDispose(timeout < 0l ? 0l : timeout);
	}

	final IInternalUser changeUser(User newUser) {
		if (newUser == null) {
			throw new NullArgumentException("newUser");
		}
		if (this.kind == SessionKind.SYSTEM) {
			throw new UnsupportedOperationException("ϵͳ�Ự��֧���л��û�");
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

	// -------------------------------����Ȩ�����--------------------------------------

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
			throw new IllegalArgumentException("��֧�ֵ��û�����");
		}
	}

	public final void setUserCurrentOrg(GUID orgID) {
		if (this.user.supportAuthority()) {
			if (orgID == null) {
				orgID = CoreAuthActorEntity.GLOBAL_ORG_ID;
			}
			this.currentOrgID = orgID;
		} else {
			throw new UnsupportedOperationException("��ǰ�û���֧��Ȩ�޲���");
		}
	}

	public final GUID getUserCurrentOrg() {
		if (this.user.supportAuthority()) {
			return this.currentOrgID;
		} else {
			throw new UnsupportedOperationException("��ǰ�û���֧��Ȩ�޲���");
		}
	}

	// -------------------------------����Ȩ�����--------------------------------------
	// --

	/**
	 * ��ȡApplication
	 * 
	 * @return
	 */
	public final ApplicationImpl getApplication() {
		return this.application;
	}

	/**
	 * �жϻỰ�Ƿ������Ҫ���٣��������ͨ�Ự����
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
	// //////////////////�������ڲ�����/////////////////////////////////////////////
	// ////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////
	// ////
	// ////////////////////
	final ApplicationImpl application;
	/**
	 * ��¼ID
	 */
	final long id;
	/**
	 * ��֤��
	 */
	final long verificationCode;
	/**
	 * �û�
	 */
	private IInternalUser user;
	/**
	 * Ȩ�����,�û���ǰ��֯ӳ��
	 */
	GUID currentOrgID;
	/**
	 * ״̬
	 */
	private volatile LoginState state;
	private volatile ContextImpl<?, ?, ?> contexts;
	final ResourceGroupMap resources = new ResourceGroupMap();
	/**
	 * ��Դ���
	 */
	final SessionKind kind;
	/**
	 * ����ʱ��
	 */
	final long createTime;

	/**
	 * ���һ�����̻߳ʱ������ٳ�ʱʱ��
	 */
	private long themeTimeOrDisposeTimeout;
	/**
	 * �ϴ�����ʱ���ʼ����ʱ��
	 */
	private long heartbeatTimeOrDisposingTime;
	/**
	 * ��������ʱ�䣨�룩�������
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
	 * �Ự��ʱʱ�䣨�룩����ʮ����
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
	 * ���ûỰ�����ڲ���ͬ��ʱ����ϵͳ�Ự
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
					// ����
				}
			}
			final Site site = this.application.getDefaultSite();
			if (site != null) {
				// XXX ֪ͨ���е�վ��
				disposer = site.sessionDisposing(this, disposer);
			}
		} catch (Throwable e) {
			// ����
		} finally {
			if (disposer != null) {
				try {
					disposer.dispose();
				} catch (Throwable e) {
					// ����
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
						// ����
					}
					context = next;
				} while (context != null);
			}
		}
		try {
			this.resources.reset(this.application.catcher, true);
		} catch (Throwable e) {
			// ����
		}
	}

	/**
	 * ���캯��
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
	 * ���ߵĸ��龰
	 */
	private SituationImpl themeRootSituation;

	public final SituationImpl resetSituation() {
		this.usingSituation();
		if (this.themeRootSituation != null) {
			this.themeRootSituation.internalClose();
			return this.themeRootSituation = new SituationImpl(this);
		} else {
			throw new UnsupportedOperationException("����ͨ�Ự��֧�������龰����");
		}
	}

	/**
	 * ���ߵĵ�ǰ������
	 */
	volatile ContextImpl<?, ?, ?> themeContext;

	/**
	 * ʹ���龰������
	 */
	final ContextImpl<?, ?, ?> usingSituation() {
		ContextImpl<?, ?, ?> context = this.themeContext;
		if (context == null || context.thread != Thread.currentThread()) {
			throw new UnsupportedOperationException("�ǽ����߳̽�ֹ�����龰�ӿ�");
		}
		return context;
	}

	/**
	 * ��������
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
	 * ��Ϣ���е�β��
	 */
	private PendingMessageImpl<?> pendingTail;

	/**
	 * �ӵȴ���Ϣ�ж����Ƴ�<br>
	 * ���ø÷���ǰ����������ǰtheme����
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
			throw new UnsupportedOperationException("ϵͳ�Ự��֧�ִ˵���");
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

	// //////////////////// ���ػ� //////////////////////
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
	// //////////////���ܼ��//////////////
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
	 * �Ƿ���Ҫ�������ܼ��ֵ�ռ���
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
	// //////////////���ܼ��//////////////
	// ///////////////////////////////////
}
