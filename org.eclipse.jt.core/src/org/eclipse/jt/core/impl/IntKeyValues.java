/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IntKeyMap.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class IntKeyValues<TValue extends IntKeyHashable<TValue>> {
    private static final float loadFactor = 2.0f;
    private TValue[] values;
    private int size;

    final synchronized void clear() {
        this.values = null;
        this.size = 0;
    }

    final synchronized void visitAll(ValueVisitor<TValue> visitor) {
        if (this.size > 0) {
            TValue value;
            for (int i = 0, len = this.values.length; i < len; i++) {
                value = this.values[i];
                while (value != null) {
                    visitor.visit(value.id(), value);
                    value = value.next();
                }
            }
        }
    }

    final synchronized TValue get(final int id) {
        if (this.size == 0) {
            return null;
        }
        int index = UtilHelper.indexForIntKey(id, this.values.length);
        TValue v = this.values[index];
        while (v != null) {
            if (v.id() == id) {
                return v;
            }
            v = v.next();
        }
        return null;
    }

    final synchronized void put(final TValue value) {
        if (value == null) {
            throw new NullArgumentException("value");
        }
        this.ensureCapacity();
        int index = UtilHelper.indexForIntKey(value.id(), this.values.length);
        TValue v = this.values[index];
        while (v != null) {
            if (v.id() == value.id()) {
                Assertion.ASSERT(v == value, "不同的对象具有了相同的ID");
                return;
            }
            v = v.next();
        }
        value.setNext(this.values[index]);
        this.values[index] = value;
        this.size++;
    }

    final synchronized TValue remove(final int id) {
        TValue result = null;
        if (this.size > 0) {
            int index = UtilHelper.indexForIntKey(id, this.values.length);
            TValue v = this.values[index], last = null;
            while (v != null) {
                if (v.id() == id) {
                    if (last == null) {
                        this.values[index] = v.next();
                    } else {
                        last.setNext(v.next());
                    }
                    this.size--;
                    result = v;
                    break;
                }
                last = v;
                v = v.next();
            }
        }
        if (this.values != null && this.size < this.values.length / loadFactor) {
            this.trim();
        }
        return result;
    }

    final synchronized void remove(final TValue value) {
        if (value != null) {
            this.remove(value.id());
        }
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (this.values == null) {
            this.values = (TValue[]) (new IntKeyHashable[4]);
            return;
        }
        if (this.size >= this.values.length * loadFactor) {
            final int newSize = this.values.length << 1;
            TValue[] newSpine = (TValue[]) (new IntKeyHashable[newSize]);
            TValue v, temp;
            int newIndex;
            for (int i = 0, len = this.values.length; i < len; i++) {
                v = this.values[i];
                while (v != null) {
                    temp = v.next();
                    newIndex = UtilHelper.indexForIntKey(v.id(), newSize);
                    v.setNext(newSpine[newIndex]);
                    newSpine[newIndex] = v;
                    v = temp;
                }
            }
            this.values = newSpine;
        }
    }

    @SuppressWarnings("unchecked")
    private void trim() {
        if (this.values != null) {
            final int newSize = (this.size >>> 1);
            if (newSize < this.values.length) {
                TValue[] newSpine = (TValue[]) (new IntKeyHashable[newSize]);
                TValue v, temp;
                int newIndex;
                for (int i = 0, len = this.values.length; i < len; i++) {
                    v = this.values[i];
                    while (v != null) {
                        temp = v.next();
                        newIndex = UtilHelper.indexForIntKey(v.id(), newSize);
                        v.setNext(newSpine[newIndex]);
                        newSpine[newIndex] = v;
                        v = temp;
                    }
                }
                this.values = newSpine;
            }
        }
    }
}
