package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.DeadLockException;

/**
 * 读写锁请求者
 * 
 * @author Jeff Tang
 * 
 */
abstract class Acquirer<TRes extends Acquirable, TAcquirer extends Acquirer<TRes, TAcquirer>> {

	TAcquirer nextInHolder;

	abstract AcquirerHolder<TAcquirer> getHolder();

	/**
	 * 如果transaction为null判断当前线程是否与事务的当前线程相同，否则判断事务是否相同
	 * 
	 * @param transaction
	 * @return
	 */
	final boolean similarTransaction(TransactionImpl transaction) {
		final TransactionImpl selfTrans = this.getHolder().transaction;
		if (transaction == null) {
			return selfTrans.onCurrentThread();
		} else {
			return selfTrans == transaction;
		}
	}

	// 1.请求共享
	final void share(TRes newRes, long timeout) throws DeadLockException {
		if (newRes == null) {
			throw new NullPointerException();
		}
		if (this.res == newRes) {
			this.share();
			return;
		} else if (this.res != null) {
			this.release();
		}
		this.res = newRes;
		synchronized (newRes) {
			this.toDo(SHARE, timeout);
		}
	}

	// 2.请求可升级的共享
	final void shareUpgradable(TRes newRes, long timeout) {
		if (newRes == null) {
			throw new NullPointerException();
		}
		if (this.res == newRes) {
			this.shareUpgradable();
			return;
		} else if (this.res != null) {
			this.release();
		}
		this.res = newRes;
		synchronized (newRes) {
			this.toDo(SHARE_UPGRADABLE, timeout);
		}
	}

	// 3.独占降级到可升级共享
	private final void shareUpgradable() {
		switch (this.status & MODE_MASK) {
		case SHARE_UPGRADABLE:
			return;
		case EXCLUSIVE:
			synchronized (this.res) {
				this.setMode(SHARE_UPGRADABLE);
				// this.res.acquirer.prev、等待列队的队首
				if (this.res.acquirer.prev != null) {
					this.res.notify();
				}
			}
			return;
		}
		throw new IllegalStateException();
	}

