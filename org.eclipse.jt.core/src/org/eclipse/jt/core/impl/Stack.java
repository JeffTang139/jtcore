/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Stack.java
 * Date 2009-4-23
 */
package org.eclipse.jt.core.impl;

import java.util.EmptyStackException;

/**
 * The <code>Stack</code> interface represents a last-in-first-out (LIFO) stack
 * of objects.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface Stack<E> {
    /**
     * Tests if this stack is empty.
     * 
     * @return <code>true</code> if and only if this stack contains no items;
     *         <code>false</code> otherwise.
     */
    boolean empty();

    /**
     * Returns <tt>true</tt> if this stack contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this collection contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null ? e==null : o.equals(e))</tt>.
     * 
     * @param o
     *            element whose presence in this stack is to be tested.
     * @return <tt>true</tt> if this stack contains the specified element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this stack (optional).
     * @throws NullPointerException
     *             if the specified element is null and this stack does not
     *             support null elements (optional).
     */
    boolean contains(Object o);

    /**
     * Removes all of the items from this stack (optional operation). This stack
     * will be empty after this method returns unless it throws an exception.
     * 
     * @throws UnsupportedOperationException
     *             if the <tt>clear<tt> method is not supported by this stack.
     */
    void clear();

    /**
     * Pushes an item onto the top of this stack.
     * 
     * @param item
     *            the item to be pushed onto this stack.
     * @return the <code>item</code> argument.
     */
    E push(E item);

    /**
     * Removes the object at the top of this stack and returns that object as
     * the value of this function.
     * 
     * @return The object at the top of this stack.
     * @throws EmptyStackException
     *             if this stack is empty.
     */
    E pop() throws EmptyStackException;

    /**
     * Looks at the object at the top of this stack without removing it from the
     * stack.
     * 
     * @return the object at the top of this stack.
     * @throws EmptyStackException
     *             if this stack is empty.
     */
    E peek() throws EmptyStackException;

    /**
     * Returns the number of items in this stack.
     * 
     * @return the number of items in this stack.
     */
    int size();
}
