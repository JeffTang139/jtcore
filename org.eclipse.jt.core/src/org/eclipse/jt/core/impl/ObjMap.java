package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.HashUtil;

/**
 * ¶ÔÏó¿½±´Ó³Éä±í
 * 
 * @author Jeff Tang
 * 
 */
final class ObjMap {
	public ObjMap() {
		this.table = new Entry[16];
	}

	private static class Entry {
		public final int hash;
		public final Object src;
		public final Object dest;
		public Entry next;

		Entry(int hash, Object src, Object dest, Entry next) {
			this.hash = hash;
			this.src = src;
			this.dest = dest;
			this.next = next;
		}
	}

	final void put(Object src, Object dest) {
		int oldLen = this.table.length;
		int index;
		if (++this.size > oldLen * 0.75) {
			int newLen = oldLen * 2;
			Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (Entry e = this.table[j], next; e != null; e = next) {
					index = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[index];
					newTable[index] = e;
				}
			}
			this.table = newTable;
			oldLen = newLen;
		}
		int hash = HashUtil.identityHash(src);
		index = hash & (oldLen - 1);
		this.table[index] = new Entry(hash, src, dest, this.table[index]);
	}

	final Object get(Object src) {
		for (Entry e = this.table[HashUtil.identityHash(src)
				& (this.table.length - 1)]; e != null; e = e.next) {
			if (e.src == src) {
				return e.dest;
			}
		}
		return null;
	}

	private Entry[] table;
	private int size;

}
