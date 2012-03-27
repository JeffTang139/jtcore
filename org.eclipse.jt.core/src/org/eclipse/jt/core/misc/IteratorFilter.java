package org.eclipse.jt.core.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * µü´ú×ª»¯Æ÷
 * 
 * @author Jeff Tang
 * 
 * @param <TIn>
 * @param <TOut>
 */
public abstract class IteratorFilter<TIn, TOut> implements Iterator<TOut> {
	private final Iterator<TIn> in;

	public IteratorFilter(Iterator<TIn> in) {
		if (in == null) {
			throw new NullArgumentException("in");
		}
		this.in = in;
		this.doNext();
	}

	private TOut current;

	private void doNext() {
		this.current = null;
		while (this.current == null && this.in.hasNext()) {
			TIn in = this.in.next();
			this.current = this.filter(in);
		}
	}

	protected abstract TOut filter(TIn in);

	public final boolean hasNext() {
		return this.current != null;
	}

	public final TOut next() {
		TOut out = this.current;
		if (out == null) {
			throw new NoSuchElementException();
		} else {
			this.doNext();
		}
		return out;
	}

	public final void remove() {
		this.in.remove();
	}
}
