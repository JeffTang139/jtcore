package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ExceptionCatcher;

/**
 * �첽���ù�����
 * 
 * @author Jeff Tang
 * 
 */
final class WorkingManager {
	/**
	 * Ĭ�ϵ���С�߳�����
	 */
	static final int DEFAULT_MIN_THREADS = 4;
	/**
	 * Ĭ�ϵ��߳��������ʱ��
	 */
	static final int DEFAULT_MAX_IDLE_LIFE = 60 * 1000;
	/**
	 * ������
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
	 * �����첽����<br>
	 * �÷����ĵ��ñ����빤���������߳���ͬһ���̣߳�������������
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
	 * ��С�߳�����
	 */
	private int minIdleThreads = DEFAULT_MIN_THREADS;
	/**
	 * ������ʱ��
	 */
	private int maxIdleLife = DEFAULT_MAX_IDLE_LIFE;

	/**
	 * �����߳�����
	 * 
	 * @return
	 */
	final int getIdleThreads() {
		return this.idles;
	}

	/**
	 * �Ƿ��̲߳�����
	 */
	final boolean isLowOnThreads() {
		return this.threads == this.maxThreads && this.idleThreads == null;
	}

	/**
	 * �̵߳�������
	 */
	private int maxThreads = Integer.MAX_VALUE;

	/**
	 * �߳�����
	 */
	private int threads;
	/**
	 * ��Ҫ����ִ�еĹ��������̲߳���ʱ������жӡ�
	 */
	private Work startingWorksTail;
	/**
	 * ���õ��߳�
	 */
	private WorkingThread idleThreads;
	/**
	 * ���õ��߳�����
	 */
	private int idles;

	/**
	 * ִ�й��� <br>
	 * �漰��������״̬�ĸı䣬<br>
	 * ��ǰ�߳��빤�����ύ�̲߳���ͬһ���߳�ʱ��Ҫ�ⲿ��������<br>
	 * ��ǰ��֪�Ĳ���ͬһ�̵߳ĵط���activeWork();ConcurrentController.leaveScope(
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
	 * �̱߳��
	 */
	private int threadNum;

	/**
	 * �½��̱߳��
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
					// ���ж����Ƴ�
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
	 * ��ȡ����
	 * 
	 * @return ����null��ʾҪ�˳��߳�
	 */
	final Work getWorkToDo(WorkingThread thread) {
		for (;;) {
			Work todo;
			findWorkToDo: {
				// ����ȡ���ȴ�ִ�еĹ����������̷߳�������ж�
				synchronized (this) {
					// ��������߳���ʱ��Ҫ����
					if (this.scheduler == null
							|| this.threads > this.maxThreads) {
						this.threadExitNoSync(thread, false);
						return null;
					}
					// ����ж������򷵻�
					final Work tail = this.startingWorksTail;
					if (tail != null) {
						final Work first = tail.removeNext();
						if (first == tail) {
							this.startingWorksTail = null;
						}
						todo = first;
						break findWorkToDo;
					}
					// �����������ж�
					thread.nextIdle = this.idleThreads;
					this.idleThreads = thread;
					this.idles++;
				}
				//
				for (;;) {
					// ���е�����ʱ�߳��Ѿ��ǿ����߳�
					try {
						synchronized (thread) {
							// �������������ͬ������Ƿ񱻷����˹���
							todo = thread.work;
							if (todo == null) {
								// ����ȴ�
								thread.wait(this.maxIdleLife);
								todo = thread.work;
							}
							thread.work = null;
						}
					} catch (InterruptedException e) {
						synchronized (this) {
							this.threadExitNoSync(thread, true);
						}
						// ����ֹ��������ֹͣ�����ǲ��ÿ��ǹ������Լ��ӿ����ж����Ƴ�
						return null;
					}
					if (todo != null) {
						break findWorkToDo;
					}
					// ��ʱ��������������Ƿ񱻷����˹������Բ���ȷ��
					synchronized (this) {
						// ���������ó�ʱ�̣߳�����������ǰ�߳���Ȼ���ܻᱻ���乤��
						todo = thread.work;
						if (todo != null) {
							thread.work = null;
							break findWorkToDo;
						}
						// ȷʵ����
						if (this.idles > this.minIdleThreads) {
							// ������С�߳�������˴������ж����Ƴ���������ֹ�߳�
							this.threadExitNoSync(thread, true);
							return null;
						}
					}
					// �̻߳����Ա�������˴��µȴ�
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
	 * ������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private final class Scheduler extends Work {
		/**
		 * ��ʱ�����жӵĶ�β������ж���Ĺ�������û�е�������ʱ��
		 */
		private Work scheduledWorksTail;

		/**
		 * ����ʱ���������ж�
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
