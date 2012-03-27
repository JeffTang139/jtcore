/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IOHelper.java
 * Date Nov 18, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jt.core.type.GUID;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class IOHelper {
    private IOHelper() {
    }

    static void writeInt(OutputStream out, int value, byte[] buf)
            throws IOException {
        if (buf == null || buf.length < 0) {
            buf = new byte[4];
        }
        Endianness.LOCAL_ENDIAN.putInt(buf, 0, value);
        out.write(buf, 0, 4);
    }

    static GUID readGUID(InputStream in, byte[] buf) throws IOException {
        if (buf == null || buf.length < 16) {
            buf = new byte[16];
        }
        int len = 16, off = 0;
        int read;
        do {
            read = in.read(buf, off, len);
            if (read < 0) {
                throw new IOException("the input stream was closed");
            }
            off += read;
            len -= read;
        } while (len > 0);
        return GUID.valueOf(buf);
    }

    static int readInt(InputStream in, Endianness endian, byte[] buf)
            throws IOException {
        if (buf == null || buf.length < 4) {
            buf = new byte[4];
        }
        int len = 4, off = 0;
        int read;
        do {
            read = in.read(buf, off, len);
            if (read < 0) {
                throw new IOException("the input stream was closed");
            }
            off += read;
            len -= read;
        } while (len > 0);
        return endian.getInt(buf, 0);
    }

    static byte[] readFully(InputStream in, byte[] buf, int len)
            throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException("len: " + len);
        }
        if (buf == null || buf.length < len) {
            buf = new byte[len];
        }
        int off = 0;
        int read;
        while (len > 0) {
            read = in.read(buf, off, len);
            off += read;
            len -= read;
        }
        return buf;
    }
}
