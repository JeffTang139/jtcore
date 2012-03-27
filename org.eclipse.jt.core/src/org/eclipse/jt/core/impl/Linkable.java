/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Linkable.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface Linkable<T extends Linkable<T>> {

	T next();

	void setNext(T next);

	static interface Helper {

		<E extends Linkable<E>> Iterator<E> iterate(E head);
	}

	static final Helper helper = new Helper() {

		public <E extends Linkable<E>> Iterator<E> iterate(final E head) {
			return new Iterator<E>() {
				private E next = head;

				public boolean hasNext() {
					return this.next != null;
				}

				public E next() {
					E current = this.next;
					if (current == null) {
						throw new NoSuchElementException();
					}
					this.next = current.next();
					return current;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

	};

}
