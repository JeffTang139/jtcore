/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ArrayList.java
 * Date 2009-8-24
 */
package org.eclipse.jt.core.impl;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.RandomAccess;

/**
 * @see java.util.ArrayList
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaArrayList<E> extends AbstractList<E> implements List<E>,
        RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * The array buffer into which the elements of the ArrayList are stored. The
     * capacity of the ArrayList is the length of this array buffer.
     */
    private transient E[] elementData;

    /**
     * The size of the ArrayList (the number of elements it contains).
     * 
     * @serial
     */
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     * 
     * @param initialCapacity
     *            the initial capacity of the list.
     * @exception IllegalArgumentException
     *                if the specified initial capacity is negative
     */
    @SuppressWarnings("unchecked")
    public DnaArrayList(int initialCapacity) {
        super();
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "
                    + initialCapacity);
        }
        this.elementData = (E[]) new Object[initialCapacity];
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public DnaArrayList() {
        this(10);
    }

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator. The
     * <tt>ArrayList</tt> instance has an initial capacity of 110% the size of
     * the specified collection.
     * 
     * @param c
     *            the collection whose elements are to be placed into this list.
     * @throws NullPointerException
     *             if the specified collection is null.
     */
    @SuppressWarnings("unchecked")
    public DnaArrayList(Collection<? extends E> c) {
        this.size = c.size();
        // Allow 10% room for growth
        int capacity = (int) Math.min((this.size * 110L) / 100,
                Integer.MAX_VALUE);
        this.elementData = (E[]) c.toArray(new Object[capacity]);
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's
     * current size. An application can use this operation to minimize the
     * storage of an <tt>ArrayList</tt> instance.
     */
    @SuppressWarnings("unchecked")
    public void trimToSize() {
        this.modCount++;
        int oldCapacity = this.elementData.length;
        if (this.size < oldCapacity) {
            Object oldData[] = this.elementData;
            this.elementData = (E[]) new Object[this.size];
            System.arraycopy(oldData, 0, this.elementData, 0, this.size);
        }
    }

    /**
     * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary,
     * to ensure that it can hold at least the number of elements specified by
     * the minimum capacity argument.
     * 
     * @param minCapacity
     *            the desired minimum capacity.
     */
    @SuppressWarnings("unchecked")
    public void ensureCapacity(int minCapacity) {
        this.modCount++;
        int oldCapacity = this.elementData.length;
        if (minCapacity > oldCapacity) {
            Object oldData[] = this.elementData;
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            this.elementData = (E[]) new Object[newCapacity];
            System.arraycopy(oldData, 0, this.elementData, 0, this.size);
        }
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Tests if this list has no elements.
     * 
     * @return <tt>true</tt> if this list has no elements; <tt>false</tt>
     *         otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * 
     * @param elem
     *            element whose presence in this List is to be tested.
     * @return <code>true</code> if the specified element is present;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean contains(Object elem) {
        return this.indexOf(elem) >= 0;
    }

    /**
     * Searches for the first occurence of the given argument, testing for
     * equality using the <tt>equals</tt> method.
     * 
     * @param elem
     *            an object.
     * @return the index of the first occurrence of the argument in this list;
     *         returns <tt>-1</tt> if the object is not found.
     * @see Object#equals(Object)
     */
    @Override
    public int indexOf(Object elem) {
        if (elem == null) {
            for (int i = 0; i < this.size; i++) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            Object item;
            for (int i = 0; i < this.size; i++) {
                item = this.elementData[i];
                if (elem == item || elem.equals(item)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified object in this
     * list.
     * 
     * @param elem
     *            the desired element.
     * @return the index of the last occurrence of the specified object in this
     *         list; returns -1 if the object is not found.
     */
    @Override
    public int lastIndexOf(Object elem) {
        if (elem == null) {
            for (int i = this.size - 1; i >= 0; i--) {
                if (this.elementData[i] == null) {
                    return i;
                }
            }
        } else {
            Object item;
            for (int i = this.size - 1; i >= 0; i--) {
                item = this.elementData[i];
                if (elem == item || elem.equals(item)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance. (The elements
     * themselves are not copied.)
     * 
     * @return a clone of this <tt>ArrayList</tt> instance.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            DnaArrayList<E> v = (DnaArrayList<E>) super.clone();
            v.elementData = (E[]) new Object[this.size];
            System.arraycopy(this.elementData, 0, v.elementData, 0, this.size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns an array containing all of the elements in this list in the
     * correct order.
     * 
     * @return an array containing all of the elements in this list in the
     *         correct order.
     */
    @Override
    public Object[] toArray() {
        Object[] result = new Object[this.size];
        System.arraycopy(this.elementData, 0, result, 0, this.size);
        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in the
     * correct order; the runtime type of the returned array is that of the
     * specified array. If the list fits in the specified array, it is returned
     * therein. Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this list.
     * <p>
     * 
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the collection is set to <tt>null</tt>.
     * This is useful in determining the length of the list <i>only</i> if the
     * caller knows that the list does not contain any <tt>null</tt> elements.
     * 
     * @param a
     *            the array into which the elements of the list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list.
     * @throws ArrayStoreException
     *             if the runtime type of a is not a supertype of the runtime
     *             type of every element in this list.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < this.size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                    .getComponentType(), this.size);
        }
        System.arraycopy(this.elementData, 0, a, 0, this.size);
        if (a.length > this.size) {
            a[this.size] = null;
        }
        return a;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of element to return.
     * @return the element at the specified position in this list.
     * @throws IndexOutOfBoundsException
     *             if index is out of range <tt>(index
     *        &lt; 0 || index &gt;= size())</tt>.
     */
    @Override
    public E get(int index) {
        this.RangeCheck(index);

        return this.elementData[index];
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws IndexOutOfBoundsException
     *             if index out of range
     *             <tt>(index &lt; 0 || index &gt;= size())</tt>.
     */
    @Override
    public E set(int index, E element) {
        this.RangeCheck(index);

        E oldValue = this.elementData[index];
        this.elementData[index] = element;
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * @param o
     *            element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    @Override
    public boolean add(E o) {
        this.ensureCapacity(this.size + 1); // Increments modCount!!
        this.elementData[this.size++] = o;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     * 
     * @param index
     *            index at which the specified element is to be inserted.
     * @param element
     *            element to be inserted.
     * @throws IndexOutOfBoundsException
     *             if index is out of range
     *             <tt>(index &lt; 0 || index &gt; size())</tt>.
     */
    @Override
    public void add(int index, E element) {
        if (index > this.size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + this.size);
        }

        this.ensureCapacity(this.size + 1); // Increments modCount!!
        System.arraycopy(this.elementData, index, this.elementData, index + 1,
                this.size - index);
        this.elementData[index] = element;
        this.size++;
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * 
     * @param index
     *            the index of the element to removed.
     * @return the element that was removed from the list.
     * @throws IndexOutOfBoundsException
     *             if index out of range <tt>(index
     *        &lt; 0 || index &gt;= size())</tt>.
     */
    @Override
    public E remove(int index) {
        this.RangeCheck(index);

        this.modCount++;
        E oldValue = this.elementData[index];

        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData,
                    index, numMoved);
        }
        this.elementData[--this.size] = null; // Let gc do its work

        return oldValue;
    }

    /**
     * Removes a single instance of the specified element from this list, if it
     * is present (optional operation). More formally, removes an element
     * <tt>e</tt> such that <tt>(o==null ? e==null :
     * o.equals(e))</tt>, if the list contains one or more such elements.
     * Returns <tt>true</tt> if the list contained the specified element (or
     * equivalently, if the list changed as a result of the call).
     * <p>
     * 
     * @param o
     *            element to be removed from this list, if present.
     * @return <tt>true</tt> if the list contained the specified element.
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < this.size; index++) {
                if (this.elementData[index] == null) {
                    this.fastRemove(index);
                    return true;
                }
            }
        } else {
            Object item;
            for (int index = 0; index < this.size; index++) {
                item = this.elementData[index];
                if (o == item || o.equals(item)) {
                    this.fastRemove(index);
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not return the
     * value removed.
     */
    private void fastRemove(int index) {
        this.modCount++;
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData,
                    index, numMoved);
        }
        this.elementData[--this.size] = null; // Let gc do its work
    }

    /**
     * Removes all of the elements from this list. The list will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        this.modCount++;

        // Let gc do its work
        for (int i = 0; i < this.size; i++) {
            this.elementData[i] = null;
        }

        this.size = 0;
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this list, in the order that they are returned by the specified
     * Collection's Iterator. The behavior of this operation is undefined if the
     * specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this list, and this list is nonempty.)
     * 
     * @param c
     *            the elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws NullPointerException
     *             if the specified collection is null.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        this.ensureCapacity(this.size + numNew); // Increments modCount
        System.arraycopy(a, 0, this.elementData, this.size, numNew);
        this.size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified Collection into this list,
     * starting at the specified position. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (increases
     * their indices). The new elements will appear in the list in the order
     * that they are returned by the specified Collection's iterator.
     * 
     * @param index
     *            index at which to insert first element from the specified
     *            collection.
     * @param c
     *            elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws IndexOutOfBoundsException
     *             if index out of range <tt>(index
     *        &lt; 0 || index &gt; size())</tt>.
     * @throws NullPointerException
     *             if the specified Collection is null.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.RangeCheck(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        this.ensureCapacity(this.size + numNew); // Increments modCount

        int numMoved = this.size - index;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index, this.elementData, index
                    + numNew, numMoved);
        }

        System.arraycopy(a, 0, this.elementData, index, numNew);
        this.size += numNew;
        return numNew != 0;
    }

    /**
     * Removes from this List all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
     * elements to the left (reduces their index). This call shortens the list
     * by <tt>(toIndex - fromIndex)</tt> elements. (If
     * <tt>toIndex==fromIndex</tt>, this operation has no effect.)
     * 
     * @param fromIndex
     *            index of first element to be removed.
     * @param toIndex
     *            index after last element to be removed.
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > this.size || fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex
                    + ", Size: " + this.size);
        }
        if (toIndex > this.size || toIndex < 0) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex
                    + ", Size: " + this.size);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex: " + fromIndex
                    + ", toIndex: " + toIndex);
        }

        this.modCount++;
        int numMoved = this.size - toIndex;
        System.arraycopy(this.elementData, toIndex, this.elementData,
                fromIndex, numMoved);

        // Let gc do its work
        int newSize = this.size - (toIndex - fromIndex);
        while (this.size != newSize) {
            this.elementData[--this.size] = null;
        }
    }

    /**
     * Check if the given index is in range. If not, throw an appropriate
     * runtime exception. This method does *not* check if the index is negative:
     * It is always used immediately prior to an array access, which throws an
     * ArrayIndexOutOfBoundsException if index is negative.
     */
    private void RangeCheck(int index) {
        if (index >= this.size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + this.size);
        }
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that is,
     * serialize it).
     * 
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        int expectedModCount = this.modCount;
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length
        s.writeInt(this.elementData.length);

        // Write out all elements in the proper order.
        for (int i = 0; i < this.size; i++) {
            s.writeObject(this.elementData[i]);
        }

        if (this.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        Object[] a = this.elementData = (E[]) new Object[arrayLength];

        // Read in all elements in the proper order.
        for (int i = 0; i < this.size; i++) {
            a[i] = s.readObject();
        }
    }

    // /////////////////////////////////////////////////////////////////////////

    static final Object[] EMPTY_OBJECT_ARRAY = {};

    private DnaArrayList(E[] elementData, int size) {
        this.elementData = elementData;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    static <E> DnaArrayList<E> wrapData(E[] elementData, int size) {
        if (size == 0) {
            return new DnaArrayList(0);
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size: " + size);
        }
        if (elementData == null || elementData.length < size) {
            throw new IllegalArgumentException("elementsData中的元素个数不满足size的要求");
        }
        return (new DnaArrayList(elementData, size));
    }

    final E[] getElementData() {
        return this.elementData;
    }

    @SuppressWarnings("unchecked")
    final void copyData(E[] sourceData, int size) {
        if (size == 0) {
            if (this.size > 0) {
                this.modCount++;
                this.elementData = (E[]) EMPTY_OBJECT_ARRAY;
                this.size = 0;
            }
            return;
        } else if (size < 0) {
            throw new IllegalArgumentException("Size: " + size);
        }
        int length = sourceData.length;
        this.modCount++;
        if (length / size > 1) {
            this.elementData = (E[]) new Object[size];
            System.arraycopy(sourceData, 0, this.elementData, 0, size);
            this.size = size;
        } else {
            this.elementData = sourceData.clone();
            this.size = size;
        }
    }

    final void removeTail(final int startIndex) {
        if (startIndex == this.size) {
            return;
        }
        if (startIndex < 0 || startIndex > this.size) {
            throw new IndexOutOfBoundsException("startIndex: " + startIndex
                    + ", Size: " + this.size);
        }
        if (startIndex == 0) {
            this.clear();
            return;
        }
        this.modCount++;
        for (int i = this.size - 1; i >= startIndex; i--) {
            this.elementData[i] = null;
        }
        this.size = startIndex;
    }
}
