package org.eclipse.jt.core.misc;

/**
 * °ë³ÉÆ·hashMap
 * 
 * @author Jeff Tang
 * 
 * @param <V>
 */
public abstract class SemiFinishedHashMap<V> {
	@SuppressWarnings("unchecked")
	public SemiFinishedHashMap(int cap) {
		if (cap <= 0) {
			this.table = empty;
		} else {
			int l = 1;
			while (l < cap) {
				l <<= 1;
			}
			this.table = new Entry[l];
		}
	}

	protected static class Entry<V> {
		public final int hash;
		public V v;
		public Entry<V> next;

		Entry(int hash, V v, Entry<V> next) {
			this.hash = hash;
			this.v = v;
			this.next = next;
		}
	}

	protected static int hash(int h) {
		h += ~(h << 9);
		h ^= (h >>> 14);
		h += (h << 4);
		return h ^ (h >>> 10);
	}

	protected final void put(V v, int hash) {
		if (v == null) {
			throw new NullPointerException();
		}
		int oldLen = this.table.length;
		int index;
		if (++this.size > oldLen * 0.75) {
			int newLen = oldLen * 2;
			@SuppressWarnings("unchecked")
			Entry<V>[] newTable = new Entry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (Entry<V> e = this.table[j], next; e != null; e = next) {
					index = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[index];
					newTable[index] = e;
				}
			}
			this.table = newTable;
			oldLen = newLen;
		}
		index = hash & (oldLen - 1);
		this.table[index] = new Entry<V>(hash, v, this.table[index]);
	}

	protected static final <V> Entry<V> getEntry(SemiFinishedHashMap<V> map,
	        int hash) {
		if (map != null) {
			int l = map.table.length;
			if (l > 0) {
				return map.table[hash & (l - 1)];
			}
		}
		return null;
	}

	protected final void setFirstEntry(Entry<V> e, int hash) {
		int l = this.table.length;
		if (l > 0) {
			this.table[hash & (l - 1)] = e;
		} else {
			throw new IllegalStateException("map is empty");
		}
	}

	protected final boolean remove(V v, int hash, ExceptionCatcher catcher) {
		int l = this.table.length;
		if (l > 0) {
			int index = hash & (l - 1);
			for (Entry<V> last = null, e = this.table[index]; e != null; last = e, e = last.next) {
				if (e.v == v) {
					if (last == null) {
						this.table[index] = e.next;
					} else {
						last.next = e.next;
					}
					this.size--;
					// help GC
					e.next = null;
					e.v = null;
					this.removing(v, catcher);
					return true;
				}
			}
		}
		return false;
	}

	protected void removing(V v, ExceptionCatcher catcher) {
	}

	protected void clearing(V v, ExceptionCatcher catcher) {
	}

	public void clear(ExceptionCatcher catcher) {
		for (int i = 0; i < this.table.length; i++) {
			for (Entry<V> last = null, e = this.table[i]; e != null; last = e, e = last.next) {
				// help GC
				if (last == null) {
					this.table[i] = null;
				} else {
					e.next = null;
				}
				V v = e.v;
				e.v = null;
				this.clearing(v, catcher);
			}
		}
		this.size = 0;
	}

	private Entry<V>[] table;
	private int size;
	@SuppressWarnings("unchecked")
	private static Entry[] empty = new Entry[0];
}
