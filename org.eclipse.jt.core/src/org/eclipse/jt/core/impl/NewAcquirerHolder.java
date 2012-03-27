package org.eclipse.jt.core.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 请求者保存者
 * 
 * @author Jeff Tang
 * 
 */
public abstract class NewAcquirerHolder<TRes extends NewAcquirable, TAcquirer extends NewAcquirer<TRes, TAcquirer>>
		implements NodeMaskProvider {
	/**
	 * 所属线程，允许为空
	 */
	final Thread ownerThread;

	final NodeMaskProvider nodeMaskProvider;

	private HashMap<TRes, TAcquirer> acquirers;

	public NewAcquirerHolder(Thread ownerThread,
			NodeMaskProvider nodeMaskProvider) {
		if (nodeMaskProvider == null) {
			throw new NullArgumentException("nodeMaskProvider");
		}
		this.ownerThread = ownerThread;
		this.nodeMaskProvider = nodeMaskProvider;
	}

	public final int getGlobalMask() {
		return this.nodeMaskProvider.getGlobalMask();
	}

	public final int getLocalMask() {
		return this.nodeMaskProvider.getLocalMask();
	}

	public final boolean isEmpty() {
		return (this.acquirers == null || this.acquirers.isEmpty());
	}

	public final int size() {
		return (this.acquirers == null ? 0 : this.acquirers.size());
	}

	protected final TAcquirer getAcquirer(TRes acquirable) {
		if (this.acquirers != null) {
			return this.acquirers.get(acquirable);
		}
		return null;
	}

	protected final void removeAcquirer(TRes res) {
		if (res != null && this.acquirers != null && this.acquirers.size() > 0) {
			this.acquirers.remove(res);
		}
	}

	protected final void putAcquirer(TAcquirer acquirer) {
		// NewAcquirerHolder<TRes, TAcquirer> holder = acquirer.getHolder();
		// if (holder != null && holder != this) {
		// throw new IllegalArgumentException();
		// }
		TRes res = acquirer.getRes();
		if (res == null) {
			throw new IllegalArgumentException("锁住的对象为空（null）");
		}
		if (this.acquirers == null) {
			this.acquirers = new HashMap<TRes, TAcquirer>();
		}
		TAcquirer old = this.acquirers.put(res, acquirer);
		Assertion.ASSERT(old == null || old == acquirer);
	}

	@SuppressWarnings("unchecked")
	final Iterator<TAcquirer> allAcquirers() {
		return (this.acquirers == null ? EMPTY : this.acquirers.values()
				.iterator());
	}

	@SuppressWarnings("unchecked")
	private static final Iterator EMPTY = new Iterator() {
		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
}
