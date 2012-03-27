package org.eclipse.jt.core.impl;

/**
 * �����ж�
 * 
 * @author Jeff Tang
 * 
 */
final class ConcurrentWorkQueue {
	/**
	 * �жӶ�β���жӳɻ�״
	 */
	private Work tail;
	/**
	 * ��С
	 */
	private int size;

	/**
	 * �����ж�<br>
	 * ���ø÷���ǰ��Ҫͬ��������<br>
	 */
	final boolean put(Work work) {
		if (work.putToCuncerringQ(this.tail)) {
			this.tail = work;
			this.size++;
			return true;
		}
		return false;
	}

	final boolean isEmpty() {
		return this.tail == null;
	}

	/**
	 * ���ж���ȡ��<br>
	 * ���ø÷���ǰ��Ҫͬ��������<br>
	 */
	final Work poll() {
		Work tail = this.tail;
		if (tail != null) {
			Work work = tail.removeNext();
			if (work == tail) {
				this.tail = null;
			}
			this.size--;
			return work;
		}
		return null;
	}
}
