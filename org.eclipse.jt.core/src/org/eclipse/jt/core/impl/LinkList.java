package org.eclipse.jt.core.impl;

import java.util.Iterator;

/**
 * Á´±í
 * 
 * @author Jeff Tang
 * 
 * @param <T>
 */
@SuppressWarnings("unchecked")
class LinkList implements Iterable, TextLocalizable {
	class LinkNode {
		public Object data;
		public LinkNode next;
	}

	class LinkListIterator implements Iterator {
		private LinkNode next;

		public LinkListIterator(LinkNode head) {
			this.next = head;
		}

		public boolean hasNext() {
			return this.next != null;
		}

		public Object next() {
			Object e = this.next.data;
			this.next = this.next.next;
			return e;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private LinkNode head;
	private LinkNode tail;
	private int count;

	public void add(Object data) {
		if (this.tail == null) {
			this.tail = this.head = new LinkNode();
		} else {
			this.tail.next = new LinkNode();
			this.tail = this.tail.next;
		}
		this.tail.data = data;
		this.count++;
	}

	public int count() {
		return this.count;
	}

	public int startLine() {
		if (this.head.data instanceof TextLocalizable) {
			return ((TextLocalizable) this.head.data).startLine();
		}
		return 0;
	}

	public int startCol() {
		if (this.head.data instanceof TextLocalizable) {
			return ((TextLocalizable) this.head.data).startCol();
		}
		return 0;
	}

	public int endLine() {
		if (this.head.data instanceof TextLocalizable) {
			return ((TextLocalizable) this.head.data).endLine();
		}
		return 0;
	}

	public int endCol() {
		if (this.head.data instanceof TextLocalizable) {
			return ((TextLocalizable) this.head.data).endCol();
		}
		return 0;
	}

	public <T> T[] toArray(T[] arr) {
		if (arr.length < this.count) {
			throw new IndexOutOfBoundsException();
		}
		LinkNode n = this.head;
		int i = 0;
		while (n != null) {
			arr[i++] = (T) n.data;
			n = n.next;
		}
		return arr;
	}

	public Iterator iterator() {
		return new LinkListIterator(this.head);
	}
}
