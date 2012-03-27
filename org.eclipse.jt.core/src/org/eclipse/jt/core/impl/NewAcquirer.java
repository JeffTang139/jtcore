package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.TimeoutException;

/**
 * 资源请求者 <br>
 * 
 * <code>
 * <pre>
 * 1. 所有的资源请求者形成一个双向链表
 * 2. tail表示最后的申请者，其prev指针指向更早些得申请者
 * 3. 最后的申请者的prev指针指向null
 * 4. tail的next指针指向最早的等待者，或者空（没有任何人在等待，说明全部都已经获得资源）
 * 
 * +------+ →prev→ +------+ →prev→ ... +------+ →prev→ +------+ →prev→ +------+ →prev→ (null)  
 * | tail |        | wait |            | wait |        | hold |        | hold |  
 * +------+ ←next← +------+ ... ←next← +------+ ←next← +------+ ←next← +------+ 
 *    ↓                                    ↑
 *    +-next---last-wait-nod-or-null-------+  
 *               
 * </pre>
 * </code>
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NewAcquirer<TAcquirable extends NewAcquirable, TAcquirer extends NewAcquirer<TAcquirable, TAcquirer>> {
	/**
	 * 无锁
	 */
	final static byte LOCK_N = 0;
	/**
	 * 共享锁类型标记码
	 */
	final static byte LOCK_MASK_S = 0;
	/**
	 * 可升级锁类型标记码
	 */
	final static byte LOCK_MASK_U = 1;
	/**
	 * 互斥锁类型标记码
	 */
	final static byte LOCK_MASK_X = 2;
	/**
	 * 升级锁升级过程中类型标记码
	 */
	final static byte LOCK_MASK_UX = 3;
	final static byte LOCK_MASK_TYPE = LOCK_MASK_S | LOCK_MASK_U | LOCK_MASK_X
			| LOCK_MASK_UX;
	/**
	 * 远程存根锁类型标记码
	 */
	final static byte LOCK_MASK_STUB = 1 << 2;
	/**
	 * 等待锁类型标记码
	 */
	final static byte LOCK_MASK_WAITING = 1 << 3;
	/**
	 * 准备好的锁
	 */
	final static byte LOCK_MASK_ACQUIRED = 1 << 4;
	/**
	 * 已经荣升为全局锁（拥有远程存根）
	 */
	final static byte LOCK_MASK_GLOBAL = 1 << 5;

	/**
	 * 本地共享锁（等待中）
	 */
	final static byte LOCK_LSW = LOCK_MASK_S | LOCK_MASK_WAITING;
	/**
	 * 本地修改锁（等待中）
	 */
	final static byte LOCK_LUW = LOCK_MASK_U | LOCK_MASK_WAITING;
	/**
	 * 本地独占锁（等待中）
	 */
	final static byte LOCK_LXW = LOCK_MASK_X | LOCK_MASK_WAITING;
	/**
	 * 本地修改锁（等待远程上锁中）
	 */
	final static byte LOCK_LUWR = LOCK_MASK_U | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * 本地独占锁（等待远程上锁中）
	 */
	final static byte LOCK_LXWR = LOCK_MASK_X | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * 远程修改锁（等待中）
	 */
	final static byte LOCK_RUW = LOCK_MASK_U | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * 远程独占锁（等待中）
	 */
	final static byte LOCK_RXW = LOCK_MASK_X | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * 本地修改锁，（等待远程升级锁中）
	 */
	final static byte LOCK_LUXWR = LOCK_MASK_UX | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * 远程修改锁，（等待升级中）
	 */
	final static byte LOCK_RUXW = LOCK_MASK_UX | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * 本地共享锁（已成为全局锁）
	 */
	final static byte LOCK_LS = LOCK_MASK_S | LOCK_MASK_ACQUIRED;
	/**
	 * 本地修改锁（已成为全局锁）
	 */
	final static byte LOCK_LU = LOCK_MASK_U | LOCK_MASK_GLOBAL
			| LOCK_MASK_ACQUIRED;
	/**
	 * 本地独占锁（已成为全局锁）
	 */
	final static byte LOCK_LX = LOCK_MASK_X | LOCK_MASK_GLOBAL
			| LOCK_MASK_ACQUIRED;
	/**
	 * 远程修改锁（已成为全局锁）
	 */
	final static byte LOCK_RU = LOCK_MASK_U | LOCK_MASK_STUB
			| LOCK_MASK_ACQUIRED;
	/**
	 * 远程独占锁（已成为全局锁）
	 */
	final static byte LOCK_RX = LOCK_MASK_X | LOCK_MASK_STUB
			| LOCK_MASK_ACQUIRED;

	final static byte LOCK_ACQUIR_RELEASE = -1;

	/**
	 * 抛出状态异常
	 */
	final static IllegalStateException illegalState(byte state) {
		throw new IllegalStateException("无效锁状态:" + state);
	}

	/**
	 * 优先锁（前一个锁）
	 */
	private NewAcquirer<TAcquirable, TAcquirer> prev;
	/**
	 * 后续锁（下一个锁）
	 */
	private NewAcquirer<TAcquirable, TAcquirer> next;
	/**
	 * 在Holder中的下一个
	 */
	private NewAcquirer<TAcquirable, TAcquirer> nextInHolderHash;

	/**
	 * 返回指定acquirable下的请求者
	 */
	final NewAcquirer<TAcquirable, TAcquirer> findByAcquirable(
			TAcquirable acquirable) {
		NewAcquirer<TAcquirable, TAcquirer> a = this;
		do {
			if (a.res == acquirable) {
				return a;
			}
			a = a.nextInHolderHash;
		} while (a != null);
		return null;
	}

	final NewAcquirer<TAcquirable, TAcquirer> removeByAcquirable(
			TAcquirable acquirable,
			NewAcquirer<TAcquirable, TAcquirer>[] hashTable, int index) {

		if (this.res == acquirable) {
			hashTable[index] = this.nextInHolderHash;
			this.nextInHolderHash = null;
			return this;
		}
		NewAcquirer<TAcquirable, TAcquirer> last = this;
		NewAcquirer<TAcquirable, TAcquirer> a = this.nextInHolderHash;
		do {
			if (a.res == acquirable) {
				last.nextInHolderHash = a.nextInHolderHash;
				a.nextInHolderHash = null;
				return a;
			}
			a = a.nextInHolderHash;
		} while (a != null);
		return null;
	}

	/**
	 * 被锁的资源
	 */
	private TAcquirable res;
	/**
	 * 锁状态
	 */
	private volatile byte lockState;
	/**
	 * 节点确认标记，表示31个节点那些节点已经确认该锁<br>
	 * 初始时，存在的集群节点的状态位被设成0,其余被设成1
	 */
	private int nodeMask;

	final static int NODE_MASK_ALL = -1;

	final TAcquirable getRes() {
		return this.res;
	}

	final NewAcquirer<TAcquirable, TAcquirer> getNext() {
		return this.next;
	}

	final NewAcquirer<TAcquirable, TAcquirer> getPrev() {
		return this.prev;
	}

	/**
	 * 获得请求者的锁类型（状态）
	 */
	public final byte lockState() {
		return this.lockState;
	}

	/**
	 * 返回群集中本地节点的掩码
	 */
	final int localNodeMask() {
		return this.holder.clusterMask;
	}

	/**
	 * 检查锁的拥有线程是否是当前线程
	 */
	private final void checkCurrentThread() {
		// ##2009-12-23##
		if (this.holder.thread != Thread.currentThread()) {
			throw new IllegalStateException("锁的拥有线城必须是当前线程");
		}
		// ##2009-12-23##
	}

	/**
	 * 获取请求者所在的容器。
	 * 
	 * @return 请求者所在的容器。
	 */
	final AcquirerHolder2<TAcquirable, TAcquirer> getHolder() {
		return this.holder;
	}

	/**
	 * 标记本节点已经确认该锁
	 * 
	 * @return 返回是否全局锁成功
	 */
	private final boolean localMarkLocked() {
		return (this.nodeMask |= this.localNodeMask()) == NODE_MASK_ALL;
	}

	/**
	 * 向其他节点发送远程锁请求，只有本地锁可以调用该方法
	 * 
	 * @param lockType
	 *            远程锁类型
	 */
	private final void localPostGlobalLockRequest(final byte lockType) {
		this.postGlobalRequest(lockType);
	}

	void postGlobalRequest(final int lockType) {
		// 只有本地锁需要重写这个方法
	}

	void postGlobalRelease() {
		// 只有本地锁需要重写这个方法
	}

	/**
	 * 向所属的本地锁返还已经锁上的回应，只有远程锁可以调用该方法
	 */
	private final void remotePostLockedRespose() {
		// TODO
	}

	/**
	 * 标记远程上锁成功，只有远程锁可以调用或重写这个方法。
	 */
	void markRemoteLocked() {
		// 只有远程锁可以重写这个方法
	}

	/**
	 * 本地接受远程锁上锁消息，由远程通过remotePostLockedRespose触发。
	 */
	final void localReceiveRemoteLocked(int nodeMask) {
		synchronized (this.res) {
			if ((this.nodeMask |= nodeMask) == NODE_MASK_ALL) {
				Unsf.unsafe.unpark(this.holder.thread);
			}
		}
	}

	/**
	 * 非同步状态下插入本节点
	 * 
	 * @param before
	 *            在此之前插入，如果为null表示插入到最后（尾部）
	 * @param waitHead
	 *            最早的等待者，指定则更新最先等待者
	 * @param lockState
	 *            更新本锁的类型
	 */
	@SuppressWarnings("unchecked")
	private final void nosyncInsertSelfBefore(
			NewAcquirer<TAcquirable, TAcquirer> before,
			NewAcquirer<TAcquirable, TAcquirer> waitHead, byte lockState) {
		// ##2009-12-23##
		if (before != null) {// 有参考的节点
			final NewAcquirer<TAcquirable, TAcquirer> beforePrev = before.prev;
			this.prev = beforePrev;
			this.next = before;
			before.prev = this;
			if (beforePrev != null) {
				beforePrev.next = this;
			}
			if (waitHead != null) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = this.res.tail;
				if (tail != null) {
					tail.next = waitHead;
				}
			}
		} else {
			final TAcquirable res = this.res;
			final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
			// 插入到最开始处
			this.next = waitHead;
			this.prev = tail;
			if (tail != null) {
				tail.next = this;
			}
			res.tail = this;
		}
		this.lockState = lockState;
		// ##2009-12-23##
	}

	/**
	 * 等待所有节点都锁住
	 * 
	 * @param deadline
	 *            超时时间
	 * @throws TimeoutException
	 *             超时异常
	 */
	private final void localWaitLock(long deadline) throws DeadLockException,
			TimeoutException {
		do {
			Unsf.unsafe.park(true, deadline);
			if (Thread.interrupted()) {
				this.release();
				Unsf.unsafe.throwException(new InterruptedException());
			}
			if ((this.lockState & LOCK_MASK_ACQUIRED) != 0) {
				return;
			}
		} while (System.currentTimeMillis() > deadline);
		this.release();
		throw new TimeoutException();
	}

	/**
	 * 本地共享请求<br>
	 */
	@SuppressWarnings("unchecked")
	private final void localAskForShare(long deadline)
			throws DeadLockException, TimeoutException {
		// ##2009-12-18##
		final TAcquirable res = this.res;
		synchronized (res) {
			final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
			final byte tailLockState = tail != null ? tail.lockState : LOCK_N;
			switch (tailLockState) {
			case LOCK_N:// 没有锁
			case LOCK_LS:// 最后的锁是共享锁，那么其余的锁要么没有，要么就都是共享锁
				// 如果没有锁，或者只有本地共享锁，那么请求成功，加入队尾
				this.nosyncInsertSelfBefore(null, null, LOCK_LS);
				// 已经获得本地共享锁，退出
				return;
			case LOCK_LU:// 第一个锁是本地升级锁，后续只可能无锁或共享锁
			case LOCK_RU:// 第一个锁是本地升级锁，后续只可能是无锁或共享锁
				// 插入到升级锁的前面，升级锁依旧是尾部
				this.nosyncInsertSelfBefore(tail, null, LOCK_LS);
				// 已经获得本地共享锁，退出
				return;
			case LOCK_LUWR:
			case LOCK_LUW:
				// 需要放到这些锁的前面，但需要放到X锁的后面
				NewAcquirer<TAcquirable, TAcquirer> u = tail;
				NewAcquirer<TAcquirable, TAcquirer> uPrev = u.prev;
				for (;;) {
					switch (uPrev.lockState) {
					case LOCK_LU:
					case LOCK_LUW:
					case LOCK_LUWR:
					case LOCK_RU:
					case LOCK_RUW:
						u = uPrev;
						uPrev = u.prev;
						continue;
					case LOCK_LS:
						this.nosyncInsertSelfBefore(uPrev, null, LOCK_LS);
						break;
					case LOCK_LXW:
					case LOCK_LXWR:
					case LOCK_LUXWR:
					case LOCK_RXW:
					case LOCK_RUXW:
					case LOCK_LSW:
						this.nosyncInsertSelfBefore(u, null, LOCK_LSW);
						break;
					case LOCK_RX:
					case LOCK_LX:
						this.nosyncInsertSelfBefore(u, this, LOCK_LSW);
						break;
					default:
						throw illegalState(uPrev.lockState);
					}
					break;
				}
				break;
			case LOCK_RUW:
			case LOCK_RXW:
			case LOCK_LSW:
			case LOCK_LXW:
			case LOCK_LXWR:
			case LOCK_LUXWR:
			case LOCK_RUXW:
				// 第一个锁是等待锁，因此只能放到队尾并且更新自己的next指向原来的最先等待节点
				this.nosyncInsertSelfBefore(null, tail.next, LOCK_LSW);
				// 需要进入等待状态
				break;
			case LOCK_LX:
			case LOCK_RX:
				// 第一个是独占锁，因此放入队尾等待
				this.nosyncInsertSelfBefore(null, this, LOCK_LSW);
				// 需要进入等待状态
				break;
			default:
				throw illegalState(tailLockState);
			}
		}
		// 等待本地锁关系解开或者超时
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * 本地可升级请求
	 * 
	 * @param deadline
	 * @throws DeadLockException
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	private final void localAskForUpgrade(long deadline)
			throws DeadLockException, TimeoutException {
		// ##2009-12-18##
		localOrRemote: {
			final TAcquirable res = this.res;
			synchronized (res) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
				final byte tailLockState = tail != null ? tail.lockState
						: LOCK_N;
				switch (tailLockState) {
				case LOCK_N: // 没有任何锁
				case LOCK_LS: // 第一个锁是共享锁，余下的锁要么没有，要么是共享锁
					// 打本节点的标记
					if (this.localMarkLocked()) {
						// 说明没有其它集群节点，直接即可获得锁
						// 加入队尾
						this.nosyncInsertSelfBefore(null, null, LOCK_LU);
						return;// 获得锁直接返回
					} else {
						// 说明还需要等待其它集群节点确认锁
						// 加入队尾，并且等待远程节点处获得锁后的确认
						this.nosyncInsertSelfBefore(null, this, LOCK_LUWR);
						break;
					}
				case LOCK_LU:
				case LOCK_RU:
				case LOCK_LX:
				case LOCK_RX:
					// 如果是与升级锁冲突的锁，则放入队尾，进行本地等待
					// 这里的最先等待锁是自己
					this.nosyncInsertSelfBefore(null, this, LOCK_LUW);
					// 跳过向集群广播
					break localOrRemote;
				case LOCK_LSW:
				case LOCK_LUW:
				case LOCK_LXW:
				case LOCK_LUWR:
				case LOCK_LXWR:
				case LOCK_RUW:
				case LOCK_RXW:
				case LOCK_LUXWR:
				case LOCK_RUXW:
					// 如果是与升级锁冲突的锁，则放入队尾，进行本地等待
					// 这里的最先等待锁是tail.next
					this.nosyncInsertSelfBefore(null, tail.next, LOCK_LUW);
					// 跳过向集群广播
					break localOrRemote;
				default:
					throw illegalState(tailLockState);
				}
			}
			// 向其它集群节点广播远程升级锁请求
			this.localPostGlobalLockRequest(LOCK_RUW);
		}
		// 等待本地的锁关系解开从而获得锁，或者超时
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * 本地独占请求
	 * 
	 * @param deadline
	 * @throws DeadLockException
	 * @throws TimeoutException
	 */
	@SuppressWarnings("unchecked")
	private final void localAskForExclude(long deadline)
			throws DeadLockException, TimeoutException {
		// ##2009-12-18##
		adjAndPostGlobalLockRequest: {
			final TAcquirable res = this.res;
			synchronized (res) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
				if (tail == null) {
					// 如果没有锁
					// 打本节点的标记
					if (this.localMarkLocked()) {
						// 说明没有其它集群节点，直接即可获得锁
						// 加入队尾
						this.nosyncInsertSelfBefore(null, null, LOCK_LX);
						return;// 获得锁直接返回
					} else {
						// 说明还需要等待其它集群节点确认锁
						// 加入队尾
						this.nosyncInsertSelfBefore(null, this, LOCK_LXWR);
						// 需要广播锁确认请求
					}
				} else {
					switch (tail.lockState) {
					case LOCK_LS:
					case LOCK_LU:
					case LOCK_RU:
					case LOCK_LX:
					case LOCK_RX:
						// 之前有锁，直接放入队尾，进行本地等待
						this.nosyncInsertSelfBefore(null, this, LOCK_LXW);
						break adjAndPostGlobalLockRequest;
					case LOCK_LSW:
					case LOCK_LUW:
					case LOCK_LXW:
					case LOCK_LUWR:
					case LOCK_LXWR:
					case LOCK_RUW:
					case LOCK_RXW:
					case LOCK_LUXWR:
					case LOCK_RUXW:
						// 之前有锁，直接放入队尾，进行本地等待，等待尾使用原来的尾
						this.nosyncInsertSelfBefore(null, tail.next, LOCK_LXW);
						break;
					default:
						throw illegalState(tail.lockState);
					}
				}
			}
			// 向其它节点广播远程独占锁请求
			this.localPostGlobalLockRequest(LOCK_RXW);
		}
		// 等待本地的锁关系解开从而获得锁，或者超时
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * 构造并上本地锁
	 * 
	 * @param res
	 *            需要被锁的资源
	 * @param lockType
	 *            要求的本地锁类型
	 * @param deadline
	 *            超时时间（精确到“毫秒”）
	 */
	public NewAcquirer(AcquirerHolder2<TAcquirable, TAcquirer> holder,
			TAcquirable res, byte lockType, long deadline)
			throws DeadLockException, TimeoutException {
		// ##2009-12-18##
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (res == null) {
			throw new NullArgumentException("res");
		}
		// 该节点是本地节点
		this.holder = holder;
		holder.put(this);
		this.res = res;
		switch (lockType) {
		case LOCK_LSW:
			// 本地共享锁请求
			this.localAskForShare(deadline);
			break;
		case LOCK_LUW:
			// 本地可升级锁请求
			this.localAskForUpgrade(deadline);
			break;
		case LOCK_LXW:
			// 本地独占锁请求
			this.localAskForExclude(deadline);
			break;
		default:
			throw illegalState(lockType);
		}
		// ##2009-12-18##
	}

	/**
	 * 为远程节点请求可升级锁
	 */
	@SuppressWarnings("unchecked")
	private final void remoteAskForUpgrade() throws DeadLockException,
			TimeoutException {
		// ##2009-12-21## ！！！
		final TAcquirable res = this.res;
		obtainTheLock: {
			synchronized (res) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
				if (tail == null) {
					// 没有任何锁，获得RU锁
					this.nosyncInsertSelfBefore(null, null, LOCK_RU);
					// 可以回发确认
					break obtainTheLock;
				}
				final int localNodeMask = this.localNodeMask();
				final NewAcquirer<TAcquirable, TAcquirer> waitHead = tail.next;
				NewAcquirer<TAcquirable, TAcquirer> locker = waitHead != null ? waitHead.next
						: tail;
				switch (locker.lockState) {
				case LOCK_LUWR:
					// TODO 这种情况还没有考虑
				case LOCK_LS: // 前面是共享锁，则获得升级锁
					this.nosyncInsertSelfBefore(locker.next, waitHead, LOCK_RU);
					// 成功则回发确认
					break obtainTheLock;
				case LOCK_RU:// 另外的节点发起的升级锁先一步到来，需要比较优先级
					if (locker.localNodeMask() > localNodeMask) {
						// 比locker的优先级高，则抢过了它的锁，locker随即进入等待状态
						locker.lockState = LOCK_RUW;
						this.nosyncInsertSelfBefore(locker, locker, LOCK_RU);
						// 成功则回发确认
						break obtainTheLock;
					} else {
						if (locker == tail) {
							// 已经抵达队尾，
							this.nosyncInsertSelfBefore(null, this, LOCK_RUW);
							// 不回发确认
							return;
						}
						locker = locker.next;// 向后搜索
						break;
					}
				case LOCK_RX:// 另外的节点发起的独占锁先一步到来，应该让出锁
					// RX 锁的优先级 低于RU锁
					locker.lockState = LOCK_RXW;
					this.nosyncInsertSelfBefore(locker, locker, LOCK_RU);
					// 成功则回发确认
					break obtainTheLock;
				default:
					throw illegalState(locker.lockState);
				}
				for (;;) {
					switch (locker.lockState) {
					case LOCK_LUWR:// 本节点发起的升级锁先一步等待远程，需要比较优先级
					case LOCK_RUW:// 另外的节点发起的升级锁先一步到来，需要比较优先级
						if (locker.localNodeMask() > localNodeMask) {
							// 比locker的优先级高，则插入在其前面，需要检查是否locker就是先前的最先等待锁
							this.nosyncInsertSelfBefore(locker,
									waitHead == locker ? this : waitHead,
									LOCK_RUW);
							// 不回发确认
							return;
						} else {
							if (locker == tail) {
								// 已经抵达队尾，且之前有等待的锁waitHead
								this.nosyncInsertSelfBefore(null, waitHead,
										LOCK_RUW);
								return;
							}
							locker = locker.next;// 向后搜索
							continue;
						}
					case LOCK_LXW: // 遇到本地等待锁则停止
					case LOCK_LUW:// 遇到本地等待锁则停止
					case LOCK_LSW:// 遇到本地等待锁则停止
					case LOCK_LXWR:// 本节点发起的独占锁先一步等待远程
					case LOCK_RXW:// 另外的节点发起的独占锁先一步到来
						// X 锁的优先级 低于U锁，需要检查是否locker就是先前的最先等待锁
						this.nosyncInsertSelfBefore(locker,
								waitHead == locker ? this : waitHead, LOCK_RUW);
						return;
					default:
						throw illegalState(locker.lockState);
					}
				}
			}
		}
		this.remotePostLockedRespose();
		// ##2009-12-21##
	}

	/**
	 * 为远程节点请求独占锁
	 */
	@SuppressWarnings("unchecked")
	private final void remoteAskForExclude() throws DeadLockException,
			TimeoutException {
		// ##2009-12-21##
		final TAcquirable res = this.res;
		obtainTheLock: {
			synchronized (res) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
				if (tail == null) {
					// 没有任何锁，获得RX锁
					this.nosyncInsertSelfBefore(null, null, LOCK_RX);
					// 可以回发确认
					break obtainTheLock;
				}
				final int localNodeMask = this.localNodeMask();
				final NewAcquirer<TAcquirable, TAcquirer> waitHead = tail.next;
				NewAcquirer<TAcquirable, TAcquirer> locker = waitHead != null ? waitHead.next
						: tail;
				switch (locker.lockState) {
				case LOCK_RX:// 另外的节点发起的独占锁先一步到来，需要比较优先级
					if (locker.localNodeMask() > localNodeMask) {
						// 比locker的优先级高，则抢过了它的锁，locker随即进入等待状态
						locker.lockState = LOCK_RXW;
						this.nosyncInsertSelfBefore(locker, locker, LOCK_RX);
						// 成功则回发确认
						break obtainTheLock;
					} else {
						if (locker == tail) {
							// 已经抵达队尾，
							this.nosyncInsertSelfBefore(null, this, LOCK_RXW);
							// 不回发确认
							return;
						}
						locker = locker.next;// 向后搜索
						break;
					}
				case LOCK_RU:// 另外的节点发起的升级锁先一步到来，RX锁比RU锁优先级低
					// RX 锁的优先级 低于RU锁
					if (locker == tail) {
						// 已经抵达队尾，
						this.nosyncInsertSelfBefore(null, this, LOCK_RXW);
						// 不回发确认
						return;
					}
					locker = locker.next;// 向后搜索
					break;
				default:
					throw illegalState(locker.lockState);
				}
				for (;;) {
					switch (locker.lockState) {
					case LOCK_LXWR:// 本节点发起的独占锁先一步等待远程，需要比较优先级
					case LOCK_RXW:// 另外的节点发起的独占锁先一步到来，需要比较优先级
						if (locker.localNodeMask() > localNodeMask) {
							// 比locker的优先级高，则插入在其前面
							this.nosyncInsertSelfBefore(locker,
									waitHead == locker ? this : waitHead,
									LOCK_RXW);
							// 不回发确认
							return;
						} else {
							if (locker == tail) {
								// 已经抵达队尾，
								this.nosyncInsertSelfBefore(null, waitHead,
										LOCK_RXW);
								return;
							}
							locker = locker.next;// 向后搜索
							continue;
						}
					case LOCK_LUWR:// 本节点发起的独占锁先一步等待远程
					case LOCK_RUW:// 另外的节点发起的独占锁先一步到来
						// X 锁的优先级 低于U锁
						if (locker == tail) {
							// 已经抵达队尾，
							this.nosyncInsertSelfBefore(null, waitHead,
									LOCK_RXW);
							return;
						}
						locker = locker.next;// 向后搜索
						continue;
					case LOCK_LXW: // 遇到本地等待锁则停止
					case LOCK_LUW:// 遇到本地等待锁则停止
					case LOCK_LSW:// 遇到本地等待锁则停止
						// 抵达需要停止的地方
						this.nosyncInsertSelfBefore(locker,
								waitHead == locker ? this : waitHead, LOCK_RXW);
						return;
					default:
						throw illegalState(locker.lockState);
					}
				}
			}
		}
		this.remotePostLockedRespose();
		// ##2009-12-21##
	}

	/**
	 * 构造并上远程锁
	 * 
	 * @param res
	 *            需要被锁的资源
	 * @param lockType
	 *            要求的本地锁类型
	 */
	NewAcquirer(AcquirerHolder2<TAcquirable, TAcquirer> holder,
			TAcquirable res, byte lockType) {
		// ##2009-12-18##
		// 该节点是远程节点
		if (holder == null) {
			throw new NullArgumentException("holder");
		}
		if (res == null) {
			throw new NullArgumentException("res");
		}
		this.holder = holder;
		holder.put(this);
		this.res = res;
		switch (lockType) {
		case LOCK_RUW:
			this.remoteAskForUpgrade();
			break;
		case LOCK_RXW:
			this.remoteAskForExclude();
			break;
		default:
			throw illegalState(lockType);
		}
		// ##2009-12-18##
	}

	/**
	 * 从RU锁升级成RX锁的请求
	 */
	public final void remoteUpgrade() {
		// ##2009-12-23##
		synchronized (this.res) {
			if (this.lockState != LOCK_RU) {
				throw new IllegalStateException("非远程更新锁不能升级到远程独占锁");
			}
			if (this.prev == null) {
				// 如果远程升级锁的前面没有锁了（S锁）则可以升级
				this.lockState = LOCK_RX;
			} else {
				// 否则需要等待
				this.lockState = LOCK_RUXW;
				return;
			}
		}
		// 远程节点回发锁确认消息
		this.remotePostLockedRespose();
		// ##2009-12-23##
	}

	/**
	 * 从LU锁升级成LX锁
	 */
	public final void localUpgrade(long deadline) throws TimeoutException {
		// ##2009-12-23##
		// 只有当前线程才能改变本地锁的状态
		this.checkCurrentThread();
		synchronized (this.res) {
			if (this.lockState != LOCK_LU) {
				throw new IllegalStateException("非本地更新锁不能升级到本地独占锁");
			}
			if (this.prev == null && this.localMarkLocked()) {
				// 如果不依赖其他的节点的确认，则升级成功，直接返回
				this.lockState = LOCK_LX;
				return;
			} else {
				// 否则等待
				this.lockState = LOCK_LUXWR;
			}
		}
		// 本地节点广播锁确认请求消息
		this.localPostGlobalLockRequest(LOCK_RUXW);
		// 进入等待
		this.localWaitLock(deadline);
		// ##2009-12-23##
	}

	/**
	 * 释放本地锁
	 */
	public void localRelease() {
		this.checkCurrentThread();

		// TODO
	}

	/**
	 * 远程锁释放
	 */
	public void remoteRelease() {
		// TODO
	}

	private final void wakeup(byte lockState) {
		this.lockState = lockState;
		Unsf.unsafe.unpark(this.holder.thread);
	}

	/**
	 * 锁的释放由以下几种情况引发:<br>
	 * 1. 正常释放。相关的锁有LS/LU/LX锁。其中LX/LU锁在释放后还需要通知其他节点上的锁存根释放。<br>
	 * 2. 等待超时。相关锁涉及全部本地等待锁包括:LSW/LUW/LXW/LUWR/LXWR/LUXWR。
	 * 其中LUWR/LXWR/LUXWR锁在释放后还需要通知其他节点上的锁存根释放。<br>
	 * 3. 锁存根释放。当锁释放或超时时，或其属地集群节点失效后，会通知其存根释放。
	 * 相关的锁包括RUW/RU/RXW/RX/RUXW。这些锁释放后不需要额外的动作。<br>
	 * 锁释放时会有三类影响<br>
	 * 1. 如果是全局锁（或是已经获得锁，或是正在等待确认中）则需要向其他节点发送撤销锁的消息<br>
	 * 2. 如果后续的锁依据锁规则，改变了锁状态，当变为全局锁时需要向其他节点发送请求锁的消息<br>
	 * 3. 如果是存根锁获得了锁，则需要向其源节点发送锁确认消息。
	 */
	public final void release() {
		final TAcquirable res = this.res;
		// 如果后续的锁依据锁规则，改变了锁状态，当变为全局锁时需要向其他节点发送请求锁的消息
		// 下面变量表示所请求锁的类型LOCK_N表示没有请求者，LOCK_ACQUIR_RELEASE表示仅请求撤销现有锁
		byte acquire_lock_type = LOCK_N;
		// 如果是存根锁获得了锁，则需要向其源节点发送锁确认消息
		// 下面的变量表示需要发送锁确认消息的源
		AcquirerHolder2<?, ?> feedBackTo = null;
		synchronized (res) {
			if ((this.lockState & LOCK_MASK_GLOBAL) != 0) {
				acquire_lock_type = LOCK_ACQUIR_RELEASE;
			}
			NewAcquirer<TAcquirable, TAcquirer> next = this.next;
			NewAcquirer<TAcquirable, TAcquirer> prev = this.prev;
			this.prev = null;
			this.next = null;
			this.lockState = LOCK_N;
			@SuppressWarnings("unchecked")
			final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
			if (prev != null) {
				prev.next = next;
			}
			if (tail == this) {
				res.tail = prev;
			} else {
				next.prev = prev;
				switch (next.lockState) {
				case LOCK_LS:
				case LOCK_RU:
				case LOCK_LU:
				case LOCK_RX:
				case LOCK_LX:
					// 已经获得锁，不采取任何动作
				case LOCK_LXWR:
				case LOCK_LUWR:
				case LOCK_LUXWR:
					// 等待远程锁者由远程存根发起的确认信息驱动
					break;
				case LOCK_RXW:
				case LOCK_RUXW:
					if (prev == null) {
						next.lockState = LOCK_RX;
						feedBackTo = next.holder;
					}
					break;
				case LOCK_RUW:
					if (prev == null || prev.lockState == LOCK_LS) {
						next.lockState = LOCK_RU;
						feedBackTo = next.holder;
					}
					break;
				case LOCK_LXW:
					if (prev == null) {
						acquire_lock_type = next.lockState = LOCK_LXWR;
					}
					break;
				case LOCK_LUW:
					if (prev == null || prev.lockState == LOCK_LS) {
						acquire_lock_type = next.lockState = LOCK_LUWR;
					}
					break;
				case LOCK_LSW:

					// 最后一个U锁
					final NewAcquirer<TAcquirable, TAcquirer> lastU;
					if (prev == null || prev.lockState == LOCK_LS) {
						lastU = null;
					} else if ((prev.lockState & LOCK_MASK_TYPE) == LOCK_MASK_U) {
						lastU = prev;
					} else {
						break;
					}
					// 第一个U锁
					NewAcquirer<TAcquirable, TAcquirer> firstU = lastU;
					// 是否能获得S锁
					final boolean sLockAcquired;
					if (lastU == null) {
						sLockAcquired = true;
					} else {
						NewAcquirer<TAcquirable, TAcquirer> firstUPrev = firstU.prev;
						while (firstUPrev != null
								&& (firstUPrev.lockState & LOCK_MASK_TYPE) == LOCK_MASK_U) {
							firstU = firstUPrev;
							firstUPrev = firstU.prev;
						}
						// 第一个S锁
						final NewAcquirer<TAcquirable, TAcquirer> firstS = next;
						firstS.prev = firstUPrev;
						if (firstUPrev != null) {
							firstUPrev.next = firstS;
							sLockAcquired = firstUPrev.lockState == LOCK_LS;
						} else {
							sLockAcquired = true;
						}
					}
					for (NewAcquirer<TAcquirable, TAcquirer> lastS = next;; lastS = next) {
						if (sLockAcquired) {
							lastS.wakeup(LOCK_LS);
						}
						if (lastS == tail) {
							// 到达尾部
							if (lastU != null) {
								res.tail = lastU;
								lastU.next = null;
							} else {
								tail.next = null;
							}
						} else {
							next = next.next;
							switch (next.lockState) {
							case LOCK_LSW:
								continue;
							case LOCK_LUW:
								if (lastU == null) {
									acquire_lock_type = next.lockState = LOCK_LUWR;
								}
							default:
								tail.next = next;
							}
						}
						if (firstU != null) {
							firstU.prev = lastS;
							lastS.next = firstU;
						}
						break;// 终止循环
					}
					break;
				default:
					throw illegalState(next.lockState);
				}
			}
		}
		if (feedBackTo != null) {
			// TODO 回发锁确认通知
		}
		if (acquire_lock_type != LOCK_N) {
			// TODO 广播锁确认或撤销请求
		}
		this.holder.remove(res);
		this.res = null;
	}

	// 使当前所兼容排他锁
	final void toBeCompatibleWithExclusive(long deadline)
			throws DeadLockException {
		// ##2009-12-23##
		// 只有当前线程才能改变本地锁的状态
		this.checkCurrentThread();
		switch (this.lockState) {
		case LOCK_LX:
			break;
		case LOCK_LU:
			synchronized (this.res) {
				if (this.prev == null && this.localMarkLocked()) {
					// 如果不依赖其他的节点的确认，则升级成功，直接返回
					this.lockState = LOCK_LX;
					return;
				} else {
					// 否则等待
					this.lockState = LOCK_LUXWR;
				}
			}
			break;
		case LOCK_LS:
			throw new UnsupportedOperationException("[共享锁]无法升级为[排他锁]");
		default:
			throw new IllegalStateException("无效锁状态：" + this.lockState);
		}
		// 本地节点广播锁确认请求消息
		this.localPostGlobalLockRequest(LOCK_RUXW);
		// 进入等待
		this.localWaitLock(deadline);
		// ##2009-12-23##
	}

	// 使当前锁兼容可升级共享锁
	final void toBeCompatibleWithShareUpgradable() {
		// ##2009-12-23##
		// 只有当前线程才能改变本地锁的状态
		this.checkCurrentThread();
		switch (this.lockState) {
		case LOCK_LU:
		case LOCK_LX:
			break;
		case LOCK_LS:
			throw new UnsupportedOperationException("[共享锁]无法升级为[可升级共享锁]");
		default:
			throw new IllegalStateException("无效锁状态：" + this.lockState);
		}
		// ##2009-12-23##
	}

	// /////////////////////////////////////////////////////////////////////////
	// Holder相关
	// /////////////////////////////////////////////////////////////////////////
	static class AcquirerHolder2<TAcquirable extends NewAcquirable, TAcquirer extends NewAcquirer<TAcquirable, TAcquirer>> {
		private int size;
		private NewAcquirer<TAcquirable, TAcquirer>[] hashTable;
		final int clusterMask;
		final Thread thread;

		AcquirerHolder2(Thread thread, int clusterMask) {
			this.thread = thread;
			this.clusterMask = clusterMask;
		}

		@SuppressWarnings("unchecked")
		private void put(NewAcquirer<TAcquirable, TAcquirer> acquirer) {
			int l;
			int h;
			NewAcquirer<TAcquirable, TAcquirer>[] hashTable = this.hashTable;
			if (hashTable == null) {
				this.hashTable = hashTable = new NewAcquirer[l = 8];
				h = l - 1;
			} else {
				l = hashTable.length;
				if (l < this.size * 2) {
					hashTable = new NewAcquirer[l <<= 1];
					h = l - 1;
					for (NewAcquirer<TAcquirable, TAcquirer> a : this.hashTable) {
						while (a != null) {
							NewAcquirer<TAcquirable, TAcquirer> next = a.nextInHolderHash;
							int i = a.res.hashCode() & h;
							a.nextInHolderHash = hashTable[i];
							hashTable[i] = a;
							a = next;
						}
					}
					this.hashTable = hashTable;
				} else {
					h = l - 1;
				}
			}
			int index = acquirer.res.hashCode() & h;
			acquirer.nextInHolderHash = hashTable[index];
			hashTable[index] = acquirer;
		}

		public NewAcquirer<TAcquirable, TAcquirer> find(NewAcquirable acquirable) {
			for (NewAcquirer<TAcquirable, TAcquirer> a = this.hashTable[acquirable
					.hashCode()
					& (this.hashTable.length - 1)]; a != null; a = a.nextInHolderHash) {
				if (a.res == acquirable) {
					return a;
				}
			}
			return null;
		}

		private void remove(NewAcquirable acquirable) {
			if (this.size == 0) {
				return;
			}
			final int index = acquirable.hashCode()
					& (this.hashTable.length - 1);
			for (NewAcquirer<TAcquirable, TAcquirer> a = this.hashTable[index], last = null; a != null; last = a, a = a.nextInHolderHash) {
				if (a.res == acquirable) {
					if (last == null) {
						this.hashTable[index] = a.nextInHolderHash;
					} else {
						last.nextInHolderHash = a.nextInHolderHash;
					}
					a.nextInHolderHash = null;
					this.size--;
					break;
				}
			}
		}
	}

	final AcquirerHolder2<TAcquirable, TAcquirer> holder;
}
