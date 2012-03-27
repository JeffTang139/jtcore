package org.eclipse.jt.core.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ���õ����Ƴ����б�
 * 
 * @author Jeff Tang
 * 
 * @param <E>
 *            �б�Ԫ��
 */
public class SafeItrList<E> extends ArrayList<E> {
	private static final long serialVersionUID = -4738148028865821502L;

	public SafeItrList() {
	}

	public SafeItrList(E[] items) {
		super(items.length);
		this.addAll(items);
	}

	public SafeItrList(int initialCapacity) {
		super(initialCapacity);
	}

	public void addAll(E[] items) {
		if (items == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < items.length; i++) {
			this.add(items[i]);
		}
	}

	public final Iterator<E> removableIterator() {
		return super.iterator();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int cursor = 0;

			public boolean hasNext() {
				return this.cursor < SafeItrList.this.size();
			}

			public E next() {
				try {
					return SafeItrList.this.get(this.cursor++);
				} catch (IndexOutOfBoundsException e) {
					throw new NoSuchElementException();
				}
			}

			/**
			 * ��ֹ��ö�����޸�
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void trunc(int fromIndex) {
		if (fromIndex < 0) {
			throw new IllegalArgumentException("fromIndex ������� 0");
		}
		for (int i = this.size() - 1; i >= fromIndex; i--) {
			this.remove(i);
		}
	}

	/**
	 * ���ؿɵ����ӿ�
	 * 
	 * @param <E>
	 * @param list
	 *            �б�
	 * @return ���ؿɵ����ӿ�
	 */
	@SuppressWarnings("unchecked")
	public static <E> Iterable<E> iterable(SafeItrList<E> list) {
		if (list == null || list.size() == 0) {
			return (Iterable<E>) EmptyIterable.emptyIterable;
		}
		return list;
	}
}
