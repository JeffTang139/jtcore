package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ExceptionCatcher;

/**
 * 异步调用管理器
 * 
 * @author Jeff Tang
 * 
 */
final class WorkingManager {
	/**
	 * 默认的最小线程数量
	 */
	static final int DEFAULT_MIN_THREADS = 4;
	/**
	 * 默认的线程最大闲置时间
	 */
	static final int DEFAULT_MAX_IDLE_LIFE = 60 * 1000;
	/**
	 * 调度器
	 */
	private Scheduler scheduler;

	final boolean isActive() {
		return this.scheduler != null;
	}

	final ApplicationImpl application;

	WorkingManager(ApplicationImpl application) {
		if (application == null) {
			throw new NullArgumentException("application");
		}
		this.application = application;
		this.startWork(this.scheduler = new Scheduler());
	}

	/**
	 * 启动异步调用<br>
	 * 该方法的调用必须与工作的启动线程在同一个线程，或者锁定工作
	 * 
	 * @param invoke
	 */
	final void postWork(Work work) {
		if (work == null) {
			throw new NullArgumentException("work");
		}
		final Scheduler schdlr = this.scheduler;
		if (schdlr != null) {
			if (work.getStartTime() <= System.currentTimeMillis()) {
				if (work.tryEnterConcurrentControlScope()) {
					this.startWork(work);
				}
			} else {
				schdlr.putScheduledWork(work);
			}
		}
	}

	final void doDispose(ExceptionCatcher catcher) {
		try {
			synchronized (this) {
				if (this.threads == 0) {
					return;
				}
				this.scheduler = null;
				this.idleThreads = null;
				this.startingWorksTail = null;
				this.threadGroup.interrupt();
				this.wait();
			}
		} catch (InterruptedException e) {
			if (catcher != null) {
				catcher.catchException(e, this);
			}
		}
	}

	final ThreadGroup threadGroup = new ThreadGroup("d&a");
	/**
	 * 最小线程数量
	 */
	private int minIdleThreads = DEFAULT_MIN_THREADS;
	/**
	 * 最大空闲时间
	 */
	private int maxIdleLife = DEFAULT_MAX_IDLE_LIFE;

	/**
	 * 空闲线程数量
	 * 
	 * @return
	 */
	final int getIdleThreads() {
		return this.idles;
	}

	/**
	 * 是否线程不够用
	 */
	final boolean isLowOnThreads() {
		return this.threads == this.maxThreads && this.idleThreads == null;
	}

	/**
	 * 线程的最大个数
	 */
	private int maxThreads = Integer.MAX_VALUE;

	/**
	 * 线程总数
	 */
	private int threads;
	/**
	 * 需要马上执行的工作，当线程不够时进入该列队。
	 */
	private Work startingWorksTail;
	/**
	 * 闲置的线程
	 */
	private WorkingThread idleThreads;
	/**
	 * 闲置的线程数量
	 */
	private int idles;

	/**
	 * 执行工作 <br>
	 * 涉及到工作的状态的改变，<br>
	 * 当前线程与工作的提交线程不是同一个线程时需要外部锁定工作<br>
	 * 当前已知的不在同一线程的地方是activeWork();ConcurrentController.leaveScope(
	 * OverlappedManager)
	 */
	final synchronized boolean startWork(Work work) {
		if (this.scheduler == null) {
			return false;
		}
		WorkingThread idle = this.idleThreads;
		if (idle != null) {
			if (work.putToWorkThread()) {
				this.idleThreads = idle.nextIdle;
				this.idles--;
				synchronized (idle) {
					idle.work = work;
					idle.notify();
				}
				return true;
			}
		} else if (work.putToStartQ(this.startingWorksTail)) {
			this.startingWorksTail = work;
			if (this.threads < this.maxThreads) {
				this.newThread();
			}
			return true;
		}
		return false;
	}

	/**
	 * 线程编号
	 */
	private int threadNum;

	/**
	 * 新建线程编号
	 * 
	 * @return
	 */
	private final void newThread() {
		this.threads++;
		new WorkingThread(this, "d&a-".concat(Integer
				.toString(++this.threadNum)));
	}

