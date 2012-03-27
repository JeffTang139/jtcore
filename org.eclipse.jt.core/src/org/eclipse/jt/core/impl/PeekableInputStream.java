/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File PeekableInputStream.java
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
final class PeekableInputStream extends InputStream {
    private final InputStream in;

    /** peeked byte */
    private int peekb = -1;

    PeekableInputStream(InputStream in) {
        this.in = in;
    }

    final byte peek() throws IOException {
        return (byte) ((this.peekb < 0) ? (this.peekb = (this.in.read() & 0xFF))
                : this.peekb);
    }

    @Override
    public final int read() throws IOException {
        if (this.peekb < 0) {
            return (this.in.read() & 0xFF);
        } else {
            int v = this.peekb;
            this.peekb = -1;
            return v;
        }
    }

    final byte readByte() throws IOException {
        if (this.peekb < 0) {
            return (byte) (this.in.read() & 0xFF);
        } else {
            byte v = (byte) this.peekb;
            this.peekb = -1;
            return v;
        }
    }

    final int readUnsignedByte() throws IOException {
        if (this.peekb < 0) {
            return (this.in.read() & 0xFF);
        } else {
            int v = this.peekb;
            this.peekb = -1;
            return v;
        }
    }

    @Override
    public final int read(byte[] b, int off, int len) throws IOException {
        // REMIND check range ?
        if (this.peekb < 0) {
            return this.in.read(b, off, len);
        } else {
            b[off++] = (byte) this.peekb;
            len--;
            this.peekb = -1;
            int n = this.in.read(b, off, len);
            return (n >= 0) ? n + 1 : 1;
        }
    }

    @Override
    public final void close() throws IOException {
        this.in.close();
    }
}
