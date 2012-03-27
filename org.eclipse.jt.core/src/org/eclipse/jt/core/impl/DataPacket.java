/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataPacket.java
 * Date 2009-3-4
 */
package org.eclipse.jt.core.impl;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 数据包对象。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class DataPacket {
    static final byte[] EMPTY = new byte[0];

    final NetConnection connection;
    final byte code;
    final int requestId;

    private byte[] data;
    private int size;
    private int pos;

    DataPacket(NetConnection connection, byte code, int requestId, int dataLength) {
        if (connection == null) {
            throw new NullArgumentException("connection");
        }
        if (dataLength < 0) {
            throw new IllegalArgumentException("数据长度无效");
        }

        this.connection = connection;
        this.code = code;
        this.requestId = requestId;
        if (dataLength == 0) {
            this.data = EMPTY;
        } else {
            this.data = new byte[dataLength];
        }
    }

    final int capacity() {
        return this.data.length;
    }

    final int size() {
        return this.size;
    }

    final int position() {
        return this.pos;
    }

    final boolean isFull() { // if true then size == capacity
        return (this.size == this.data.length);
    }

    final void clear() {
        this.size = 0;
    }

    final void reset() {
        this.pos = 0;
    }

    final boolean hasRemaining() {
        return this.pos < this.size;
    }

    final int remaining() {
        return this.size - this.pos;
    }

    final int read(byte[] b, int off, int len) {
        // REMIND check range ?
        if (this.pos == this.size) {
            return 0;
        }

        int count = this.size - this.pos;
        if (count > len) {
            count = len;
        }
        System.arraycopy(this.data, this.pos, b, off, count);
        this.pos += count;
        return count;
    }

    final int put(ByteBuffer src) {
        int remaining = src.remaining();
        if (remaining == 0) {
            return 0;
        }
        int len = this.data.length - this.size;
        if (len == 0) {
            return 0;
        }
        len = len > remaining ? remaining : len;
        src.get(this.data, this.size, len);
        this.size += len;
        return len;
    }

    final byte get(int index) {
        return this.data[index];
    }

    final byte read() {
        if (this.pos == this.size) {
            throw new IndexOutOfBoundsException();
        }
        return this.data[this.pos++];
    }

    final int getInt(Endianness endian, int index) {
        if (index + 4 > this.size) {
            throw new BufferUnderflowException();
        }
        return endian.getInt(this.data, index);
    }

    final int readInt(Endianness endian) {
        if (this.pos + 4 > this.size) {
            throw new BufferUnderflowException();
        }
        int v = endian.getInt(this.data, this.pos);
        this.pos += 4;
        return v;
    }

    final long readLong(Endianness endian) {
        if (this.pos + 8 > this.size) {
            throw new BufferUnderflowException();
        }
        long v = endian.getLong(this.data, this.pos);
        this.pos += 8;
        return v;
    }

    final char readChar(Endianness endian) {
        if (this.pos + 2 > this.size) {
            throw new BufferUnderflowException();
        }
        char v = endian.getChar(this.data, this.pos);
        this.pos += 2;
        return v;
    }

    final short readShort(Endianness endian) {
        if (this.pos + 2 > this.size) {
            throw new BufferUnderflowException();
        }
        short v = endian.getShort(this.data, this.pos);
        this.pos += 2;
        return v;
    }

    final double readDouble(Endianness endian) {
        if (this.pos + 8 > this.size) {
            throw new BufferUnderflowException();
        }
        double v = endian.getDouble(this.data, this.pos);
        this.pos += 8;
        return v;
    }

    final float readFloat(Endianness endian) {
        if (this.pos + 4 > this.size) {
            throw new BufferUnderflowException();
        }
        float v = endian.getFloat(this.data, this.pos);
        this.pos += 4;
        return v;
    }

    final void work() {
        this.connection.handOut(this);
    }
}
