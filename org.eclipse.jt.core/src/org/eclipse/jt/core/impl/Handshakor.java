/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Handshakor.java
 * Date Nov 20, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
class Handshakor {
    final SocketChannel socketChannel;

    Handshakor(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    InputStream getInputStream() throws IOException {
        return this.socketChannel.socket().getInputStream();
    }

    OutputStream getOutputStream() throws IOException {
        return this.socketChannel.socket().getOutputStream();
    }
}

class HandshakorWithSSL extends Handshakor {
    private final SSLEngine sslEngine;
    private final InputStream in;
    private final OutputStream out;

    HandshakorWithSSL(SocketChannel socketChannel, SSLEngine sslEngine) {
        super(socketChannel);
        this.sslEngine = sslEngine;
        this.in = new SSLInputStream();
        this.out = new SSLOutputStream();
    }

    @Override
    InputStream getInputStream() throws IOException {
        return this.in;
    }

    @Override
    OutputStream getOutputStream() throws IOException {
        return this.out;
    }

    private class SSLInputStream extends InputStream {
        final ByteBuffer sslPacket = (ByteBuffer) ByteBuffer.allocate(
                HandshakorWithSSL.this.sslEngine.getSession()
                        .getPacketBufferSize()).flip();
        final ByteBuffer msg = (ByteBuffer) ByteBuffer.allocate(
                HandshakorWithSSL.this.sslEngine.getSession()
                        .getPacketBufferSize()).flip();

        @Override
        public int read() throws IOException {
            if (this.msg.hasRemaining()) {
                return (this.msg.get() & 0xFF);
            }
            do {
                this.msg.clear();
                if (this.sslPacket.hasRemaining()) {
                    SSLEngineResult result = HandshakorWithSSL.this.sslEngine
                            .unwrap(this.sslPacket, this.msg);
                    if (result.bytesProduced() > 0) {
                        this.msg.flip();
                        return (this.msg.get() & 0xFF);
                    }
                }
                if (this.sslPacket.hasRemaining()) {
                    this.sslPacket.compact();
                } else {
                    this.sslPacket.clear();
                }
                if (HandshakorWithSSL.this.socketChannel.read(this.sslPacket) < 0) {
                    this.msg.limit(0);
                    this.sslPacket.limit(0);
                    return -1; // reached the end.
                }
                this.sslPacket.flip();
            } while (true);
        }

        @Override
        public int read(final byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0)
                    || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            int read = 0;
            int readable = this.msg.remaining();
            if (readable >= len) {
                this.msg.get(b, off, len);
                return len;
            } else if (readable > 0) {
                this.msg.get(b, off, readable);
                read += readable;
                off += readable;
                len -= readable;
            }

            do {
                while (this.sslPacket.hasRemaining()) {
                    this.msg.clear();
                    SSLEngineResult result = HandshakorWithSSL.this.sslEngine
                            .unwrap(this.sslPacket, this.msg);
                    if (result.bytesProduced() > 0) {
                        this.msg.flip();
                        readable = this.msg.remaining();
                        if (readable >= len) {
                            this.msg.get(b, off, len);
                            return (read + len);
                        } else if (readable > 0) {
                            this.msg.get(b, off, readable);
                            read += readable;
                            off += readable;
                            len -= readable;
                        }
                    } else {
                        break;
                    }
                }
                if (this.sslPacket.hasRemaining()) {
                    this.sslPacket.compact();
                } else {
                    this.sslPacket.clear();
                }
                if (HandshakorWithSSL.this.socketChannel.read(this.sslPacket) < 0) {
                    this.msg.limit(0);
                    this.sslPacket.limit(0);
                    return read; // reached the end.
                }
                this.sslPacket.flip();
            } while (true);
        }

        @Override
        public int available() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private class SSLOutputStream extends OutputStream {
        final ByteBuffer sslPacket = ByteBuffer
                .allocate(HandshakorWithSSL.this.sslEngine.getSession()
                        .getPacketBufferSize());
        final ByteBuffer msg = ByteBuffer
                .allocate(HandshakorWithSSL.this.sslEngine.getSession()
                        .getPacketBufferSize());

        @Override
        public void flush() throws IOException {
            if (this.msg.position() > 0) {
                this.msg.flip();
                while (this.msg.hasRemaining()) {
                    this.sslPacket.clear();
                    HandshakorWithSSL.this.sslEngine.wrap(this.msg,
                            this.sslPacket);
                    this.sslPacket.flip();
                    do {
                        HandshakorWithSSL.this.socketChannel
                                .write(this.sslPacket);
                    } while (this.sslPacket.hasRemaining());
                }
                this.msg.clear();
            }
        }

        @Override
        public void write(int b) throws IOException {
            if (this.msg.hasRemaining()) {
                this.msg.put((byte) b);
            } else {
                this.flush();
                this.msg.put((byte) b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if ((off < 0) || (off > b.length) || (len < 0)
                    || ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

            do {
                int writable = this.msg.remaining();
                if (writable >= len) {
                    this.msg.put(b, off, len);
                    return;
                } else if (writable > 0) {
                    this.msg.put(b, off, writable);
                    off += writable;
                    len -= writable;
                }
                this.flush();
            } while (true);
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}