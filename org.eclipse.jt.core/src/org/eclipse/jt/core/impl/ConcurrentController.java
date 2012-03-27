package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 并发控制器
 * 
 * @author Jeff Tang
 * 
 */
final class ConcurrentController {
	/**
	 * 并发限制
	 */
	private final int permits;
	/**
	 * 并发数量
	 */
	private int concurrings;
	/**
	 * 列队
	 */
	final ConcurrentWorkQueue workqueue;

	/**
	 * 列队键
	 */
	ConcurrentController(ConcurrentWorkQueue workqueue, int permits) {
		if (permits <= 0) {
			throw new IllegalArgumentException("permits must > 0");
		}
		if (workqueue == null) {
			throw new NullArgumentException("workqueue");
		}
		this.workqueue = workqueue;
		this.permits = permits;
	}

	/**
	 * 进入并发<br>
	 * 返回true表示工作可以开始，<br>
	 * 否则表示工作开始排队。工作不需要开始
	 * 
	 * @param work
	 *            工作
	 */
	final boolean enterScope(Work work) {
		// 所有控制器的状态以其列队为锁
		synchronized (this.workqueue) {
			if (this.workqueue.isEmpty() && this.concurrings < this.permits) {
				this.concurrings++;
				return true;
			}
			this.workqueue.put(work);
			return false;
		}
	}

	/**
	 * 离开并发<br>
	 * 返回非空值表示还需要继续处理work，但不用调用enter(OverlappedWork)<br>
	 * 返回空值表示没有等待的工作处理了
	 */
	final void leaveScope(WorkingManager manager) {
		// 所有控制器的状态以其列队为锁
		synchronized (this.workqueue) {
			this.concurrings--;
			for (Work work = this.workqueue.poll(); work != null; work = this.workqueue
			        .poll()) {
				ConcurrentController ccr = work.getConcurrentController();
				// 在列队中的工作的ccr不可能为空
				if (ccr.concurrings < ccr.permits) {
					// 当前线程与工作的提交线程不同，需要锁定工作
					synchronized (work) {
						if (manager.startWork(work)) {
							ccr.concurrings++;
						}
					}
				} else {
					// 达到并发限制，停止提交工作
					break;
				}
			}
		}
	}
}
