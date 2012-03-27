package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.TimeoutException;

/**
 * ��Դ������ <br>
 * 
 * <code>
 * <pre>
 * 1. ���е���Դ�������γ�һ��˫������
 * 2. tail��ʾ���������ߣ���prevָ��ָ�����Щ��������
 * 3. ���������ߵ�prevָ��ָ��null
 * 4. tail��nextָ��ָ������ĵȴ��ߣ����߿գ�û���κ����ڵȴ���˵��ȫ�����Ѿ������Դ��
 * 
 * +------+ ��prev�� +------+ ��prev�� ... +------+ ��prev�� +------+ ��prev�� +------+ ��prev�� (null)  
 * | tail |        | wait |            | wait |        | hold |        | hold |  
 * +------+ ��next�� +------+ ... ��next�� +------+ ��next�� +------+ ��next�� +------+ 
 *    ��                                    ��
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
	 * ����
	 */
	final static byte LOCK_N = 0;
	/**
	 * ���������ͱ����
	 */
	final static byte LOCK_MASK_S = 0;
	/**
	 * �����������ͱ����
	 */
	final static byte LOCK_MASK_U = 1;
	/**
	 * ���������ͱ����
	 */
	final static byte LOCK_MASK_X = 2;
	/**
	 * �������������������ͱ����
	 */
	final static byte LOCK_MASK_UX = 3;
	final static byte LOCK_MASK_TYPE = LOCK_MASK_S | LOCK_MASK_U | LOCK_MASK_X
			| LOCK_MASK_UX;
	/**
	 * Զ�̴�������ͱ����
	 */
	final static byte LOCK_MASK_STUB = 1 << 2;
	/**
	 * �ȴ������ͱ����
	 */
	final static byte LOCK_MASK_WAITING = 1 << 3;
	/**
	 * ׼���õ���
	 */
	final static byte LOCK_MASK_ACQUIRED = 1 << 4;
	/**
	 * �Ѿ�����Ϊȫ������ӵ��Զ�̴����
	 */
	final static byte LOCK_MASK_GLOBAL = 1 << 5;

	/**
	 * ���ع��������ȴ��У�
	 */
	final static byte LOCK_LSW = LOCK_MASK_S | LOCK_MASK_WAITING;
	/**
	 * �����޸������ȴ��У�
	 */
	final static byte LOCK_LUW = LOCK_MASK_U | LOCK_MASK_WAITING;
	/**
	 * ���ض�ռ�����ȴ��У�
	 */
	final static byte LOCK_LXW = LOCK_MASK_X | LOCK_MASK_WAITING;
	/**
	 * �����޸������ȴ�Զ�������У�
	 */
	final static byte LOCK_LUWR = LOCK_MASK_U | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * ���ض�ռ�����ȴ�Զ�������У�
	 */
	final static byte LOCK_LXWR = LOCK_MASK_X | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * Զ���޸������ȴ��У�
	 */
	final static byte LOCK_RUW = LOCK_MASK_U | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * Զ�̶�ռ�����ȴ��У�
	 */
	final static byte LOCK_RXW = LOCK_MASK_X | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * �����޸��������ȴ�Զ���������У�
	 */
	final static byte LOCK_LUXWR = LOCK_MASK_UX | LOCK_MASK_WAITING
			| LOCK_MASK_GLOBAL;
	/**
	 * Զ���޸��������ȴ������У�
	 */
	final static byte LOCK_RUXW = LOCK_MASK_UX | LOCK_MASK_STUB
			| LOCK_MASK_WAITING;
	/**
	 * ���ع��������ѳ�Ϊȫ������
	 */
	final static byte LOCK_LS = LOCK_MASK_S | LOCK_MASK_ACQUIRED;
	/**
	 * �����޸������ѳ�Ϊȫ������
	 */
	final static byte LOCK_LU = LOCK_MASK_U | LOCK_MASK_GLOBAL
			| LOCK_MASK_ACQUIRED;
	/**
	 * ���ض�ռ�����ѳ�Ϊȫ������
	 */
	final static byte LOCK_LX = LOCK_MASK_X | LOCK_MASK_GLOBAL
			| LOCK_MASK_ACQUIRED;
	/**
	 * Զ���޸������ѳ�Ϊȫ������
	 */
	final static byte LOCK_RU = LOCK_MASK_U | LOCK_MASK_STUB
			| LOCK_MASK_ACQUIRED;
	/**
	 * Զ�̶�ռ�����ѳ�Ϊȫ������
	 */
	final static byte LOCK_RX = LOCK_MASK_X | LOCK_MASK_STUB
			| LOCK_MASK_ACQUIRED;

	final static byte LOCK_ACQUIR_RELEASE = -1;

	/**
	 * �׳�״̬�쳣
	 */
	final static IllegalStateException illegalState(byte state) {
		throw new IllegalStateException("��Ч��״̬:" + state);
	}

	/**
	 * ��������ǰһ������
	 */
	private NewAcquirer<TAcquirable, TAcquirer> prev;
	/**
	 * ����������һ������
	 */
	private NewAcquirer<TAcquirable, TAcquirer> next;
	/**
	 * ��Holder�е���һ��
	 */
	private NewAcquirer<TAcquirable, TAcquirer> nextInHolderHash;

	/**
	 * ����ָ��acquirable�µ�������
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
	 * ��������Դ
	 */
	private TAcquirable res;
	/**
	 * ��״̬
	 */
	private volatile byte lockState;
	/**
	 * �ڵ�ȷ�ϱ�ǣ���ʾ31���ڵ���Щ�ڵ��Ѿ�ȷ�ϸ���<br>
	 * ��ʼʱ�����ڵļ�Ⱥ�ڵ��״̬λ�����0,���౻���1
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
	 * ��������ߵ������ͣ�״̬��
	 */
	public final byte lockState() {
		return this.lockState;
	}

	/**
	 * ����Ⱥ���б��ؽڵ������
	 */
	final int localNodeMask() {
		return this.holder.clusterMask;
	}

	/**
	 * �������ӵ���߳��Ƿ��ǵ�ǰ�߳�
	 */
	private final void checkCurrentThread() {
		// ##2009-12-23##
		if (this.holder.thread != Thread.currentThread()) {
			throw new IllegalStateException("����ӵ���߳Ǳ����ǵ�ǰ�߳�");
		}
		// ##2009-12-23##
	}

	/**
	 * ��ȡ���������ڵ�������
	 * 
	 * @return ���������ڵ�������
	 */
	final AcquirerHolder2<TAcquirable, TAcquirer> getHolder() {
		return this.holder;
	}

	/**
	 * ��Ǳ��ڵ��Ѿ�ȷ�ϸ���
	 * 
	 * @return �����Ƿ�ȫ�����ɹ�
	 */
	private final boolean localMarkLocked() {
		return (this.nodeMask |= this.localNodeMask()) == NODE_MASK_ALL;
	}

	/**
	 * �������ڵ㷢��Զ��������ֻ�б��������Ե��ø÷���
	 * 
	 * @param lockType
	 *            Զ��������
	 */
	private final void localPostGlobalLockRequest(final byte lockType) {
		this.postGlobalRequest(lockType);
	}

	void postGlobalRequest(final int lockType) {
		// ֻ�б�������Ҫ��д�������
	}

	void postGlobalRelease() {
		// ֻ�б�������Ҫ��д�������
	}

	/**
	 * �������ı����������Ѿ����ϵĻ�Ӧ��ֻ��Զ�������Ե��ø÷���
	 */
	private final void remotePostLockedRespose() {
		// TODO
	}

	/**
	 * ���Զ�������ɹ���ֻ��Զ�������Ե��û���д���������
	 */
	void markRemoteLocked() {
		// ֻ��Զ����������д�������
	}

	/**
	 * ���ؽ���Զ����������Ϣ����Զ��ͨ��remotePostLockedRespose������
	 */
	final void localReceiveRemoteLocked(int nodeMask) {
		synchronized (this.res) {
			if ((this.nodeMask |= nodeMask) == NODE_MASK_ALL) {
				Unsf.unsafe.unpark(this.holder.thread);
			}
		}
	}

	/**
	 * ��ͬ��״̬�²��뱾�ڵ�
	 * 
	 * @param before
	 *            �ڴ�֮ǰ���룬���Ϊnull��ʾ���뵽���β����
	 * @param waitHead
	 *            ����ĵȴ��ߣ�ָ����������ȵȴ���
	 * @param lockState
	 *            ���±���������
	 */
	@SuppressWarnings("unchecked")
	private final void nosyncInsertSelfBefore(
			NewAcquirer<TAcquirable, TAcquirer> before,
			NewAcquirer<TAcquirable, TAcquirer> waitHead, byte lockState) {
		// ##2009-12-23##
		if (before != null) {// �вο��Ľڵ�
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
			// ���뵽�ʼ��
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
	 * �ȴ����нڵ㶼��ס
	 * 
	 * @param deadline
	 *            ��ʱʱ��
	 * @throws TimeoutException
	 *             ��ʱ�쳣
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
	 * ���ع�������<br>
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
			case LOCK_N:// û����
			case LOCK_LS:// �������ǹ���������ô�������Ҫôû�У�Ҫô�Ͷ��ǹ�����
				// ���û����������ֻ�б��ع���������ô����ɹ��������β
				this.nosyncInsertSelfBefore(null, null, LOCK_LS);
				// �Ѿ���ñ��ع��������˳�
				return;
			case LOCK_LU:// ��һ�����Ǳ���������������ֻ��������������
			case LOCK_RU:// ��һ�����Ǳ���������������ֻ����������������
				// ���뵽��������ǰ�棬������������β��
				this.nosyncInsertSelfBefore(tail, null, LOCK_LS);
				// �Ѿ���ñ��ع��������˳�
				return;
			case LOCK_LUWR:
			case LOCK_LUW:
				// ��Ҫ�ŵ���Щ����ǰ�棬����Ҫ�ŵ�X���ĺ���
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
				// ��һ�����ǵȴ��������ֻ�ܷŵ���β���Ҹ����Լ���nextָ��ԭ�������ȵȴ��ڵ�
				this.nosyncInsertSelfBefore(null, tail.next, LOCK_LSW);
				// ��Ҫ����ȴ�״̬
				break;
			case LOCK_LX:
			case LOCK_RX:
				// ��һ���Ƕ�ռ������˷����β�ȴ�
				this.nosyncInsertSelfBefore(null, this, LOCK_LSW);
				// ��Ҫ����ȴ�״̬
				break;
			default:
				throw illegalState(tailLockState);
			}
		}
		// �ȴ���������ϵ�⿪���߳�ʱ
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * ���ؿ���������
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
				case LOCK_N: // û���κ���
				case LOCK_LS: // ��һ�����ǹ����������µ���Ҫôû�У�Ҫô�ǹ�����
					// �򱾽ڵ�ı��
					if (this.localMarkLocked()) {
						// ˵��û��������Ⱥ�ڵ㣬ֱ�Ӽ��ɻ����
						// �����β
						this.nosyncInsertSelfBefore(null, null, LOCK_LU);
						return;// �����ֱ�ӷ���
					} else {
						// ˵������Ҫ�ȴ�������Ⱥ�ڵ�ȷ����
						// �����β�����ҵȴ�Զ�̽ڵ㴦��������ȷ��
						this.nosyncInsertSelfBefore(null, this, LOCK_LUWR);
						break;
					}
				case LOCK_LU:
				case LOCK_RU:
				case LOCK_LX:
				case LOCK_RX:
					// ���������������ͻ������������β�����б��صȴ�
					// ��������ȵȴ������Լ�
					this.nosyncInsertSelfBefore(null, this, LOCK_LUW);
					// ������Ⱥ�㲥
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
					// ���������������ͻ������������β�����б��صȴ�
					// ��������ȵȴ�����tail.next
					this.nosyncInsertSelfBefore(null, tail.next, LOCK_LUW);
					// ������Ⱥ�㲥
					break localOrRemote;
				default:
					throw illegalState(tailLockState);
				}
			}
			// ��������Ⱥ�ڵ�㲥Զ������������
			this.localPostGlobalLockRequest(LOCK_RUW);
		}
		// �ȴ����ص�����ϵ�⿪�Ӷ�����������߳�ʱ
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * ���ض�ռ����
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
					// ���û����
					// �򱾽ڵ�ı��
					if (this.localMarkLocked()) {
						// ˵��û��������Ⱥ�ڵ㣬ֱ�Ӽ��ɻ����
						// �����β
						this.nosyncInsertSelfBefore(null, null, LOCK_LX);
						return;// �����ֱ�ӷ���
					} else {
						// ˵������Ҫ�ȴ�������Ⱥ�ڵ�ȷ����
						// �����β
						this.nosyncInsertSelfBefore(null, this, LOCK_LXWR);
						// ��Ҫ�㲥��ȷ������
					}
				} else {
					switch (tail.lockState) {
					case LOCK_LS:
					case LOCK_LU:
					case LOCK_RU:
					case LOCK_LX:
					case LOCK_RX:
						// ֮ǰ������ֱ�ӷ����β�����б��صȴ�
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
						// ֮ǰ������ֱ�ӷ����β�����б��صȴ����ȴ�βʹ��ԭ����β
						this.nosyncInsertSelfBefore(null, tail.next, LOCK_LXW);
						break;
					default:
						throw illegalState(tail.lockState);
					}
				}
			}
			// �������ڵ�㲥Զ�̶�ռ������
			this.localPostGlobalLockRequest(LOCK_RXW);
		}
		// �ȴ����ص�����ϵ�⿪�Ӷ�����������߳�ʱ
		this.localWaitLock(deadline);
		// ##2009-12-18##
	}

	/**
	 * ���첢�ϱ�����
	 * 
	 * @param res
	 *            ��Ҫ��������Դ
	 * @param lockType
	 *            Ҫ��ı���������
	 * @param deadline
	 *            ��ʱʱ�䣨��ȷ�������롱��
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
		// �ýڵ��Ǳ��ؽڵ�
		this.holder = holder;
		holder.put(this);
		this.res = res;
		switch (lockType) {
		case LOCK_LSW:
			// ���ع���������
			this.localAskForShare(deadline);
			break;
		case LOCK_LUW:
			// ���ؿ�����������
			this.localAskForUpgrade(deadline);
			break;
		case LOCK_LXW:
			// ���ض�ռ������
			this.localAskForExclude(deadline);
			break;
		default:
			throw illegalState(lockType);
		}
		// ##2009-12-18##
	}

	/**
	 * ΪԶ�̽ڵ������������
	 */
	@SuppressWarnings("unchecked")
	private final void remoteAskForUpgrade() throws DeadLockException,
			TimeoutException {
		// ##2009-12-21## ������
		final TAcquirable res = this.res;
		obtainTheLock: {
			synchronized (res) {
				final NewAcquirer<TAcquirable, TAcquirer> tail = res.tail;
				if (tail == null) {
					// û���κ��������RU��
					this.nosyncInsertSelfBefore(null, null, LOCK_RU);
					// ���Իط�ȷ��
					break obtainTheLock;
				}
				final int localNodeMask = this.localNodeMask();
				final NewAcquirer<TAcquirable, TAcquirer> waitHead = tail.next;
				NewAcquirer<TAcquirable, TAcquirer> locker = waitHead != null ? waitHead.next
						: tail;
				switch (locker.lockState) {
				case LOCK_LUWR:
					// TODO ���������û�п���
				case LOCK_LS: // ǰ���ǹ�����������������
					this.nosyncInsertSelfBefore(locker.next, waitHead, LOCK_RU);
					// �ɹ���ط�ȷ��
					break obtainTheLock;
				case LOCK_RU:// ����Ľڵ㷢�����������һ����������Ҫ�Ƚ����ȼ�
					if (locker.localNodeMask() > localNodeMask) {
						// ��locker�����ȼ��ߣ�����������������locker�漴����ȴ�״̬
						locker.lockState = LOCK_RUW;
						this.nosyncInsertSelfBefore(locker, locker, LOCK_RU);
						// �ɹ���ط�ȷ��
						break obtainTheLock;
					} else {
						if (locker == tail) {
							// �Ѿ��ִ��β��
							this.nosyncInsertSelfBefore(null, this, LOCK_RUW);
							// ���ط�ȷ��
							return;
						}
						locker = locker.next;// �������
						break;
					}
				case LOCK_RX:// ����Ľڵ㷢��Ķ�ռ����һ��������Ӧ���ó���
					// RX �������ȼ� ����RU��
					locker.lockState = LOCK_RXW;
					this.nosyncInsertSelfBefore(locker, locker, LOCK_RU);
					// �ɹ���ط�ȷ��
					break obtainTheLock;
				default:
					throw illegalState(locker.lockState);
				}
				for (;;) {
					switch (locker.lockState) {
					case LOCK_LUWR:// ���ڵ㷢�����������һ���ȴ�Զ�̣���Ҫ�Ƚ����ȼ�
					case LOCK_RUW:// ����Ľڵ㷢�����������һ����������Ҫ�Ƚ����ȼ�
						if (locker.localNodeMask() > localNodeMask) {
							// ��locker�����ȼ��ߣ����������ǰ�棬��Ҫ����Ƿ�locker������ǰ�����ȵȴ���
							this.nosyncInsertSelfBefore(locker,
									waitHead == locker ? this : waitHead,
									LOCK_RUW);
							// ���ط�ȷ��
							return;
						} else {
							if (locker == tail) {
								// �Ѿ��ִ��β����֮ǰ�еȴ�����waitHead
								this.nosyncInsertSelfBefore(null, waitHead,
										LOCK_RUW);
								return;
							}
							locker = locker.next;// �������
							continue;
						}
					case LOCK_LXW: // �������صȴ�����ֹͣ
					case LOCK_LUW:// �������صȴ�����ֹͣ
					case LOCK_LSW:// �������صȴ�����ֹͣ
					case LOCK_LXWR:// ���ڵ㷢��Ķ�ռ����һ���ȴ�Զ��
					case LOCK_RXW:// ����Ľڵ㷢��Ķ�ռ����һ������
						// X �������ȼ� ����U������Ҫ����Ƿ�locker������ǰ�����ȵȴ���
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
	 * ΪԶ�̽ڵ������ռ��
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
					// û���κ��������RX��
					this.nosyncInsertSelfBefore(null, null, LOCK_RX);
					// ���Իط�ȷ��
					break obtainTheLock;
				}
				final int localNodeMask = this.localNodeMask();
				final NewAcquirer<TAcquirable, TAcquirer> waitHead = tail.next;
				NewAcquirer<TAcquirable, TAcquirer> locker = waitHead != null ? waitHead.next
						: tail;
				switch (locker.lockState) {
				case LOCK_RX:// ����Ľڵ㷢��Ķ�ռ����һ����������Ҫ�Ƚ����ȼ�
					if (locker.localNodeMask() > localNodeMask) {
						// ��locker�����ȼ��ߣ�����������������locker�漴����ȴ�״̬
						locker.lockState = LOCK_RXW;
						this.nosyncInsertSelfBefore(locker, locker, LOCK_RX);
						// �ɹ���ط�ȷ��
						break obtainTheLock;
					} else {
						if (locker == tail) {
							// �Ѿ��ִ��β��
							this.nosyncInsertSelfBefore(null, this, LOCK_RXW);
							// ���ط�ȷ��
							return;
						}
						locker = locker.next;// �������
						break;
					}
				case LOCK_RU:// ����Ľڵ㷢�����������һ��������RX����RU�����ȼ���
					// RX �������ȼ� ����RU��
					if (locker == tail) {
						// �Ѿ��ִ��β��
						this.nosyncInsertSelfBefore(null, this, LOCK_RXW);
						// ���ط�ȷ��
						return;
					}
					locker = locker.next;// �������
					break;
				default:
					throw illegalState(locker.lockState);
				}
				for (;;) {
					switch (locker.lockState) {
					case LOCK_LXWR:// ���ڵ㷢��Ķ�ռ����һ���ȴ�Զ�̣���Ҫ�Ƚ����ȼ�
					case LOCK_RXW:// ����Ľڵ㷢��Ķ�ռ����һ����������Ҫ�Ƚ����ȼ�
						if (locker.localNodeMask() > localNodeMask) {
							// ��locker�����ȼ��ߣ����������ǰ��
							this.nosyncInsertSelfBefore(locker,
									waitHead == locker ? this : waitHead,
									LOCK_RXW);
							// ���ط�ȷ��
							return;
						} else {
							if (locker == tail) {
								// �Ѿ��ִ��β��
								this.nosyncInsertSelfBefore(null, waitHead,
										LOCK_RXW);
								return;
							}
							locker = locker.next;// �������
							continue;
						}
					case LOCK_LUWR:// ���ڵ㷢��Ķ�ռ����һ���ȴ�Զ��
					case LOCK_RUW:// ����Ľڵ㷢��Ķ�ռ����һ������
						// X �������ȼ� ����U��
						if (locker == tail) {
							// �Ѿ��ִ��β��
							this.nosyncInsertSelfBefore(null, waitHead,
									LOCK_RXW);
							return;
						}
						locker = locker.next;// �������
						continue;
					case LOCK_LXW: // �������صȴ�����ֹͣ
					case LOCK_LUW:// �������صȴ�����ֹͣ
					case LOCK_LSW:// �������صȴ�����ֹͣ
						// �ִ���Ҫֹͣ�ĵط�
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
	 * ���첢��Զ����
	 * 
	 * @param res
	 *            ��Ҫ��������Դ
	 * @param lockType
	 *            Ҫ��ı���������
	 */
	NewAcquirer(AcquirerHolder2<TAcquirable, TAcquirer> holder,
			TAcquirable res, byte lockType) {
		// ##2009-12-18##
		// �ýڵ���Զ�̽ڵ�
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
	 * ��RU��������RX��������
	 */
	public final void remoteUpgrade() {
		// ##2009-12-23##
		synchronized (this.res) {
			if (this.lockState != LOCK_RU) {
				throw new IllegalStateException("��Զ�̸���������������Զ�̶�ռ��");
			}
			if (this.prev == null) {
				// ���Զ����������ǰ��û�����ˣ�S�������������
				this.lockState = LOCK_RX;
			} else {
				// ������Ҫ�ȴ�
				this.lockState = LOCK_RUXW;
				return;
			}
		}
		// Զ�̽ڵ�ط���ȷ����Ϣ
		this.remotePostLockedRespose();
		// ##2009-12-23##
	}

	/**
	 * ��LU��������LX��
	 */
	public final void localUpgrade(long deadline) throws TimeoutException {
		// ##2009-12-23##
		// ֻ�е�ǰ�̲߳��ܸı䱾������״̬
		this.checkCurrentThread();
		synchronized (this.res) {
			if (this.lockState != LOCK_LU) {
				throw new IllegalStateException("�Ǳ��ظ������������������ض�ռ��");
			}
			if (this.prev == null && this.localMarkLocked()) {
				// ��������������Ľڵ��ȷ�ϣ��������ɹ���ֱ�ӷ���
				this.lockState = LOCK_LX;
				return;
			} else {
				// ����ȴ�
				this.lockState = LOCK_LUXWR;
			}
		}
		// ���ؽڵ�㲥��ȷ��������Ϣ
		this.localPostGlobalLockRequest(LOCK_RUXW);
		// ����ȴ�
		this.localWaitLock(deadline);
		// ##2009-12-23##
	}

	/**
	 * �ͷű�����
	 */
	public void localRelease() {
		this.checkCurrentThread();

		// TODO
	}

	/**
	 * Զ�����ͷ�
	 */
	public void remoteRelease() {
		// TODO
	}

	private final void wakeup(byte lockState) {
		this.lockState = lockState;
		Unsf.unsafe.unpark(this.holder.thread);
	}

	/**
	 * �����ͷ������¼����������:<br>
	 * 1. �����ͷš���ص�����LS/LU/LX��������LX/LU�����ͷź���Ҫ֪ͨ�����ڵ��ϵ�������ͷš�<br>
	 * 2. �ȴ���ʱ��������漰ȫ�����صȴ�������:LSW/LUW/LXW/LUWR/LXWR/LUXWR��
	 * ����LUWR/LXWR/LUXWR�����ͷź���Ҫ֪ͨ�����ڵ��ϵ�������ͷš�<br>
	 * 3. ������ͷš������ͷŻ�ʱʱ���������ؼ�Ⱥ�ڵ�ʧЧ�󣬻�֪ͨ�����ͷš�
	 * ��ص�������RUW/RU/RXW/RX/RUXW����Щ���ͷź���Ҫ����Ķ�����<br>
	 * ���ͷ�ʱ��������Ӱ��<br>
	 * 1. �����ȫ�����������Ѿ���������������ڵȴ�ȷ���У�����Ҫ�������ڵ㷢�ͳ���������Ϣ<br>
	 * 2. ��������������������򣬸ı�����״̬������Ϊȫ����ʱ��Ҫ�������ڵ㷢������������Ϣ<br>
	 * 3. ����Ǵ�����������������Ҫ����Դ�ڵ㷢����ȷ����Ϣ��
	 */
	public final void release() {
		final TAcquirable res = this.res;
		// ��������������������򣬸ı�����״̬������Ϊȫ����ʱ��Ҫ�������ڵ㷢������������Ϣ
		// ���������ʾ��������������LOCK_N��ʾû�������ߣ�LOCK_ACQUIR_RELEASE��ʾ��������������
		byte acquire_lock_type = LOCK_N;
		// ����Ǵ�����������������Ҫ����Դ�ڵ㷢����ȷ����Ϣ
		// ����ı�����ʾ��Ҫ������ȷ����Ϣ��Դ
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
					// �Ѿ������������ȡ�κζ���
				case LOCK_LXWR:
				case LOCK_LUWR:
				case LOCK_LUXWR:
					// �ȴ�Զ��������Զ�̴�������ȷ����Ϣ����
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

					// ���һ��U��
					final NewAcquirer<TAcquirable, TAcquirer> lastU;
					if (prev == null || prev.lockState == LOCK_LS) {
						lastU = null;
					} else if ((prev.lockState & LOCK_MASK_TYPE) == LOCK_MASK_U) {
						lastU = prev;
					} else {
						break;
					}
					// ��һ��U��
					NewAcquirer<TAcquirable, TAcquirer> firstU = lastU;
					// �Ƿ��ܻ��S��
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
						// ��һ��S��
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
							// ����β��
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
						break;// ��ֹѭ��
					}
					break;
				default:
					throw illegalState(next.lockState);
				}
			}
		}
		if (feedBackTo != null) {
			// TODO �ط���ȷ��֪ͨ
		}
		if (acquire_lock_type != LOCK_N) {
			// TODO �㲥��ȷ�ϻ�������
		}
		this.holder.remove(res);
		this.res = null;
	}

	// ʹ��ǰ������������
	final void toBeCompatibleWithExclusive(long deadline)
			throws DeadLockException {
		// ##2009-12-23##
		// ֻ�е�ǰ�̲߳��ܸı䱾������״̬
		this.checkCurrentThread();
		switch (this.lockState) {
		case LOCK_LX:
			break;
		case LOCK_LU:
			synchronized (this.res) {
				if (this.prev == null && this.localMarkLocked()) {
					// ��������������Ľڵ��ȷ�ϣ��������ɹ���ֱ�ӷ���
					this.lockState = LOCK_LX;
					return;
				} else {
					// ����ȴ�
					this.lockState = LOCK_LUXWR;
				}
			}
			break;
		case LOCK_LS:
			throw new UnsupportedOperationException("[������]�޷�����Ϊ[������]");
		default:
			throw new IllegalStateException("��Ч��״̬��" + this.lockState);
		}
		// ���ؽڵ�㲥��ȷ��������Ϣ
		this.localPostGlobalLockRequest(LOCK_RUXW);
		// ����ȴ�
		this.localWaitLock(deadline);
		// ##2009-12-23##
	}

	// ʹ��ǰ�����ݿ�����������
	final void toBeCompatibleWithShareUpgradable() {
		// ##2009-12-23##
		// ֻ�е�ǰ�̲߳��ܸı䱾������״̬
		this.checkCurrentThread();
		switch (this.lockState) {
		case LOCK_LU:
		case LOCK_LX:
			break;
		case LOCK_LS:
			throw new UnsupportedOperationException("[������]�޷�����Ϊ[������������]");
		default:
			throw new IllegalStateException("��Ч��״̬��" + this.lockState);
		}
		// ##2009-12-23##
	}

	// /////////////////////////////////////////////////////////////////////////
	// Holder���
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
