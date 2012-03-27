package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.log.LogTask;

/**
 * 日志管理器<br>
 * 要谨防site不慎释放后造成的悬空
 * 
 * @author Jeff Tang
 * 
 */
final class LogManager {
	final Site site;
	/**
	 * 尾部
	 */
	private LogEntryImpl tail;
	/**
	 * 禁止状态
	 */
	final static int S_DISABLE = 0;
	/**
	 * 准备状态，这时接受日志条目，但是不会启动记录日志线程
	 */
	final static int S_PREPARING = 1;
	/**
	 * 就绪状态，接受日志信息，同时记录日志
	 */
	final static int S_READY = 2;
	/**
	 * 状态
	 */
	private volatile int state;
	private LogWork logWork;

	/**
	 * 设置状态
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
	 * 追加日志
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
				return false;// 终止
			}
			tail = this.tail;
			if (tail == null) {
				this.wait();
				tail = this.tail;
				if (tail == null) {
					return false;// 终止
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
			return null;// 终止
		}
		LogEntryImpl to = this.toLog;
		if (to == null) {
			LogEntryImpl tail;
			synchronized (this) {
				tail = this.tail;
				if (tail == null) {
					return null;// 终止
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
