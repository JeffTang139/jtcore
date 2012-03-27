/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataPacketDeserializer.java
 * Date 2009-3-24
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 基于数据包工作的反序列化器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class DataPacketDeserializer implements DataDeserializer {
    /**
     * 数据包队列。
     */
    private final FinishableLinkedList<DataPacket> source;
    /**
     * 字节序。
     */
    private final Endianness endian;

    /**
     * 当前工作数据包。
     */
    private DataPacket working;

    /**
     * 给定的列表中的数据包不能有空的。
     * 
     * @param src
     *            数据包队列。
     */
    DataPacketDeserializer(FinishableLinkedList<DataPacket> src,
            Endianness endian) {
        if (src == null) {
            throw new NullArgumentException("src");
        }
        if (endian == null) {
            throw new NullArgumentException("endian");
        }
        this.source = src;
        this.endian = endian;
    }

    private DataPacket nextPacket() {
        synchronized (this.source) {
            while (this.source.isEmpty() && !this.source.finished()) {
                try {
                    this.source.wait();
                } catch (InterruptedException e) {
                    throw Utils.tryThrowException(e);
                }
            }
            if (this.source.isEmpty()) {
                this.working = null;
            } else {
                this.working = this.source.removeFirst();
            }
        }
        return this.working;
    }

    private void checkReadable() throws IOException {
        if (this.working == null || !this.working.hasRemaining()) {
            this.nextPacket();
            if ((this.working == null || !this.working.hasRemaining())
                    && this.source.finished()) {
                throw new StreamCorruptedException("unexpected end");
            }
        }
    }

    private final byte[] buf = new byte[8];

    private void readToBuf(int size) throws IOException {
        int read = this.working.read(this.buf, 0, size);
        while (read != size) {
            this.nextPacket();
            if ((this.working == null || !this.working.hasRemaining())
                    && this.source.finished()) {
                throw new StreamCorruptedException("unexpected end");
            }
            read += this.working.read(this.buf, read, size - read);
        }
    }

    public void close() throws IOException {
        Assertion.ASSERT(this.source.isEmpty()
                && (this.working == null || !this.working.hasRemaining()));
    }

    public byte peekByte() throws IOException {

        return this.working.get(this.working.position());
    }

    public boolean readBoolean() throws IOException {
        this.checkReadable();
        return this.working.read() != 0;
    }

    public byte readByte() throws IOException {
        this.checkReadable();
        return this.working.read();
    }

    public char readChar() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 2) {
            return this.working.readChar(this.endian);
        } else {
            this.readToBuf(2);
            return this.endian.getChar(this.buf, 0);
        }
    }

    public double readDouble() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 8) {
            return this.working.readDouble(this.endian);
        } else {
            this.readToBuf(8);
            return this.endian.getDouble(this.buf, 0);
        }
    }

    public float readFloat() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 4) {
            return this.working.readFloat(this.endian);
        } else {
            this.readToBuf(4);
            return this.endian.getFloat(this.buf, 0);
        }
    }

    public int readInt() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 4) {
            return this.working.readInt(this.endian);
        } else {
            this.readToBuf(4);
            return this.endian.getInt(this.buf, 0);
        }
    }

    public long readLong() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 8) {
            return this.working.readLong(this.endian);
        } else {
            this.readToBuf(8);
            return this.endian.getLong(this.buf, 0);
        }
    }

    public short readShort() throws IOException {
        this.checkReadable();
        if (this.working.remaining() >= 2) {
            return this.working.readShort(this.endian);
        } else {
            this.readToBuf(2);
            return this.endian.getShort(this.buf, 0);
        }
    }

    public int readUnsignedByte() throws IOException {
        return (this.readByte() & 0x000000FF);
    }

    public void readFully(byte[] b) throws IOException {
        if (b == null || b.length == 0) {
            return;
        }

        int read = 0, off = 0, len = b.length;
        while (len > 0) {
            this.checkReadable();
            read = this.working.read(b, off, len);
            off += read;
            len -= read;
        }
    }
}
