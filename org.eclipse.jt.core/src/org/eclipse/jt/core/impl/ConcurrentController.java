package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ����������
 * 
 * @author Jeff Tang
 * 
 */
final class ConcurrentController {
	/**
	 * ��������
	 */
	private final int permits;
	/**
	 * ��������
	 */
	private int concurrings;
	/**
	 * �ж�
	 */
	final ConcurrentWorkQueue workqueue;

	/**
	 * �жӼ�
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
	 * ���벢��<br>
	 * ����true��ʾ�������Կ�ʼ��<br>
	 * �����ʾ������ʼ�Ŷӡ���������Ҫ��ʼ
	 * 
	 * @param work
	 *            ����
	 */
	final boolean enterScope(Work work) {
		// ���п�������״̬�����ж�Ϊ��
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
	 * �뿪����<br>
	 * ���طǿ�ֵ��ʾ����Ҫ��������work�������õ���enter(OverlappedWork)<br>
	 * ���ؿ�ֵ��ʾû�еȴ��Ĺ���������
	 */
	final void leaveScope(WorkingManager manager) {
		// ���п�������״̬�����ж�Ϊ��
		synchronized (this.workqueue) {
			this.concurrings--;
			for (Work work = this.workqueue.poll(); work != null; work = this.workqueue
			        .poll()) {
				ConcurrentController ccr = work.getConcurrentController();
				// ���ж��еĹ�����ccr������Ϊ��
				if (ccr.concurrings < ccr.permits) {
					// ��ǰ�߳��빤�����ύ�̲߳�ͬ����Ҫ��������
					synchronized (work) {
						if (manager.startWork(work)) {
							ccr.concurrings++;
						}
					}
				} else {
					// �ﵽ�������ƣ�ֹͣ�ύ����
					break;
				}
			}
		}
	}
}
