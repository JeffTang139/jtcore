/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AbstractMap.java
 * Date 2009-5-11
 */
package org.eclipse.jt.core.impl;

import java.util.ConcurrentModificationException;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 用整数作为键值的哈希表。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class IntKeyMap<TValue> {
    private static final float loadFactor = 2.0f;

    transient volatile int modCount;

    protected int modCount() {
        return this.modCount;
    }

    public static class IntMapEntry<TValue> {
        public final int key;
        private TValue value;

        private IntMapEntry<TValue> next;

        private IntMapEntry(int key, TValue value, IntMapEntry<TValue> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public TValue getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private IntMapEntry<TValue>[] entries;
    private int size;

    public boolean isEmpty() {
        return (this.size == 0);
    }

    public void clear() {
        this.modCount++;
        this.size = 0;
        this.entries = null;
    }

    public TValue get(int key) {
        if (this.size > 0) {
            int index = UtilHelper.indexForIntKey(key, this.entries.length);
            IntMapEntry<TValue> e = this.entries[index];
            while (e != null) {
                if (e.key == key) {
                    return e.value;
                }
                e = e.next;
            }
        }
        return null;
    }

    public TValue put(int key, TValue value) {
        this.ensureCapacity();

        TValue old;
        int index = UtilHelper.indexForIntKey(key, this.entries.length);
        IntMapEntry<TValue> e = this.entries[index];
        while (e != null) {
            if (e.key == key) {
                old = e.value;
                e.value = value;
                return old;
            }
            e = e.next;
        }

        e = new IntMapEntry<TValue>(key, value, this.entries[index]);
        this.modCount++;
        this.entries[index] = e;
        this.size++;
        return null;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (this.entries == null) {
            this.entries = new IntMapEntry[4];
            return;
        }
        if (this.size >= this.entries.length * loadFactor) {
            this.modCount++;
            final int newSize = this.entries.length << 1;
            IntMapEntry<TValue>[] newSpine = new IntMapEntry[newSize];
            IntMapEntry<TValue> e, temp;
            int newIndex;
            for (int i = 0, len = this.entries.length; i < len; i++) {
                e = this.entries[i];
                while (e != null) {
                    temp = e.next;
                    newIndex = UtilHelper.indexForIntKey(e.key, newSize);
                    e.next = newSpine[newIndex];
                    newSpine[newIndex] = e;
                    e = temp;
                }
            }
            this.entries = newSpine;
        }
    }

    public TValue remove(int key) {
        TValue removed = null;
        if (this.size > 0) {
            int index = UtilHelper.indexForIntKey(key, this.entries.length);
            IntMapEntry<TValue> e = this.entries[index], last = null;
            while (e != null) {
                if (e.key == key) {
                    this.modCount++;
                    if (last == null) {
                        this.entries[index] = e.next;
                    } else {
                        last.next = e.next;
                    }
                    this.size--;
                    removed = e.value;
                    break;
                }
                last = e;
                e = e.next;
            }
        }
        // if (entries != null && this.size < entries.length / loadFactor) {
        // this.trim();
        // }
        return removed;
    }

    // /////////////////////////////////////////////////////////////////////

    public void visitAll(ValueVisitor<TValue> visitor) {
        if (visitor == null) {
            throw new NullArgumentException("visitor");
        }
        if (this.size > 0) {
            final int expectedModCount = this.modCount;
            IntMapEntry<TValue> entry;
            for (int i = 0, len = this.entries.length; i < len; i++) {
                if (expectedModCount != this.modCount) {
                    throw new ConcurrentModificationException();
                }
                entry = this.entries[i];
                while (entry != null) {
                    visitor.visit(entry.key, entry.value);
                    if (expectedModCount != this.modCount) {
                        throw new ConcurrentModificationException();
                    }
                    entry = entry.next;
                }
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        StringBuilder map = new StringBuilder("{");
        if (this.size > 0) {
            final int expectedModCount = this.modCount;
            IntMapEntry<TValue> entry;
            for (int i = 0, len = this.entries.length; i < len; i++) {
                if (expectedModCount != this.modCount) {
                    throw new ConcurrentModificationException();
                }
                entry = this.entries[i];
                while (entry != null) {
                    map.append(entry.key);
                    map.append('=');
                    map
                            .append(entry.value == this ? "(this map)"
                                    : entry.value);
                    map.append(", ");
                    if (expectedModCount != this.modCount) {
                        throw new ConcurrentModificationException();
                    }
                    entry = entry.next;
                }
            }
            if (map.length() >= 3) {
                map.deleteCharAt(map.length() - 1);
                map.deleteCharAt(map.length() - 1);
            }
        }
        map.append("}");
        return map.toString();
    }
}
