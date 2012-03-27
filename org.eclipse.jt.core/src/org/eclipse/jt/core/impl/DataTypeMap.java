package org.eclipse.jt.core.impl;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jt.core.misc.HashUtil;


/**
 * ±àÂëÀàÐÍÓ³Éä
 * 
 * @author Jeff Tang
 * 
 */
public abstract class DataTypeMap {

	DataTypeMap() {
		this.table = new Entry[256];
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.readLock = rwl.readLock();
		this.writeLock = rwl.writeLock();
	}

	private final ReentrantReadWriteLock.ReadLock readLock;
	private final ReentrantReadWriteLock.WriteLock writeLock;
	private volatile int modifyVersion;

	abstract DataTypeBase newType(int length, int precision, int scale);

	abstract int keyCode(int length, int precision, int scale);

	private static class Entry {
		public final int hash;
		public final int keyCode;
		public final DataTypeBase type;
		public Entry next;

		Entry(int hash, int keyCode, DataTypeBase type, Entry next) {
			this.hash = hash;
			this.keyCode = keyCode;
			this.type = type;
			this.next = next;
		}
	}

	public final DataTypeBase get(int length, int precision, int scale) {
		int keyCode = this.keyCode(length, precision, scale);
		int hash = HashUtil.hash(keyCode);
		int mv;
		this.readLock.lock();
		try {
			mv = this.modifyVersion;
			for (Entry e = this.table[hash & (this.table.length - 1)]; e != null; e = e.next) {
				if (e.keyCode == keyCode) {
					return e.type;
				}
			}
		} finally {
			this.readLock.unlock();
		}
		this.writeLock.lock();
		try {
			int oldLen = this.table.length;
			int index = hash & (oldLen - 1);
			Entry firstE = this.table[index];
			if (this.modifyVersion != mv) {
				for (Entry e = firstE; e != null; e = e.next) {
					if (e.keyCode == keyCode) {
						return e.type;
					}
				}
			}
			DataTypeBase type = this.newType(length, precision, scale);
			this.table[index] = new Entry(hash, keyCode, type, firstE);
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
			this.modifyVersion++;
			return type;
		} finally {
			this.writeLock.unlock();
		}
	}

	private Entry[] table;
	private int size;
}
