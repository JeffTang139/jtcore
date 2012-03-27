package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.exception.SessionDisposedException.SessionDisposedKind;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.spi.application.Session;
import org.eclipse.jt.core.spi.application.SessionIniter;


/**
 * 会话池
 * 
 * @author Jeff Tang
 * 
 */
public class SessionManager {
	final ApplicationImpl application;

	private void disposeOrReset(ExceptionCatcher catcher, boolean disposeOrReset) {
		final ArrayList<SessionImpl> sessions;
		this.writeLock.lock();
		try {
			if (this.size > 0) {
				sessions = new ArrayList<SessionImpl>(this.size);
				for (SessionImpl session : this.sessionHashTable) {
					while (session != null) {
						sessions.add(session);
						session = session.nextInHashTable;
					}
				}
			} else {
				sessions = null;
			}
			if (disposeOrReset) {
				this.systemSession.internalDispose(0l);
			} else {
				this.systemSession = this.newSystemSession();
			}
		} finally {
			this.writeLock.unlock();
		}
		if (sessions != null) {
			for (SessionImpl session : sessions) {
				try {
					session.internalDispose(disposeOrReset ? 0l : 1l);
				} catch (Throwable e) {
					catcher.catchException(e, session);
				}
			}
		}
	}

	/**
	 * 重置会话，用于参数同步后重置会话
	 */
	final void doReset(ExceptionCatcher catcher) {
		this.disposeOrReset(catcher, false);
	}

	final void doDispose(ExceptionCatcher catcher) {
		this.disposeOrReset(catcher, true);
	}

	/**
	 * 会话超时时间（秒），三十分钟
	 */
	private int sessionTimeoutMinutes;

	private SessionImpl newSystemSession() {
		return new SessionImpl(this.application,
				this.application.timeRelatedSequence.next(),
				SessionKind.SYSTEM, InternalUser.system, null, null,
				this.sessionTimeoutMinutes);
	}

	final static String xml_element_session = "session";
	final static String xml_element_sessionTimeoutMins = "timeout-m";

	SessionManager(ApplicationImpl application, SXElement sessionConfig) {
		if (application == null) {
			throw new NullArgumentException("application");
		}
		this.sessionTimeoutMinutes = sessionConfig != null ? sessionConfig
				.getInt(xml_element_sessionTimeoutMins,
						Session.DEFAULT_TIMEOUT_MINUTEs)
				: Session.DEFAULT_TIMEOUT_MINUTEs;
		this.application = application;
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.readLock = rwl.readLock();
		this.writeLock = rwl.writeLock();
		this.systemSession = this.newSystemSession();
		application.overlappedManager.postWork(new RepeatWork(5000) {
			@Override
			protected void workDoing(WorkingThread thread) throws Throwable {
				SessionManager.this.clearExpiredSessions();
			}
		});
	}

	private final void clearExpiredSessions() {
		final long now = System.currentTimeMillis();
		final long maxPVCAge = now - 10000;// 清理掉5秒内没有访问的性能指标收集器
		ArrayList<SessionImpl> expiredOrTimeOuts = null;
		ArrayList<SessionImpl> needGcPVCs = null;
		this.readLock.lock();
		try {
			if (this.size > 0) {
				for (SessionImpl session : this.sessionHashTable) {
					while (session != null) {
						if (session.itsTimeToDispose(now)) {
							if (expiredOrTimeOuts == null) {
								expiredOrTimeOuts = new ArrayList<SessionImpl>();
							}
							expiredOrTimeOuts.add(session);
						} else if (session.pmv_CollectOld(maxPVCAge)) {
							if (needGcPVCs == null) {
								needGcPVCs = new ArrayList<SessionImpl>();
							}
							needGcPVCs.add(session);
						}
						session = session.nextInHashTable;
					}
				}
			}
			if (this.systemSession.pmv_CollectOld(maxPVCAge)) {
				if (needGcPVCs == null) {
					needGcPVCs = new ArrayList<SessionImpl>();
				}
				needGcPVCs.add(this.systemSession);
			}
		} finally {
			this.readLock.unlock();
		}
		if (needGcPVCs != null) {
			for (int i = 0, c = needGcPVCs.size(); i < c; i++) {
				needGcPVCs.get(i).pmv_GC();
			}
		}
		if (expiredOrTimeOuts != null) {
			for (int i = 0, c = expiredOrTimeOuts.size(); i < c; i++) {
				expiredOrTimeOuts.get(i).doDispose();
			}
		}
	}