	private final void threadExitNoSync(WorkingThread thread, boolean idleThread) {
		if (idleThread) {
			for (WorkingThread first = this.idleThreads, last = null; first != null; last = first, first = first.nextIdle) {
				if (first == thread) {
					// 从列队中移除
					if (last == null) {
						this.idleThreads = thread.nextIdle;
					} else {
						last.nextIdle = thread.nextIdle;
					}
					this.idles--;
					break;
				}
			}
		}
		thread.nextIdle = null;
		if (--this.threads == 0) {
			this.notify();
		}
	}

	final synchronized void threadExit(WorkingThread thread) {
		this.threadExitNoSync(thread, false);
	}

	/**
	 * 获取工作
	 * 
	 * @return 返回null表示要退出线程
	 */
	final Work getWorkToDo(WorkingThread thread) {
		for (;;) {
			Work todo;
			findWorkToDo: {
				// 尝试取出等待执行的工作，否则将线程放入空闲列队
				synchronized (this) {
					// 大于最大线程数时就要收缩
					if (this.scheduler == null
							|| this.threads > this.maxThreads) {
						this.threadExitNoSync(thread, false);
						return null;
					}
					// 如果列队中有则返回
					final Work tail = this.startingWorksTail;
					if (tail != null) {
						final Work first = tail.removeNext();
						if (first == tail) {
							this.startingWorksTail = null;
						}
						todo = first;
						break findWorkToDo;
					}
					// 否则放入空闲列队
					thread.nextIdle = this.idleThreads;
					this.idleThreads = thread;
					this.idles++;
				}
				//
				for (;;) {
					// 运行到这里时线程已经是空闲线程
					try {
						synchronized (thread) {
							// 检查在上下两个同步块间是否被分派了工作
							todo = thread.work;
							if (todo == null) {
								// 否则等待
								thread.wait(this.maxIdleLife);
								todo = thread.work;
							}
							thread.work = null;
						}
					} catch (InterruptedException e) {
						synchronized (this) {
							this.threadExitNoSync(thread, true);
						}
						// 被终止，管理器停止，这是不用考虑工作，以及从空闲列队中移除
						return null;
					}
					if (todo != null) {
						break findWorkToDo;
					}
					// 超时，但其闲置与否（是否被分配了工作）仍不可确定
					synchronized (this) {
						// 即便是闲置超时线程，但进入这里前线程仍然可能会被分配工作
						todo = thread.work;
						if (todo != null) {
							thread.work = null;
							break findWorkToDo;
						}
						// 确实闲置
						if (this.idles > this.minIdleThreads) {
							// 超过最小线程数，因此从闲置列队中移除，并且终止线程
							this.threadExitNoSync(thread, true);
							return null;
						}
					}
					// 线程还可以保留，因此从新等待
				}
			}
			if (todo.getStartTime() <= System.currentTimeMillis()) {
				return todo;
			}
			final Scheduler schdlr = this.scheduler;
			if (schdlr != null) {
				schdlr.putScheduledWork(todo);
			}
		}
	}

	/**
	 * 调度器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private final class Scheduler extends Work {
		/**
		 * 定时工作列队的队尾，这个列队里的工作都还没有到工作的时间
		 */
		private Work scheduledWorksTail;

		/**
		 * 将定时工作放入列队
		 */
		final void putScheduledWork(Work work) {
			synchronized (this) {
				int m = work.putToSchedulingQ(this.scheduledWorksTail);
				if ((m & Work.WORK_LAST_MASK) != 0) {
					this.scheduledWorksTail = work;
				}
				if ((m & Work.WORK_FIRST_MASK) != 0) {
					this.notify();
				}
			}
		}

		@Override
		protected final void doWork(WorkingThread thread) {
			thread.setName("d&a - scheduler");
			synchronized (this) {
				for (;;) {
					long timeout;
					if (this.scheduledWorksTail != null) {
						timeout = this.scheduledWorksTail
								.tryTailStartScheduledWorks(thread.manager);
						if (timeout == 0) {
							this.scheduledWorksTail = null;
						}
					} else {
						timeout = 0;
					}
					try {
						this.wait(timeout);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}
	}
}
