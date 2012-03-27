package org.eclipse.jt.core.impl;

/**
 * 请求者保存者
 * 
 * @author Jeff Tang
 * 
 */
public abstract class AcquirerHolder<TAcquirer extends Acquirer<? extends Acquirable, TAcquirer>> {

	protected TAcquirer[] acquirers;
	protected int size;

	/**
	 * 所属线程，允许为空
	 */
	final TransactionImpl transaction;

	public AcquirerHolder(TransactionImpl tansaction) {
		this.transaction = tansaction;
	}

	public final boolean isEmpty() {
		return (this.size == 0);
	}

	public final int size() {
		return this.size;
	}

	protected final TAcquirer getAcquirer(final Acquirable acquirable) {
		if (this.size > 0) {
			final TAcquirer[] acquirers = this.acquirers;
			final int index = acquirable.hashCode() & (acquirers.length - 1);
			for (TAcquirer a = acquirers[index]; a != null; a = a.nextInHolder) {
				if (acquirable == a.res) {
					return a;
				}
			}
		}
		return null;
	}

	protected final void removeAcquirer(final TAcquirer acquirer) {
		Acquirable res = acquirer.res;
		if (acquirer.getHolder() != this || this.acquirers == null
				|| res == null) {
			throw new IllegalArgumentException();
		}
		if (this.size > 0) {
			final TAcquirer[] acquirers = this.acquirers;
			final int index = res.hashCode() & (acquirers.length - 1);
			for (TAcquirer a = acquirers[index], last = null; a != null; last = a, a = a.nextInHolder) {
				if (acquirer == a) {
					if (last == null) {
						acquirers[index] = a.nextInHolder;
					} else {
						last.nextInHolder = a.nextInHolder;
					}
					a.nextInHolder = null;
					this.size--;
					return;
				}
			}
		}
	}

	protected abstract TAcquirer[] newArray(int length);

	protected final void putAcquirer(final TAcquirer acquirer) {
		AcquirerHolder<TAcquirer> holder = acquirer.getHolder();
		if (holder != null && holder != this) {
			throw new IllegalArgumentException();
		}
		int high;
		TAcquirer[] acquirers = this.acquirers;
		if (acquirers == null) {
			this.acquirers = acquirers = this.newArray(4);
			high = 3;
		} else {
			final int oldL = acquirers.length;
			if (this.size >= oldL) {
				final int newSize = oldL << 1;
				high = newSize - 1;
				TAcquirer[] newSpine = this.newArray(newSize);
				TAcquirer a, next;
				int newIndex;
				for (int i = 0; i < oldL; i++) {
					for (a = acquirers[i]; a != null;) {
						next = a.nextInHolder;
						newIndex = a.res.hashCode() & high;
						a.nextInHolder = newSpine[newIndex];
						newSpine[newIndex] = a;
						a = next;
					}
				}
				this.acquirers = acquirers = newSpine;
			} else {
				high = oldL - 1;
			}
		}
		final int index = acquirer.res.hashCode() & high;
		for (TAcquirer a = acquirers[index]; a != null; a = a.nextInHolder) {
			if (acquirer == a) {
				return;
			}
		}
		acquirer.nextInHolder = acquirers[index];
		acquirers[index] = acquirer;
		this.size++;
	}
}
