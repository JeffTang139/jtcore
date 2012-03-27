package org.eclipse.jt.core.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * ¿Õµü´úÆ÷
 * @author Jeff Tang
 * 
 */
public final class EmptyIterator implements Iterator<Object> {
	public boolean hasNext() {
		return false;
	}
	public Object next() {
		throw new NoSuchElementException();
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
	private EmptyIterator() {
	}
	public static final Iterator<Object> emptyIterator = new EmptyIterator();
}