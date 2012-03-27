package org.eclipse.jt.core.impl;

import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * 为Jetty而作的线程池
 * 
 * @author Jeff Tang
 * 
 */
final class ThreadPoolForJetty implements ThreadPool {
	static final int DEFAULT_MAX_THREADS = 200;
	final WorkingManager manager;

	ThreadPoolForJetty(WorkingManager manager) {
		this(manager, DEFAULT_MAX_THREADS);
	}

	ThreadPoolForJetty(WorkingManager manager, int maxThread) {
		this.manager = manager;
		this.maxThread = maxThread < 1 ? 1 : maxThread;
	}

	private final int maxThread;
	private int actives;

	private Work queueTail;

	final synchronized void WorkFinalizing() {
		this.actives--;
		for (Work tail = this.queueTail; tail != null
		        && this.actives < this.maxThread;) {
			Work work = tail.removeNext();
			if (work == tail) {
				this.queueTail = null;
				tail = null;
			}
			// 当前线程与工作的提交线程不同，但工作不会被取消因此不需要锁定工作
			if (this.manager.startWork(work)) {
				this.actives++;
			}
		}
	}

	public final synchronized boolean dispatch(Runnable job) {
		if (this.manager.isActive()) {
			HttpWorkForJetty work = new HttpWorkForJetty(this, job);
			if (this.actives < this.maxThread) {
				this.manager.startWork(work);
				this.actives++;
			} else {
				work.putToQ(this.queueTail);
				this.queueTail = work;
			}
			return true;
		}
		return false;
	}

	public final int getIdleThreads() {
		return Math.min(this.manager.getIdleThreads(), this.maxThread);
	}

	public final int getThreads() {
		return this.actives;
	}

	public final boolean isLowOnThreads() {
		return this.manager.isLowOnThreads();
	}

	public final void join() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

}
