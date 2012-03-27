/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File BlockDataSerializer.java
 * Date 2009-3-24
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class StreamBasedDataSerializer implements DataSerializer {
    /** the underlying input stream */
    private final OutputStream out;
    /** the bytes' order */
    private final Endianness endian;

    /** maximum data buf length */
    private static final int MAX_BUF_SIZE = 1024;
    /** buffer for writing data */
    private final byte[] buf = new byte[MAX_BUF_SIZE];

    /** current offset into buf */
    private int pos = 0;

    StreamBasedDataSerializer(OutputStream out, Endianness endian) {
        this.out = out;
        this.endian = endian.tryUnsafe();
    }

    /**
     * Writes all buffered data from this stream to the underlying stream, but
     * does not flush underlying stream.
     */
    private void drain() throws IOException {
        if (this.pos == 0) {
            return;
        }
        this.out.write(this.buf, 0, this.pos);
        this.pos = 0;
    }

    public final void close() throws IOException {
        this.flush();
        this.out.close();
    }

    public final void flush() throws IOException {
        this.drain();
        this.out.flush();
    }

    private void write0(byte[] b, int off, int len) throws IOException {
        this.drain();
        this.out.write(b, off, len);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        this.write0(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        if (b == null || b.length == 0) {
            return;
        }
        this.write0(b, 0, b.length);
    }

    public void writeBoolean(boolean v) throws IOException {
        this.writeByte((byte) (v ? 1 : 0));
    }

    public void writeByte(byte v) throws IOException {
        if (this.pos >= MAX_BUF_SIZE) {
            this.drain();
        }
        this.buf[this.pos++] = v;
    }

    public void writeChar(char v) throws IOException {
        if (this.pos + 2 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putChar(this.buf, this.pos, v);
        this.pos += 2;
    }

    public void writeDouble(double v) throws IOException {
        if (this.pos + 8 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putDouble(this.buf, this.pos, v);
        this.pos += 8;
    }

    public void writeFloat(float v) throws IOException {
        if (this.pos + 4 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putFloat(this.buf, this.pos, v);
        this.pos += 4;
    }

    public void writeInt(int v) throws IOException {
        if (this.pos + 4 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putInt(this.buf, this.pos, v);
        this.pos += 4;
    }

    public void writeLong(long v) throws IOException {
        if (this.pos + 8 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putLong(this.buf, this.pos, v);
        this.pos += 8;
    }

    public void writeShort(short v) throws IOException {
        if (this.pos + 2 > MAX_BUF_SIZE) {
            this.drain();
        }
        this.endian.putShort(this.buf, this.pos, v);
        this.pos += 2;
    }
}
