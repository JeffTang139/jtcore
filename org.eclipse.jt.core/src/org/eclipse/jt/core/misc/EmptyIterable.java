package org.eclipse.jt.core.misc;

import java.util.Iterator;
/**
 * 空的可迭代类
 * @author Jeff Tang
 * 
 */
public final class EmptyIterable implements Iterable<Object> {
	public Iterator<Object> iterator() {
		return EmptyIterator.emptyIterator;
	}
	private EmptyIterable() {
	}
	public static final Iterable<Object> emptyIterable = new EmptyIterable();
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> emptyIterable(Class<T> clazz) {
		return (Iterable<T>) emptyIterable;
	}
}