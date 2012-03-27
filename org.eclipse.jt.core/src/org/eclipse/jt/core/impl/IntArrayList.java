/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IntArrayList.java
 * Date Dec 2, 2009
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class IntArrayList {
    private int[] elements;
    private int size;

    public IntArrayList() {
        this.elements = new int[10];
    }

    public IntArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "
                    + initialCapacity);
        }
        this.elements = new int[initialCapacity];
    }

    private void rangeCheck(int index) {
        if (index >= this.size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + this.size);
        }
    }

    public void ensureCapacity(int minCapacity) {
        int oldc = this.elements.length;
        if (minCapacity > oldc) {
            int[] old = this.elements;
            int newc = (oldc * 3) / 2 + 1;
            if (newc < minCapacity) {
                newc = minCapacity;
            }
            this.elements = new int[newc];
            System.arraycopy(old, 0, this.elements, 0, this.size);
        }
    }

    public void add(int element) {
        this.ensureCapacity(this.size + 1);
        this.elements[this.size++] = element;
    }

    public int get(int index) {
        this.rangeCheck(index);
        return this.elements[index];
    }

    public int remove(int index) {
        this.rangeCheck(index);
        int e = this.elements[index];
        int tomove = this.size - index - 1;
        if (tomove > 0) {
            System.arraycopy(this.elements, index + 1, this.elements, index,
                    tomove);
        }
        this.elements[--this.size] = 0;
        return e;
    }

    public int set(int index, int newValue) {
        this.rangeCheck(index);
        int oldValue = this.elements[index];
        this.elements[index] = newValue;
        return oldValue;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
