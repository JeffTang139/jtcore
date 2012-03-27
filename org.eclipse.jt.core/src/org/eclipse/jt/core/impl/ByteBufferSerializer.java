/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferSerializer.java
 * Date 2009-3-24
 */
package org.eclipse.jt.core.impl;

import java.nio.ByteBuffer;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;


/**
 * ���ByteBuffer�����л�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ByteBufferSerializer implements DataSerializer {
    private final ByteBufferSender sender;
    private final ByteBufferManager bufManager;
    final PacketCode packetCode;
    final int requestId;

    private ByteBufferWrapper wrapper;
    private ByteBuffer working;

    ByteBufferSerializer(ByteBufferSender sender, ByteBufferManager bufManager,
            PacketCode packetCode, int requestId) {
        if (sender == null) {
            throw new NullArgumentException("sender");
        }
        if (bufManager == null) {
            throw new NullArgumentException("bufManager");
        }
        if (packetCode == null) {
            throw new NullArgumentException("packetCode");
        }
        this.sender = sender;
        this.bufManager = bufManager;
        this.packetCode = packetCode;
        this.requestId = requestId;
    }

    // submit�в������հ���
    private void submitCurrentBuffer() {
        Assertion.ASSERT(this.working != null);
        this.working.flip();
        int size = this.working.limit() - NetConnection.DATAGRAM_HEADER_LENGTH;
        Assertion.ASSERT(size > 0, "�㳤�����ݰ�");
        // ���ð������ݵĳ��ȡ�
        this.working.putInt(NetConnection.DATAGRAM_HEADER_LENGTH_POS, size);
        this.sender.toSend(this.wrapper);
        this.working = null;
        this.wrapper = null;
    }

    private void nextByteBuffer() {
        this.wrapper = this.bufManager.getBuffer();
        this.working = this.wrapper.buffer;
        this.working.order(Endianness.LOCAL_ENDIAN.byteOrder());
        NetConnection.putDataPacketHeaderData(this.working, this.packetCode, 0,
                this.requestId, 0/* �����ݰ� */);
    }

    private void checkWritable(int size) {
        if (this.working == null) {
            this.nextByteBuffer();
            Assertion.ASSERT(this.working.remaining() >= size);
            return;
        }
        if (this.working.remaining() < size) {
            this.submitCurrentBuffer();
            this.nextByteBuffer();
            Assertion.ASSERT(this.working.remaining() >= size);
        }
    }

    private void checkFull() {
        if (!this.working.hasRemaining()) {
            this.submitCurrentBuffer();
        }
    }

    /**
     * ֻ���������档
     */
    final void dispose() {
        if (this.wrapper != null) {
            this.wrapper.free();
            this.working = null;
            this.wrapper = null;
        }
    }

    public final void close() {
        if (this.working != null
                && this.working.position() > NetConnection.DATAGRAM_HEADER_LENGTH) {
            // ����������δ���͵����ݡ�
            this.submitCurrentBuffer();
        } // else ������ǡ��û��δ���͵�ʣ�����ݡ�

        // ����һ���հ�����ʾ�������ݵĽ�����
        if (this.working == null) {
            this.nextByteBuffer();
        } // else working�պû���һ���հ���
        this.working.flip();
        this.sender.toSend(this.wrapper);
        this.working = null;
        this.wrapper = null;
    }

    public void flush() {
    }

    private void write0(byte[] b, int off, int len) {
        if (this.working == null) {
            this.nextByteBuffer();
            Assertion.ASSERT(this.working.hasRemaining());
        }
        int write;
        while (len > 0) {
            write = this.working.remaining();
            if (write > len) {
                write = len;
            }
            this.working.put(b, off, write);
            len -= write;
            off += write;
            if (!this.working.hasRemaining()) {
                this.submitCurrentBuffer();
                if (len > 0) {
                    this.nextByteBuffer();
                    Assertion.ASSERT(this.working.hasRemaining());
                }
            }
        }
    }

    public void write(byte[] b) {
        if (b == null || b.length == 0) {
            return;
        }
        this.write0(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullArgumentException("b");
        } else if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        this.write0(b, off, len);
    }

    public void writeBoolean(boolean v) {
        this.checkWritable(1);
        this.working.put((byte) (v ? 1 : 0));
        this.checkFull();
    }

    public void writeByte(byte v) {
        this.checkWritable(1);
        this.working.put(v);
        this.checkFull();
    }

    public void writeChar(char v) {
        this.checkWritable(2);
        this.working.putChar(v);
        this.checkFull();
    }

    public void writeDouble(double v) {
        this.checkWritable(8);
        this.working.putDouble(v);
        this.checkFull();
    }

    public void writeFloat(float v) {
        this.checkWritable(4);
        this.working.putFloat(v);
        this.checkFull();
    }

    public void writeInt(int v) {
        this.checkWritable(4);
        this.working.putInt(v);
        this.checkFull();
    }

    public void writeLong(long v) {
        this.checkWritable(8);
        this.working.putLong(v);
        this.checkFull();
    }

    public void writeShort(short v) {
        this.checkWritable(2);
        this.working.putShort(v);
        this.checkFull();
    }
}