	// 4.独占或可升级共享降级到共享
	private final void share() {
		switch (this.status & MODE_MASK) {
		case SHARE:
			return;
		case EXCLUSIVE:
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				this.setMode(SHARE);
				// this.res.acquirer.prev、等待列队的队首
				if (this.res.acquirer.prev != null) {
					this.res.notify();
				}
			}
			return;
		}
		throw new IllegalStateException();
	}

	// 5.请求独占
	final void exclusive(TRes newRes, long timeout) throws DeadLockException {
		if (newRes == null) {
			throw new NullPointerException();
		}
		if (this.res == newRes) {
			this.exclusive(timeout);
			return;
		} else if (this.res != null) {
			this.release();
		}
		this.res = newRes;
		synchronized (newRes) {
			this.toDo(EXCLUSIVE, timeout);
		}
	}

	// 3.升级共享到独占
	final void exclusive(long timeout) throws DeadLockException {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
			return;
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				@SuppressWarnings("unchecked")
				Acquirer<TRes, TAcquirer> acquirer = this.res.acquirer;
				if (this == acquirer && this.next == null) {// 唯一的占有者
					this.setMode(EXCLUSIVE);
				} else {
					if (this == acquirer) {// 第一个
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// 非第一个
						if (this.next != null) {
							this.next.prev = this.prev;
						}
						this.prev.next = this.next;
					}
					this.next = null;
					this.prev = null;
					this.toDo(SHARE_UPGRADABLE, timeout);
				}
			}
			return;
		}
		throw new IllegalStateException();
	}

	// 5.释放
	final void release() {
		if ((this.status & MODE_MASK) != 0) {
			synchronized (this.res) {
				if (this == this.res.acquirer) {// 第一个
					if (this.next != null) {// 自己不是唯一的占有者
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// 自己是唯一的占有者
						if (this.prev != null) {
							// 等候列队不为空，通知等候列队
							this.res.notify();
						}
						this.res.acquirer = this.prev;
					}
				} else {// 非第一个
					if (this.next != null) {
						this.next.prev = this.prev;
					}
					this.prev.next = this.next;
				}
				this.next = null;
				this.prev = null;
			}
			this.status = 0;
			this.res = null;
		}
	}

	// 使当前所兼容排他锁
	final void toBeCompatibleWithExclusive(long timeout)
			throws DeadLockException {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
			break;
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				@SuppressWarnings("unchecked")
				Acquirer<TRes, TAcquirer> acquirer = this.res.acquirer;
				if (this == acquirer && this.next == null) {// 唯一的占有者
					this.setMode(EXCLUSIVE);
				} else {
					if (this == acquirer) {// 第一个
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// 非第一个
						if (this.next != null) {
							this.next.prev = this.prev;
						}
						this.prev.next = this.next;
					}
					this.next = null;
					this.prev = null;
					this.toDo(SHARE_UPGRADABLE, timeout);
				}
			}
			break;
		case SHARE:
			throw new UnsupportedOperationException("[共享锁]无法升级为[排他锁]");
		default:
			throw new IllegalStateException();
		}
	}

	// 使当前锁兼容可升级共享锁
	final void toBeCompatibleWithShareUpgradable() {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
		case SHARE_UPGRADABLE:
			break;
		case SHARE:
			throw new UnsupportedOperationException("[共享锁]无法升级为[可升级共享锁]");
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * 下一个
	 */
	private Acquirer<TRes, TAcquirer> next;
	/**
	 * 下一个
	 */
	private Acquirer<TRes, TAcquirer> prev;
	/**
	 * 请求状态
	 */
	private int status;
	/**
	 * 请求的资源
	 */
	TRes res;
	// 插队个数
	private static final int INSERTERC_MASK = 3;
	// 共享模式
	private static final int SHARE = 1 << 2;
	// 共享可升级模式
	private static final int SHARE_UPGRADABLE = 2 << 2;
	// 升级中
	private static final int UPGRADING = 3 << 2;
	// 独占模式
	private static final int EXCLUSIVE = 4 << 2;
	// 模式掩码
	private static final int MODE_MASK = 7 << 2;
	// 死锁标记
	private static final int DEADLOCK_MASK = 8 << 2;

	// 设置模式
	private final void setMode(int mode) {
		this.status = (this.status & ~MODE_MASK) | mode;
	}

	final boolean isExclusive() {
		return (this.status & MODE_MASK) == EXCLUSIVE;
	}

	// 测试能否插队
	private final boolean canInsert(Acquirer<TRes, TAcquirer> which) {
		if (which == this) {
			return true;
		}
		int thisMode = this.status & MODE_MASK;
		if (thisMode == UPGRADING) {
			return false;
		}
		int thatMode = which.status & MODE_MASK;
		int inserts;
		if (thisMode == SHARE) {
			if (thatMode == SHARE) {
				return true;
			} else {
				inserts = this.status & INSERTERC_MASK + 1;
			}
		} else {
			inserts = this.status & INSERTERC_MASK
					+ (thatMode == SHARE ? 1 : 2);
		}
		if (inserts >= INSERTERC_MASK) {
			this.status |= INSERTERC_MASK;
			return false;
		} else {
			this.status = (this.status & ~INSERTERC_MASK) | inserts;
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	private void toDo(int mode, long timeout) {
		this.setMode(mode);
		if (this.res.acquirer == null) {
			/*
			 * 没有其它锁定者，所以理论上肯定可以成功。
			 */
			if (this.tryHoldOrPutToWaitings(null, null)) {
				return;
			}
			throw new IllegalStateException();
		}
		Acquirer<TRes, TAcquirer> holdings, waitings;
		for (boolean firstTime = true;;) {
			InterruptedException interrupted = null;
			if (!firstTime) {
				try {
					this.res.wait(timeout);// 等待
				} catch (InterruptedException e) {
					interrupted = e;
				}
			}
			holdings = this.res.acquirer;
			if (holdings.prev != null && holdings.prev.next == holdings) {
				waitings = holdings;
				holdings = null;
			} else {
				waitings = holdings.prev;
			}
			if (firstTime) {
				// 尝试插队，失败则放入等待列队
				if (this.tryHoldOrPutToWaitings(holdings, waitings)) {
					return;
				}
				firstTime = false;
				// 换算成毫秒
			} else if (interrupted != null) {// 出现中断异常
				throw this.handleInterrupted(interrupted, holdings, waitings);
			} else if (this.tryHoldAfterWait(holdings, waitings)) {
				return;
			}
		}
	}

	// 处理中断异常
	private final RuntimeException handleInterrupted(
			InterruptedException interrupted,
			Acquirer<TRes, TAcquirer> holdings,
			Acquirer<TRes, TAcquirer> waitings) {
		boolean deedLock = (this.status & DEADLOCK_MASK) != 0;
		this.status &= ~DEADLOCK_MASK;
		if (this.next != this) {// 自己以外还有等待的结点
			this.res.notify();// 通知下一个
		}
		if ((this.status & MODE_MASK) == UPGRADING) {
			if (this.next != this) {// 自己以外还有等待的结点
				this.prev.next = this.next;
				this.next.prev = this.prev;
				// UPGRADING一定是第一个等待的节点
			} else {
				this.prev = null;
			}
			this.next = holdings;
			if (holdings != null) {
				holdings.prev = this;
			}
			this.res.acquirer = this;
			this.setMode(SHARE_UPGRADABLE);
		} else {
			if (this.next != this) {// 自己以外还有等待的结点
				if (this == waitings) {// 如果自己是第一个等待者
					if (holdings != null) {
						holdings.prev = this.prev;
					} else {
						this.res.acquirer = this.next;
					}
				}
				this.next.prev = this.prev;
				this.prev.next = this.next;
			} else {
				if (holdings != null) {
					holdings.prev = null;
				} else {
					this.res.acquirer = null;
				}
			}
			this.prev = null;
			this.next = null;
			this.status = 0;
			this.res = null;
		}
		if (deedLock) {
			return new DeadLockException();// 抛出死锁异常
		} else {
			return Utils.tryThrowException(interrupted);
		}
	}

	// 尝试插队，成功返回true,失败则放入等待列队返回false
	private final boolean tryHoldOrPutToWaitings(
			Acquirer<TRes, TAcquirer> holdings,
			Acquirer<TRes, TAcquirer> waitings) {
		int mode = this.status & MODE_MASK;
		// 尝试插队
		if ((holdings == null || (mode == SHARE || mode == SHARE_UPGRADABLE)
				&& (holdings.status & MODE_MASK) == SHARE)
				&& (waitings == null || waitings.canInsert(this))) {
			this.prev = waitings;
			if (holdings != null) {
				this.next = holdings;
				holdings.prev = this;
			}
			this.res.acquirer = this;
			if (mode == SHARE_UPGRADABLE) {
				this.setMode(EXCLUSIVE);
			}
			return true;
		}
		// 放入等待列队
		if (waitings == null) {
			this.next = this;
			this.prev = this;
		} else {
			this.next = waitings.next;
			this.prev = waitings;
			waitings.next.prev = this;
			waitings.next = this;
			if (mode != UPGRADING) {
				return false;// 仅放在队尾
			}
		}
		// 放入等待列队的队首
		if (holdings != null) {
			holdings.prev = this;
		} else {
			this.res.acquirer = this;
		}
		return false;
	}

	private final boolean tryHoldAfterWait(Acquirer<TRes, TAcquirer> holdings,
			Acquirer<TRes, TAcquirer> waitings) {
		int mode = this.status & MODE_MASK;
		if ((holdings == null || mode == SHARE
				&& (holdings.status & MODE_MASK) == SHARE)
				&& (waitings == this || waitings.canInsert(this))) {
			if (this.next != this) {// 自己以外还有等待的结点
				this.prev.next = this.next;
				this.next.prev = this.prev;
				if (waitings != this) {// 自己不是第一个等待者
					this.prev = waitings;
				}
				this.res.notify();// 通知下一个
			} else {
				this.prev = null;
			}
			this.next = holdings;
			if (holdings != null) {
				holdings.prev = this;
			}
			this.res.acquirer = this;
			if (mode == UPGRADING) {
				this.setMode(EXCLUSIVE);
			}
			return true;
		} else {
			this.res.notify();// 通知下一个
			return false;
		}
	}
}
