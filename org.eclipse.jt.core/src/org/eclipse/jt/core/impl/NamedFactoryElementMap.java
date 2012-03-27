package org.eclipse.jt.core.impl;

import java.util.ArrayList;

/**
 * √¸√˚∂®“ÂMap
 * 
 * @author Jeff Tang
 * 
 */
final class NamedFactoryElementMap {
	public NamedFactoryElementMap() {
		this.table = new Entry[4];
	}

	private static class Entry {
		final int hash;
		NamedFactoryElement meta;
		Entry next;

		Entry(int hash, NamedFactoryElement meta, Entry next) {
			this.hash = hash;
			this.meta = meta;
			this.next = next;
		}
	}

	final int size() {
		return this.size;
	}

	@SuppressWarnings("unchecked")
	final <TElementMeta extends NamedFactoryElement> ArrayList<TElementMeta> fillElements(
	        ArrayList<TElementMeta> metas) {
		if (this.size > 0) {
			if (metas == null) {
				metas = new ArrayList<TElementMeta>(this.size);
			} else {
				metas.ensureCapacity(this.size + metas.size());
			}
			for (Entry e : this.table) {
				while (e != null) {
					metas.add((TElementMeta) e.meta);
					e = e.next;
				}
			}
		}
		return metas;
	}

	final NamedFactoryElement put(NamedFactoryElement meta) {
		int hash = meta.name.hashCode();
		for (Entry e = this.table[hash & (this.table.length - 1)]; e != null; e = e.next) {
			if (e.hash == hash && e.meta.name.equals(meta.name)) {
				NamedFactoryElement old = e.meta;
				e.meta = meta;
				return old;
			}
		}
		int oldLen = this.table.length;
		int index = hash & (oldLen - 1);
		this.table[index] = new Entry(hash, meta, this.table[index]);
		if (++this.size > oldLen * 0.75) {
			int newLen = oldLen * 2;
			Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (Entry e = this.table[j], next; e != null; e = next) {
					int i = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[i];
					newTable[i] = e;
				}
			}
			this.table = newTable;
		}
		return null;
	}

	final NamedFactoryElement get(String id) {
		int hash = id.hashCode();
		for (Entry e = this.table[hash & (this.table.length - 1)]; e != null; e = e.next) {
			if (e.hash == hash && e.meta.name.equals(id)) {
				return e.meta;
			}
		}
		return null;
	}

	private Entry[] table;
	private int size;

}
