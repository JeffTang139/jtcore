/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterNodeInfoMap.java
 * Date 2009-6-5
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class NetNodeInfoMap<TKey> {
    private static final float loadFactor = 2.0f;
    private Entry<TKey>[] entries;
    private int size;

    private static class Entry<TKey> {
        private final TKey key;
        private NetNodeInfo value;
        private Entry<TKey> next;

        Entry(TKey key, NetNodeInfo value, Entry<TKey> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        boolean equal(TKey key, int port) {
            if (port != this.value.getPort()) {
                return false;
            }
            if (key == this.key) {
                return true;
            }
            return key.equals(this.key);
        }
    }

    final NetNodeInfo get(final TKey key, final int port) {
        if (key != null && this.size > 0) {
            int index = UtilHelper.indexForObjectKey(key, this.entries.length);
            Entry<TKey> e = this.entries[index];
            while (e != null) {
                if (e.equal(key, port)) {
                    return e.value;
                }
                e = e.next;
            }
        }
        return null;
    }

    final NetNodeInfo put(final TKey key, final NetNodeInfo nodeInfo) {
        this.ensureCapacity();
        int index = UtilHelper.indexForObjectKey(key, this.entries.length);
        Entry<TKey> e = this.entries[index];
        while (e != null) {
            if (e.value == nodeInfo) {
                return nodeInfo;
            } else if (e.equal(key, nodeInfo.getPort())) {
                NetNodeInfo old = e.value;
                e.value = nodeInfo;
                return old;
            }
            e = e.next;
        }
        this.entries[index] = new Entry<TKey>(key, nodeInfo,
                this.entries[index]);
        this.size++;
        return null;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (this.entries == null) {
            this.entries = new Entry[4];
            return;
        }
        if (this.size >= this.entries.length * loadFactor) {
            final int newSize = this.entries.length << 1;
            Entry<TKey>[] newSpine = new Entry[newSize];
            Entry<TKey> e, temp;
            int newIndex;
            for (int i = 0, len = this.entries.length; i < len; i++) {
                e = this.entries[i];
                while (e != null) {
                    temp = e.next;
                    newIndex = UtilHelper.indexForObjectKey(e.key, newSize);
                    e.next = newSpine[newIndex];
                    newSpine[newIndex] = e;
                    e = temp;
                }
            }
            this.entries = newSpine;
        }
    }
}
