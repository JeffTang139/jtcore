/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IntKeyTable.java
 * Date 2009-5-25
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class IntKeyTable<TValue> extends IntKeyMap<TValue> {

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @Override
    public synchronized TValue get(int key) {
        return super.get(key);
    }

    @Override
    public synchronized TValue put(int key, TValue value) {
        return super.put(key, value);
    }

    @Override
    public synchronized TValue remove(int key) {
        return super.remove(key);
    }

    @Override
    public synchronized void visitAll(ValueVisitor<TValue> visitor) {
        super.visitAll(visitor);
    }
}
