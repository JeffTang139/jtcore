package org.eclipse.jt.core.impl;

import java.util.List;

/**
 * √¸√˚∂®“ÂMap
 * 
 * @author Jeff Tang
 * 
 */
final class NamedDefineMap {
	public NamedDefineMap() {
		this.table = new Entry[4];
	}

	private static class Entry {
		final int hash;
		NamedDefineImpl define;
		Entry next;

		Entry(int hash, NamedDefineImpl define, Entry next) {
			this.hash = hash;
			this.define = define;
			this.next = next;
		}
	}

	final synchronized void fetchAll(List<NamedDefineImpl> fetchInto) {
		for (Entry e : this.table) {
			while (e != null) {
				fetchInto.add(e.define);
				e = e.next;
			}
		}
	}

	final synchronized NamedDefineImpl put(NamedDefineImpl define) {
		final String name = define.name;
		final int hash = name.hashCode();
		Entry[] table = this.table;
		final int length = table.length;
		int index = hash & (length - 1);
		for (Entry e = table[index]; e != null; e = e.next) {
			if (e.hash == hash && e.define.name.equals(name)) {
				NamedDefineImpl old = e.define;
				e.define = define;
				return old;
			}
		}
		if (++this.size > length * 0.75) {
			final int newLen = length * 2;
			final int newH = newLen - 1;
			final Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < length; j++) {
				for (Entry e = table[j], next; e != null; e = next) {
					final int i = e.hash & newH;
					next = e.next;
					e.next = newTable[i];
					newTable[i] = e;
				}
			}
			this.table = table = newTable;
			index = hash & (newLen - 1);
		}
		table[index] = new Entry(hash, define, table[index]);
		return null;
	}

	final synchronized NamedDefineImpl remove(String name) {
		int hash = name.hashCode();
		final Entry[] table = this.table;
		final int index = hash & (table.length - 1);
		for (Entry e = table[index], last = null; e != null; last = e, e = e.next) {
			if (e.hash == hash && e.define.name.equals(name)) {
				NamedDefineImpl old = e.define;
				if (last == null) {
					table[index] = e.next;
				} else {
					last.next = e.next;
				}
				e.next = null;// HELP GC
				return old;
			}
		}
		return null;
	}

	final synchronized NamedDefineImpl get(String name) {
		final Entry[] table = this.table;
		final int hash = name.hashCode();
		for (Entry e = table[hash & (table.length - 1)]; e != null; e = e.next) {
			if (e.hash == hash && e.define.name.equals(name)) {
				return e.define;
			}
		}
		return null;
	}

	private Entry[] table;
	private int size;

}
