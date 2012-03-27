/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StreamBasedDataDeserializer.java
 * Date 2009-3-24
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class StreamBasedDataDeserializer implements DataDeserializer {
    /** the underlying input stream */
    private final PeekableInputStream in;
    /** the bytes' order */
    private final Endianness endian;

    private static final int MAX_BUF_SIZE = 8;
    private final byte[] buf = new byte[MAX_BUF_SIZE];

    StreamBasedDataDeserializer(InputStream in, Endianness endian) {
        this.in = new PeekableInputStream(in);
        this.endian = endian.tryUnsafe();
    }

    /**
     * Read but not consume a byte.
     */
    public final byte peekByte() throws IOException {
        return this.in.peek();
    }

    public final void close() throws IOException {
        this.in.close();
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        int read = 0;
        while (len > 0) {
            read = this.in.read(b, off, len);
            off += read;
            len -= read;
        }
    }

    public final boolean readBoolean() throws IOException {
        return (this.readByte() != 0);
    }

    public final byte readByte() throws IOException {
        return this.in.readByte();
    }

    public final int readUnsignedByte() throws IOException {
        return (this.readByte() & 0x000000FF);
    }

    public final char readChar() throws IOException {
        this.readFully(this.buf, 0, 2);
        return this.endian.getChar(this.buf, 0);
    }

    public final double readDouble() throws IOException {
        this.readFully(this.buf, 0, 8);
        return this.endian.getDouble(this.buf, 0);
    }

    public final float readFloat() throws IOException {
        this.readFully(this.buf, 0, 4);
        return this.endian.getFloat(this.buf, 0);
    }

    public final int readInt() throws IOException {
        this.readFully(this.buf, 0, 4);
        return this.endian.getInt(this.buf, 0);
    }

    public final long readLong() throws IOException {
        this.readFully(this.buf, 0, 8);
        return this.endian.getLong(this.buf, 0);
    }

    public final short readShort() throws IOException {
        this.readFully(this.buf, 0, 2);
        return this.endian.getShort(this.buf, 0);
    }

    public final void readFully(byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }
}
