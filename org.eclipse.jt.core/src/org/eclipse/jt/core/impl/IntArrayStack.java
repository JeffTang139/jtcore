/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IntArrayStack.java
 * Date Dec 2, 2009
 */
package org.eclipse.jt.core.impl;

import java.util.EmptyStackException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class IntArrayStack {
    private int[] stack;
    private int top = -1;

    public IntArrayStack() {
        this.stack = new int[10];
    }

    public IntArrayStack(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "
                    + initialCapacity);
        }
        this.stack = new int[initialCapacity];
    }

    public void trimToSize() {
        int size = this.top + 1;
        if (size < this.stack.length) {
            int[] old = this.stack;
            this.stack = new int[size];
            if (size > 0) {
                System.arraycopy(old, 0, this.stack, 0, size);
            }
        }
    }

    public boolean empty() {
        return (this.top < 0);
    }

    public void clear() {
        this.top = -1;
    }

    public int push(final int item) {
        final int size = ++this.top;
        if (size >= this.stack.length) {
            int newc = this.stack.length * 3 / 2 + 1;
            int[] old = this.stack;
            this.stack = new int[newc];
            System.arraycopy(old, 0, this.stack, 0, size);
        }
        this.stack[this.top] = item;
        return item;
    }

    public int setTop(int topValue) throws EmptyStackException {
        if (this.top < 0) {
            throw new EmptyStackException();
        }
        int old = this.stack[this.top];
        this.stack[this.top] = topValue;
        return old;
    }

    public int pop() throws EmptyStackException {
        if (this.top < 0) {
            throw new EmptyStackException();
        }
        return this.stack[this.top--];
    }

    public int peek() throws EmptyStackException {
        if (this.top < 0) {
            throw new EmptyStackException();
        }
        return this.stack[this.top];
    }

    public int size() {
        return this.top + 1;
    }
}
