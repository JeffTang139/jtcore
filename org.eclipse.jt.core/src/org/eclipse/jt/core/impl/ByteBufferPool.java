/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferPool.java
 * Date 2009-3-2
 */
package org.eclipse.jt.core.impl;

import java.nio.ByteBuffer;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ByteBufferPool {
    static final int DEFAULT_BUF_SIZE = 8192 * 10;
    static final int DEFAULT_MIN_CAPACITY = 4;

    private int size;
    private int capacity;

    private final int bufSize;

    ByteBufferPool() {
        this.capacity = DEFAULT_MIN_CAPACITY;
        this.bufSize = DEFAULT_BUF_SIZE;
    }

    ByteBufferPool(int capacity, int bufSize) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("给定的容量（capacity）值不合法："
                    + capacity);
        }
        if (bufSize <= 0) {
            bufSize = DEFAULT_BUF_SIZE;
        }
        this.capacity = capacity;
        this.bufSize = bufSize;
    }

    /**
     * 如果现有容量比指定的容量小，则把容量扩大到指定的容量。
     * 
     * @param minCapacity
     *            指定的最小容量。
     */
    final synchronized void ensureCapacity(int minCapacity) {
        if (minCapacity > this.capacity) {
            this.capacity = minCapacity;
        } else if (minCapacity * 4 < this.capacity) {
            if (minCapacity < DEFAULT_MIN_CAPACITY) {
                this.capacity = DEFAULT_MIN_CAPACITY;
            } else {
                this.capacity = minCapacity;
            }
        }
        this.notify();
    }

    final synchronized ByteBufferWrapper get() {
        while (this.pool.empty() && this.size >= this.capacity) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw Utils.tryThrowException(e);
            }
        }

        ByteBufferWrapper buf = null;
        if (!this.pool.empty()) {
            buf = this.pool.pop();
            buf.clear();
        } else if (this.size < this.capacity) {
            buf = new ByteBufferWrapper(this, ByteBuffer
                    .allocateDirect(this.bufSize));
            this.size++;
        }

        if (buf == null) {
            throw new InternalError();
        }
        return buf;
    }

    private final synchronized void free(ByteBufferWrapper buf) {
        if (buf != null) {
            Assertion
                    .ASSERT(!(buf instanceof ByteBufferWrapper.ReadOnlyByteBufferWrapper));
            int plsize = this.pool.size();
            if (plsize < this.capacity) {
                this.pool.push(buf);
            } else if (plsize < this.size) {
                buf = null;
                this.size--;
            } else {
                if (this.pool.contains(buf)) {
                    throw new UnsupportedOperationException("不能重复释放字节缓冲区");
                } else {
                    throw new UnsupportedOperationException("不能识别的字节缓冲区");
                }
            }
        }
        this.notify();
    }

    /**
     * 字节缓冲区包装对象。
     * 
     * @author Jeff Tang
     * @version 1.0
     */
    static class ByteBufferWrapper {
        private final ByteBufferPool pool;
        final ByteBuffer buffer;
        private Set<ReadOnlyByteBufferWrapper> viewers;

        private ByteBufferWrapper(ByteBufferPool pool, ByteBuffer byteBuffer) {
            if (byteBuffer == null) {
                throw new NullArgumentException("byteBuffer");
            }
            this.pool = pool;
            this.buffer = byteBuffer;
        }

        private void clear() {
            this.buffer.clear();
            this.viewers = null;
        }

        synchronized ByteBufferWrapper newSpecialViewer() {
            if (this.viewers == null) {
                this.viewers = new HashSet<ReadOnlyByteBufferWrapper>();
            }
            ReadOnlyByteBufferWrapper viewer = new ReadOnlyByteBufferWrapper(
                    this);
            this.viewers.add(viewer);
            return viewer;
        }

        synchronized void free() {
            while (this.viewers != null && this.viewers.size() > 0) {
                return;
            }
            this.viewers = null;
            this.pool.free(this);
        }

        private synchronized void freeSpecialViewer(
                ReadOnlyByteBufferWrapper viewer) {
            if (viewer != null) {
                Assertion.ASSERT(viewer.wrapper == this);
                if (this.viewers != null) {
                    this.viewers.remove(viewer);
                    if (this.viewers.size() == 0) {
                        this.viewers = null;
                    }
                }
                if (this.viewers == null) {
                    this.pool.free(viewer.wrapper);
                }
            }
        }

        /**
         * 只读字节缓冲区包装对象
         * 
         * @author Jeff Tang
         * @version 1.0
         */
        private static final class ReadOnlyByteBufferWrapper extends
                ByteBufferWrapper {
            final ByteBufferWrapper wrapper;

            ReadOnlyByteBufferWrapper(ByteBufferWrapper byteBufferWrapper) {
                super(null, byteBufferWrapper.buffer.asReadOnlyBuffer());
                if (byteBufferWrapper instanceof ReadOnlyByteBufferWrapper) {
                    throw new IllegalArgumentException(
                            "Unsupported wrapper type: "
                                    + byteBufferWrapper.getClass().getName());
                }
                this.wrapper = byteBufferWrapper;
            }

            @Override
            ByteBufferWrapper newSpecialViewer() {
                return this.wrapper.newSpecialViewer();
            }

            @Override
            void free() {
                this.wrapper.freeSpecialViewer(this);
            }
        }
    }

    private final Stack<ByteBufferWrapper> pool = new Stack<ByteBufferWrapper>() {
        private final LinkedList<ByteBufferWrapper> items = new LinkedList<ByteBufferWrapper>();

        public void clear() {
            this.items.clear();
        }

        public boolean contains(Object o) {
            return this.items.contains(o);
        }

        public boolean empty() {
            return this.items.isEmpty();
        }

        public ByteBufferWrapper peek() throws EmptyStackException {
            if (this.items.isEmpty()) {
                throw new EmptyStackException();
            }
            return this.items.getFirst();
        }

        public ByteBufferWrapper pop() throws EmptyStackException {
            if (this.items.isEmpty()) {
                throw new EmptyStackException();
            }
            return this.items.removeFirst();
        }

        public ByteBufferWrapper push(ByteBufferWrapper item) {
            this.items.addFirst(item);
            return item;
        }

        public int size() {
            return this.items.size();
        }
    };
}
