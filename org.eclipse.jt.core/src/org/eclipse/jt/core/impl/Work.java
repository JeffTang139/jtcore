package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.AsyncState;

/**
 * 异步处理
 * 
 * @author Jeff Tang
 * 
 */
abstract class Work {
	/**
	 * 任务列队的下一个
	 */
	private Work next;

	/**
	 * 从环中剥离出
	 */
	final Work removeNext() {
		Work work = this.next;
		this.next = work.next;
		work.next = null;
		return work;
	}

	/**
	 * 状态
	 */
	private volatile AsyncState state = AsyncState.POSTING;

	/**
	 * 放入环形列队中
	 * 
	 * @param queueTail
	 */
	final void putToQ(Work queueTail) {
		if (queueTail == null) {
			this.next = this;
		} else {
			this.next = queueTail.next;
			queueTail.next = this;
		}
	}

	protected final IllegalStateException illegalState() {
		return new IllegalStateException("无效状态: " + this.state);
	}

	/**
	 * 进入处理列队状态
	 */
	final boolean putToStartQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
		case QUEUING:
			this.state = AsyncState.STARTING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		this.putToQ(queueTail);
		return true;
	}

	final boolean putToWorkThread() {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
		case QUEUING:
			this.state = AsyncState.PROCESSING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		return true;
	}

	/**
	 * 进入并发控制列队状态
	 */
	final boolean putToCuncerringQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
			this.state = AsyncState.QUEUING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		this.putToQ(queueTail);
		return true;
	}

	final static int WORK_LAST_MASK = 1;
	final static int WORK_FIRST_MASK = 2;

	/**
	 * 记入定时列队
	 * 
	 * @param regeneration
	 *            标记是否是重新调度
	 * @param queueTail
	 *            时间表的队尾
	 * @return 返回是否在列队尾或首
	 */
	final int putToSchedulingQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
			this.state = AsyncState.SCHEDULING;
			break;
		case CANCELED:
			return 0;
		default:
			throw this.illegalState();
		}
		if (queueTail == null) {
			this.next = this;
			return WORK_LAST_MASK | WORK_FIRST_MASK;
		} else {
			final long startTime = this.getStartTime();
			// 放在队尾的概率高，因此单独判断
			if (startTime >= queueTail.getStartTime()) {
				this.next = queueTail.next;
				queueTail.next = this;
				return WORK_LAST_MASK;
			}
			Work last = queueTail;
			Work work = queueTail.next;
			while (work != queueTail && work.getStartTime() <= startTime) {
				last = work;
				work = work.next;
			}
			this.next = work;
			last.next = this;
			return last == queueTail ? WORK_FIRST_MASK : 0;
		}
	}

	protected final void checkFinished() throws IllegalStateException {
		if (!this.state.stopped) {
			throw new IllegalStateException("异步操作还未完成");
		}
	}

	// long startime;

	/**
	 * 获得并发控制器，返回null表示不控制并发
	 */
	protected ConcurrentController getConcurrentController() {
		return null;
	}

	/**
	 * 尝试进入并发控制区域<br>
	 * 如果没有并发控制，或者成功进入返回true<br>
	 * 涉及到工作的状态的改变，<br>
	 * 当前线程与工作的提交线程不是同一个线程时需要外部锁定工作<br>
	 */
	final boolean tryEnterConcurrentControlScope() {
		ConcurrentController ccr = this.getConcurrentController();
		return (ccr == null || ccr.enterScope(this));
	}

	/**
	 * 尝试启动到时间的工作<br>
	 * 
	 * @return 返回随后的等待时间
	 */
	final long tryTailStartScheduledWorks(WorkingManager manager) {
		long timeout;
		final long curr = System.currentTimeMillis();
		for (Work first = this.next; (timeout = first.getStartTime() - curr) <= 0; first = this.next) {
			if (first != this) {
				this.next = first.next;
				first.next = null;
			}
			// 当前线程与工作的提交线程不同，需要锁定工作
			synchronized (first) {
				if (first.tryEnterConcurrentControlScope()) {
					manager.startWork(first);
				}
			}
			if (first == this) {
				return 0;// 全部结束
			}
		}
		return timeout;
	}

	/**
	 * 通知再生，返回是否再生（通过这种模式实现周期工作）
	 */
	protected boolean regeneration() {
		return false;
	}

	/**
	 * 返回任务的开始时间
	 */
	protected long getStartTime() {
		return 0l;
	}

	/**
	 * 开始前，返回false表示需要终止
	 */
	protected boolean workBeginning() {
		return true;
	}

	/**
	 * 开始工作
	 * 
	 * @throws Throwable
	 */
	protected void workDoing(WorkingThread thread) throws Throwable {
	}

	/**
	 * 取消中
	 */
	protected void workCanceling() {
		this.thread.interrupt();
	}

	/**
	 * 完结中
	 * 
	 * @param e
	 *            无异常时为null
	 */
	protected void workFinalizing(Throwable e) {

	}

	private WorkingThread thread;

	/**
	 * 返回false表示需要终止
	 */
	private final boolean beforeDoingNoSync(WorkingThread thread,
			ConcurrentController ccr) {
		switch (this.state) {
		case CANCELED:
			if (ccr != null) {
				// 离开并发区域
				ccr.leaveScope(thread.manager);
			}
			return false;// 提前就被终止
		case POSTING:
		case STARTING:
			this.state = AsyncState.PROCESSING;
		case PROCESSING:
			break;
		default:
			throw this.illegalState();
		}
		this.thread = thread;
		return this.workBeginning();
	}

	private final void afterDoingNoSync(WorkingThread thread,
			ConcurrentController ccr, Throwable ex) {
		this.thread = null;
		try {
			this.workFinalizing(ex);
		} finally {
			if (ccr != null) {
				// 离开并发区域
				ccr.leaveScope(thread.manager);
			}
			this.notifyAll();
			// 确定最终状态
			if (ex == null) {
				this.state = AsyncState.FINISHED;
			} else if (ex instanceof InterruptedException
					&& this.state.canceling) {
				this.state = AsyncState.CANCELED;
				return;
			} else {
				this.state = AsyncState.ERROR;
				return;
			}
			// 尝试周期性工作
			if (this.regeneration()) {
				this.state = AsyncState.POSTING;
				thread.manager.postWork(this);
			}
		}
	}

	/**
	 * 运行，返回false表示要求线程终止
	 * 
	 * @param thread
	 * @return
	 * @throws Throwable
	 */
	protected void doWork(WorkingThread thread) throws Throwable {
		ConcurrentController ccr = this.getConcurrentController();
		Throwable ex = null;
		beforeDoing: synchronized (this) {
			try {
				if (this.beforeDoingNoSync(thread, ccr)) {
					break beforeDoing;
				}
			} catch (Throwable e) {
				ex = e;
			}
			this.afterDoingNoSync(thread, ccr, ex);
			if (ex != null) {
				throw ex;
			} else {
				return;
			}
		}
		try {
			this.workDoing(thread);
		} catch (Throwable e) {
			ex = e;
		}
		synchronized (this) {
			this.afterDoingNoSync(thread, ccr, ex);
		}
		if (ex != null) {
			throw ex;
		}
	}

	/**
	 * 尝试取消，不一定能够成功地取消，这取决于实现者是否做了相应的支持
	 */
	public synchronized final void cancel() {
		switch (this.state) {
		// 处理之前
		case SCHEDULING:
		case QUEUING:
			// TODO 清理
		case POSTING:
		case STARTING:
			this.state = AsyncState.CANCELED;
			this.notifyAll();
			return;

			// 处理之中
		case PROCESSING:
			this.state = AsyncState.CANCELING;
			break;
		// 处理结束
		case FINISHED:
		case ERROR:
		case CANCELED:
		case CANCELING:
			return;// 已经结束或正在结束
		default:
			throw this.illegalState();
		}
		this.workCanceling();
	}

	/**
	 * 等待结束
	 */
	public synchronized void waitStop(long timeout) throws InterruptedException {
		long outTime = 0;
		for (;;) {
			switch (this.state) {
			// 以下部分表示处理先于等待结束，于是直接返回
			case FINISHED:
			case ERROR:
			case CANCELED:
				return;// 结束返回
				// 以下表示无效状态
			case SCHEDULING: // 可能造成线程长时间等待，因此不作支持
				throw this.illegalState();
			}
			if (timeout != 0) {
				if (outTime == 0) {
					outTime = System.nanoTime() / 1000000L + timeout;
				} else {
					timeout = outTime - System.nanoTime() / 1000000L;
					if (timeout <= 0) {
						return;// 超时
					}
				}
			}
			this.wait(timeout);
		}
	}

	public final AsyncState getState() {
		return this.state;
	}
}