	final void remove(SessionImpl session) {
		this.writeLock.lock();
		try {
			final SessionImpl[] sessionHashTable = this.sessionHashTable;
			if (sessionHashTable == null) {
				return;
			}
			final int index = TimeRelatedSequenceImpl.hash(session.id)
					& (sessionHashTable.length - 1);
			for (SessionImpl s = sessionHashTable[index], prov = null; s != null; prov = s, s = s.nextInHashTable) {
				if (s == session) {
					if (prov == null) {
						sessionHashTable[index] = session.nextInHashTable;
					} else {
						prov.nextInHashTable = session.nextInHashTable;
					}
					this.size--;
					if (session.kind == SessionKind.NORMAL) {
						this.normalSessionCount--;
					}
					break;
				}
			}
		} finally {
			this.writeLock.unlock();
			session.nextInHashTable = null;
		}
	}

	private volatile SessionImpl systemSession;

	final SessionImpl getSystemSession() {
		return this.systemSession;
	}

	private final ReadLock readLock;
	private final WriteLock writeLock;

	final <TUserData> SessionImpl newSession(SessionKind kind, User user,
			SessionIniter<TUserData> sessionIniter, TUserData userData) {
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		if (user == null) {
			throw new NullArgumentException("user");
		}
		final int hash;
		final long sessionID;
		if (kind == SessionKind.SYSTEM) {
			throw new IllegalArgumentException("不支持创建系统会话");
		}
		sessionID = this.application.timeRelatedSequence.next();
		hash = TimeRelatedSequenceImpl.hash(sessionID);
		final SessionImpl session;
		this.writeLock.lock();
		try {
			SessionImpl[] sessionHashTable = this.sessionHashTable;
			if (sessionHashTable == null) {
				this.sessionHashTable = sessionHashTable = new SessionImpl[256];
			}
			final int tableL = sessionHashTable.length;
			final int index;
			if (++this.size > tableL * 0.75) {
				final int newLen = tableL * 2;
				final int newHigh = newLen - 1;
				final SessionImpl[] newTable = new SessionImpl[newLen];
				for (int j = 0; j < tableL; j++) {
					for (SessionImpl s = sessionHashTable[j], next; s != null; s = next) {
						next = s.nextInHashTable;
						final int newIndex = TimeRelatedSequenceImpl.hash(s.id)
								& newHigh;
						s.nextInHashTable = newTable[newIndex];
						newTable[newIndex] = s;
					}
				}
				this.sessionHashTable = sessionHashTable = newTable;
				index = hash & newHigh;
			} else {
				index = hash & (tableL - 1);
			}
			session = sessionHashTable[index] = new SessionImpl(
					this.application, sessionID, kind, user, null,
					sessionHashTable[index], this.sessionTimeoutMinutes);
			if (kind == SessionKind.NORMAL) {
				this.normalSessionCount++;
			}
		} finally {
			this.writeLock.unlock();
		}
		if (sessionIniter != null) {
			try {
				sessionIniter.initSession(session, userData);
			} catch (Throwable e) {
				session.internalDispose(0);
				throw Utils.tryThrowException(e);
			}
		}
		return session;
	}

	final SessionImpl getOrFindSession(long sessionID, boolean get)
			throws SessionDisposedException {
		final SessionImpl systemSession;
		SessionImpl session;
		find: {
			this.readLock.lock();
			try {
				systemSession = this.systemSession;
				if (sessionID == systemSession.id) {
					return systemSession;
				}
				final int hash = TimeRelatedSequenceImpl.hash(sessionID);
				if (this.size > 0) {
					final SessionImpl[] sessionHashTable = this.sessionHashTable;
					for (session = sessionHashTable[hash
							& (sessionHashTable.length - 1)]; session != null; session = session.nextInHashTable) {
						if (session.id == sessionID) {
							if (session.disposingOrDisposed()) {
								if (get) {
									break find;
								} else {
									return null;
								}
							} else {
								return session;
							}
						}
					}
				}
			} finally {
				this.readLock.unlock();
			}
			if (!get) {
				return null;
			}
		}
		throw new SessionDisposedException(
				sessionID < systemSession.id ? SessionDisposedKind.OBSOLETE
						: SessionDisposedKind.NORMAL);
	}

	private volatile SessionImpl[] sessionHashTable;
	private volatile int size;
	private volatile int normalSessionCount;

	public final int getNormalSessionCount() {
		return this.normalSessionCount;
	}

	/**
	 * 获得所有普通会话列表
	 */
	@SuppressWarnings("unchecked")
	public final List<? extends SessionImpl> getNormalSessions() {
		if (this.normalSessionCount > 0) {
			this.readLock.lock();
			try {
				if (this.normalSessionCount > 0) {
					final ArrayList<SessionImpl> to = new ArrayList<SessionImpl>(
							this.normalSessionCount);
					for (SessionImpl session : this.sessionHashTable) {
						while (session != null) {
							if (session.kind == SessionKind.NORMAL) {
								to.add(session);
							}
							session = session.nextInHashTable;
						}
					}
					return to;
				}
			} finally {
				this.readLock.unlock();
			}
		}
		return Collections.EMPTY_LIST;
	}
}
