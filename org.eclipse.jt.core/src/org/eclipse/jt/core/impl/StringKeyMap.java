package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.exception.NamedDefineExistingException;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * String为Key的Hash表
 * 
 * @author Jeff Tang
 * 
 * @param <TValue>
 */
public final class StringKeyMap<TValue> {

	public final TValue get(String key) {
		return this.get(key, false);
	}

	public final TValue get(String key, boolean mandatory) {
		checkKey(key);
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			if (mandatory) {
				throw missing(key);
			} else {
				return null;
			}
		}
		final int hash = key.hashCode();
		final int index = hash & (this.table.length - 1);
		for (Entry<TValue> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				return e.value;
			}
		}
		if (mandatory) {
			throw missing(key);
		}
		return null;
	}

	public final TValue put(String key, TValue value) {
		return this.put(key, value, false);
	}

	@SuppressWarnings("unchecked")
	public final TValue put(String key, TValue value, boolean mandatory) {
		checkKey(key);
		Entry<TValue>[] table = this.table;
		if (table == null) {
			table = new Entry[8];
			table[key.hashCode() & 7] = new Entry<TValue>(key, value);
			this.table = table;
			this.size = 1;
			return null;
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<TValue> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				if (mandatory) {
					throw existing(key);
				} else {
					TValue old = e.value;
					e.value = value;
					return old;
				}
			}
		}
		Entry<TValue> entry = new Entry<TValue>(key, value, table[index]);
		table[index] = entry;
		if (this.size++ > table.length * loadfactor) {
			final int newLength = table.length << 1;
			final int newMask = newLength - 1;
			final Entry<TValue>[] newTable = new Entry[newLength];
			for (Entry<TValue> e : table) {
				while (e != null) {
					final Entry<TValue> next = e.next;
					final int newIndex = e.key.hashCode() & newMask;
					e.next = newTable[newIndex];
					newTable[newIndex] = e;
					e = next;
				}
			}
			this.table = newTable;
		}
		return null;
	}

	public final TValue remove(String key) {
		return this.remove(key, false);
	}

	public final TValue remove(String key, boolean mandatory) {
		checkKey(key);
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			if (mandatory) {
				throw missing(key);
			} else {
				return null;
			}
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<TValue> e = table[index], prev = e; e != null; prev = e, e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				if (prev == e) {
					table[index] = e.next;
				} else {
					prev.next = e.next;
				}
				e.next = null;
				this.size--;
				return e.value;
			}
		}
		if (mandatory) {
			throw missing(key);
		}
		return null;
	}

	public final boolean containsKey(String key) {
		checkKey(key);
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			return false;
		}
		final int hash = key.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry<TValue> e = table[index]; e != null; e = e.next) {
			if (hash == e.hash && (key == e.key || key.equals(e.key))) {
				return true;
			}
		}
		return false;
	}

	public final boolean containsValue(TValue value) {
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			return false;
		}
		for (int i = 0, c = table.length; i < c; i++) {
			for (Entry<TValue> e = table[i]; e != null; e = e.next) {
				if (e.value == null) {
					if (value == null) {
						return true;
					} else {
						continue;
					}
				} else if (e.value == value || e.value.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	public final void validateKey(String key) {
		if (this.containsKey(key)) {
			throw existing(key);
		}
	}

	public final void clear() {
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			table[i] = null;
		}
		this.size = 0;
	}

	public final boolean isEmpty() {
		return this.size == 0;
	}

	public final int size() {
		return this.size;
	}

	public static interface StringKeyMapVisitor<T> {

		public void doVisit(String key, T value);
	}

	public final void visitAll(StringKeyMapVisitor<TValue> visitor) {
		if (visitor == null) {
			throw new NullArgumentException("访问器");
		}
		final Entry<TValue>[] table = this.table;
		if (table == null) {
			return;
		}
		for (int i = 0; i < table.length; i++) {
			Entry<TValue> e = table[i];
			while (e != null) {
				visitor.doVisit(e.key, e.value);
				e = e.next;
			}
		}
	}

	public static final class Entry<TValue> {

		final String key;

		final int hash;

		TValue value;

		Entry<TValue> next;

		Entry(String key, TValue value) {
			this.key = key;
			this.hash = key.hashCode();
			this.value = value;
		}

		Entry(String key, TValue value, Entry<TValue> next) {
			this.key = key;
			this.hash = key.hashCode();
			this.value = value;
			this.next = next;
		}
	}

	private static final void checkKey(String key) {
		if (key == null || key.length() == 0) {
			throw new NullArgumentException("键值");
		}
	}

	private static final float loadfactor = 0.75f;

	private static final int MAXIMUM_CAPACITY = 1 << 30;

	private Entry<TValue>[] table;

	private int size;

	private static final NamedDefineExistingException existing(String name) {
		return new NamedDefineExistingException("名称为[" + name + "]的元素已经存在.");
	}

	private static final MissingDefineException missing(String name) {
		return new MissingDefineException("名称为[" + name + "]的元素不存在.");
	}

	public StringKeyMap() {
	}

	@SuppressWarnings("unchecked")
	public StringKeyMap(int initialCapacity) {
		if (initialCapacity > MAXIMUM_CAPACITY) {
			initialCapacity = MAXIMUM_CAPACITY;
			this.table = new Entry[initialCapacity];
		} else {
			int capacity = 1;
			while (capacity < initialCapacity) {
				capacity <<= 1;
			}
			this.table = new Entry[capacity];
		}
	}
}
