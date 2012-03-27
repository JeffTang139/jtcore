/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File SortedInts.java
 * Date 2009-4-14
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class SortedIntSet {
    private int[] values;
    private int size;

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            if (this.values == null) {
                this.values = new int[minCapacity];
            } else if (minCapacity > this.values.length) {
                int newCap = (this.size * 3) / 2 + 1;
                if (newCap < minCapacity) {
                    newCap = minCapacity;
                }
                int[] newVs = new int[newCap];
                System.arraycopy(this.values, 0, newVs, 0, this.size);
                this.values = newVs;
            }
        }
    }

    private void trimToCapacity(int tryCapacity) {
        if (this.size == 0) {
            this.values = null;
        } else if (tryCapacity > 0) {
            if (tryCapacity < this.size) {
                tryCapacity = this.size;
            }
            if (tryCapacity < this.values.length) {
                int[] newVs = new int[tryCapacity];
                System.arraycopy(this.values, 0, newVs, 0, this.size);
                this.values = newVs;
            }
        }
    }

    private int indexOf(int v) {
        if (this.size > 0) {
            int low = 0, high = this.size - 1;
            int pos = high / 2;
            while (low < high) {
                if (this.values[pos] == v) {
                    return pos;
                }
                if (this.values[pos] < v) {
                    low = pos + 1;
                } else {
                    high = pos - 1;
                }
                pos = (low + high) / 2;
            }
        }
        return -1;
    }

    private void removeAt(int index) {
        if (this.size > 0 && index >= 0 && index < this.size) {
            System.arraycopy(this.values, index + 1, this.values, index,
                    this.size - index - 1);
            this.size--;
            this.values[this.size] = 0;
            if (this.size < (this.values.length / 4)) {
                this.trimToCapacity(this.values.length / 4);
            }
        }
    }

    synchronized void put(int v) {
        if (this.size == 0) {
            this.ensureCapacity(this.size + 1);
            this.values[0] = v;
        } else {
            int low = 0, high = this.size - 1;
            int pos = high / 2;
            while (low < high) {
                if (this.values[pos] == v) {
                    return;
                }
                if (this.values[pos] < v) {
                    low = pos + 1;
                } else {
                    high = pos - 1;
                }
                pos = (low + high) / 2;
            }

            this.ensureCapacity(this.size + 1);
            if (low < this.size) {
                if (this.values[low] < v) {
                    low++;
                }
                System.arraycopy(this.values, low, this.values, low + 1,
                        this.size - low);
            } else if (low > this.size) {
                low = this.size;
            }
            this.values[low] = v;
        }
        this.size++;
    }

    synchronized void clear() {
        this.values = null;
        this.size = 0;
    }

    synchronized boolean contains(int v) {
        return (this.indexOf(v) >= 0);
    }

    synchronized boolean isEmpty() {
        return (this.size == 0);
    }

    synchronized void remove(int v) {
        this.removeAt(this.indexOf(v));
    }

    synchronized int size() {
        return this.size;
    }
}
