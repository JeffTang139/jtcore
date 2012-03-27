package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.log.LogTask;

/**
 * ��־������<br>
 * Ҫ����site�����ͷź���ɵ�����
 * 
 * @author Jeff Tang
 * 
 */
final class LogManager {
	final Site site;
	/**
	 * β��
	 */
	private LogEntryImpl tail;
	/**
	 * ��ֹ״̬
	 */
	final static int S_DISABLE = 0;
	/**
	 * ׼��״̬����ʱ������־��Ŀ�����ǲ���������¼��־�߳�
	 */
	final static int S_PREPARING = 1;
	/**
	 * ����״̬��������־��Ϣ��ͬʱ��¼��־
	 */
	final static int S_READY = 2;
	/**
	 * ״̬
	 */
	private volatile int state;
	private LogWork logWork;

	/**
	 * ����״̬
	 */
	final void setState(int newState) {
		ok: synchronized (this) {
			switch (this.state) {
			case S_DISABLE:
				switch (newState) {
				case S_DISABLE:
				case S_PREPARING:
					break;
				case S_READY:
					this.site.application.overlappedManager
							.postWork(this.logWork = new LogWork());
					break;
				default:
					break ok;
				}
				break;
			case S_PREPARING:
				switch (newState) {
				case S_PREPARING:
					break;
				case S_DISABLE:
					this.tail = null;
					this.toLog = null;
					break;
				case S_READY:
					this.site.application.overlappedManager
							.postWork(this.logWork = new LogWork());
					break;
				default:
					break ok;
				}
				break;
			case S_READY:
				switch (newState) {
				case S_READY:
					break;
				case S_DISABLE:
					this.tail = null;
					this.toLog = null;
				case S_PREPARING:
					this.logWork.cancel();
					this.logWork = null;
					break;
				default:
					break ok;
				}
				break;
			default:
				break ok;
			}
			this.state = newState;
			return;
		}
		throw new IllegalArgumentException("illegal state: old[" + this.state
				+ "],new[" + newState + "]");
	}

	/**
	 * ׷����־
	 */
	final void log(SessionImpl session, InfoImpl info) {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		if (session == null) {
			throw new NullArgumentException("session");
		}
		info.tryAllocID(session.application);
		LogEntryImpl le = new LogEntryImpl(session, info);
		synchronized (this) {
			LogEntryImpl tail = this.tail;
			if (tail == null) {
				this.tail = le.next = le;
				if (this.state == S_READY) {
					this.notify();
				}
			} else {
				le.next = tail.next;// first
				this.tail = tail.next = le;
			}
		}
	}

	private volatile LogEntryImpl toLog;

	private boolean loggerWaitToLog() throws InterruptedException {
		LogEntryImpl tail;
		synchronized (this) {
			if (this.state != S_READY) {
				return false;// ��ֹ
			}
			tail = this.tail;
			if (tail == null) {
				this.wait();
				tail = this.tail;
				if (tail == null) {
					return false;// ��ֹ
				} else {
					this.tail = null;
				}
			}
		}
		this.toLog = tail.next;
		tail.next = null;
		return true;
	}

	final LogEntryImpl loggerGetToLog() {
		if (this.state != S_READY) {
			return null;// ��ֹ
		}
		LogEntryImpl to = this.toLog;
		if (to == null) {
			LogEntryImpl tail;
			synchronized (this) {
				tail = this.tail;
				if (tail == null) {
					return null;// ��ֹ
				} else {
					this.tail = null;
				}
			}
			to = tail.next;
			tail.next = null;
		}
		this.toLog = to.next;
		to.next = null;
		return to;
	}

	private final void loggerDoLog(WorkingThread thread) {
		thread.setName("d&a-logger-" + this.site.name);
		final LogTask logTask = new LogTask(this);
		try {
			while (this.loggerWaitToLog()) {
				final ContextImpl<?, ?, ?> context = LogManager.this.site
						.newSystemSessionSiteContext(ContextKind.TRANSIENT);
				try {
					context.handle(logTask);
				} catch (Throwable e) {
					context.catcher.catchException(e, this);
				} finally {
					context.dispose();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	private class LogWork extends Work {
		@Override
		protected void workDoing(WorkingThread thread) throws Throwable {
			LogManager.this.loggerDoLog(thread);
		}
	}

	LogManager(Site site) {
		this.site = site;
	}

}
