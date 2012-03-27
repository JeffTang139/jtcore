package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.DeadLockException;

/**
 * ��д��������
 * 
 * @author Jeff Tang
 * 
 */
abstract class Acquirer<TRes extends Acquirable, TAcquirer extends Acquirer<TRes, TAcquirer>> {

	TAcquirer nextInHolder;

	abstract AcquirerHolder<TAcquirer> getHolder();

	/**
	 * ���transactionΪnull�жϵ�ǰ�߳��Ƿ�������ĵ�ǰ�߳���ͬ�������ж������Ƿ���ͬ
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

	// 1.������
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

	// 2.����������Ĺ���
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

	// 3.��ռ����������������
	private final void shareUpgradable() {
		switch (this.status & MODE_MASK) {
		case SHARE_UPGRADABLE:
			return;
		case EXCLUSIVE:
			synchronized (this.res) {
				this.setMode(SHARE_UPGRADABLE);
				// this.res.acquirer.prev���ȴ��жӵĶ���
				if (this.res.acquirer.prev != null) {
					this.res.notify();
				}
			}
			return;
		}
		throw new IllegalStateException();
	}

	// 4.��ռ�������������������
	private final void share() {
		switch (this.status & MODE_MASK) {
		case SHARE:
			return;
		case EXCLUSIVE:
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				this.setMode(SHARE);
				// this.res.acquirer.prev���ȴ��жӵĶ���
				if (this.res.acquirer.prev != null) {
					this.res.notify();
				}
			}
			return;
		}
		throw new IllegalStateException();
	}

	// 5.�����ռ
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

	// 3.����������ռ
	final void exclusive(long timeout) throws DeadLockException {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
			return;
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				@SuppressWarnings("unchecked")
				Acquirer<TRes, TAcquirer> acquirer = this.res.acquirer;
				if (this == acquirer && this.next == null) {// Ψһ��ռ����
					this.setMode(EXCLUSIVE);
				} else {
					if (this == acquirer) {// ��һ��
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// �ǵ�һ��
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

	// 5.�ͷ�
	final void release() {
		if ((this.status & MODE_MASK) != 0) {
			synchronized (this.res) {
				if (this == this.res.acquirer) {// ��һ��
					if (this.next != null) {// �Լ�����Ψһ��ռ����
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// �Լ���Ψһ��ռ����
						if (this.prev != null) {
							// �Ⱥ��жӲ�Ϊ�գ�֪ͨ�Ⱥ��ж�
							this.res.notify();
						}
						this.res.acquirer = this.prev;
					}
				} else {// �ǵ�һ��
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

	// ʹ��ǰ������������
	final void toBeCompatibleWithExclusive(long timeout)
			throws DeadLockException {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
			break;
		case SHARE_UPGRADABLE:
			synchronized (this.res) {
				@SuppressWarnings("unchecked")
				Acquirer<TRes, TAcquirer> acquirer = this.res.acquirer;
				if (this == acquirer && this.next == null) {// Ψһ��ռ����
					this.setMode(EXCLUSIVE);
				} else {
					if (this == acquirer) {// ��һ��
						this.next.prev = this.prev;
						this.res.acquirer = this.next;
					} else {// �ǵ�һ��
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
			throw new UnsupportedOperationException("[������]�޷�����Ϊ[������]");
		default:
			throw new IllegalStateException();
		}
	}

	// ʹ��ǰ�����ݿ�����������
	final void toBeCompatibleWithShareUpgradable() {
		switch (this.status & MODE_MASK) {
		case EXCLUSIVE:
		case SHARE_UPGRADABLE:
			break;
		case SHARE:
			throw new UnsupportedOperationException("[������]�޷�����Ϊ[������������]");
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * ��һ��
	 */
	private Acquirer<TRes, TAcquirer> next;
	/**
	 * ��һ��
	 */
	private Acquirer<TRes, TAcquirer> prev;
	/**
	 * ����״̬
	 */
	private int status;
	/**
	 * �������Դ
	 */
	TRes res;
	// ��Ӹ���
	private static final int INSERTERC_MASK = 3;
	// ����ģʽ
	private static final int SHARE = 1 << 2;
	// ���������ģʽ
	private static final int SHARE_UPGRADABLE = 2 << 2;
	// ������
	private static final int UPGRADING = 3 << 2;
	// ��ռģʽ
	private static final int EXCLUSIVE = 4 << 2;
	// ģʽ����
	private static final int MODE_MASK = 7 << 2;
	// �������
	private static final int DEADLOCK_MASK = 8 << 2;

	// ����ģʽ
	private final void setMode(int mode) {
		this.status = (this.status & ~MODE_MASK) | mode;
	}

	final boolean isExclusive() {
		return (this.status & MODE_MASK) == EXCLUSIVE;
	}

	// �����ܷ���
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
			 * û�����������ߣ����������Ͽ϶����Գɹ���
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
					this.res.wait(timeout);// �ȴ�
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
				// ���Բ�ӣ�ʧ�������ȴ��ж�
				if (this.tryHoldOrPutToWaitings(holdings, waitings)) {
					return;
				}
				firstTime = false;
				// ����ɺ���
			} else if (interrupted != null) {// �����ж��쳣
				throw this.handleInterrupted(interrupted, holdings, waitings);
			} else if (this.tryHoldAfterWait(holdings, waitings)) {
				return;
			}
		}
	}

	// �����ж��쳣
	private final RuntimeException handleInterrupted(
			InterruptedException interrupted,
			Acquirer<TRes, TAcquirer> holdings,
			Acquirer<TRes, TAcquirer> waitings) {
		boolean deedLock = (this.status & DEADLOCK_MASK) != 0;
		this.status &= ~DEADLOCK_MASK;
		if (this.next != this) {// �Լ����⻹�еȴ��Ľ��
			this.res.notify();// ֪ͨ��һ��
		}
		if ((this.status & MODE_MASK) == UPGRADING) {
			if (this.next != this) {// �Լ����⻹�еȴ��Ľ��
				this.prev.next = this.next;
				this.next.prev = this.prev;
				// UPGRADINGһ���ǵ�һ���ȴ��Ľڵ�
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
			if (this.next != this) {// �Լ����⻹�еȴ��Ľ��
				if (this == waitings) {// ����Լ��ǵ�һ���ȴ���
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
			return new DeadLockException();// �׳������쳣
		} else {
			return Utils.tryThrowException(interrupted);
		}
	}

	// ���Բ�ӣ��ɹ�����true,ʧ�������ȴ��жӷ���false
	private final boolean tryHoldOrPutToWaitings(
			Acquirer<TRes, TAcquirer> holdings,
			Acquirer<TRes, TAcquirer> waitings) {
		int mode = this.status & MODE_MASK;
		// ���Բ��
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
		// ����ȴ��ж�
		if (waitings == null) {
			this.next = this;
			this.prev = this;
		} else {
			this.next = waitings.next;
			this.prev = waitings;
			waitings.next.prev = this;
			waitings.next = this;
			if (mode != UPGRADING) {
				return false;// �����ڶ�β
			}
		}
		// ����ȴ��жӵĶ���
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
			if (this.next != this) {// �Լ����⻹�еȴ��Ľ��
				this.prev.next = this.next;
				this.next.prev = this.prev;
				if (waitings != this) {// �Լ����ǵ�һ���ȴ���
					this.prev = waitings;
				}
				this.res.notify();// ֪ͨ��һ��
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
			this.res.notify();// ֪ͨ��һ��
			return false;
		}
	}
}
