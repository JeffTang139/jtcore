/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NetConnectionWithSSL.java
 * Date Nov 10, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.Status;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.DataWorker.WorkStatus;
import org.eclipse.jt.core.type.GUID;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class NetConnectionWithSSL extends NetConnection {
    final SSLEngine sslEngine;
    final int packetBufferSize;

    // TODO 管理这些缓冲区
    private ByteBuffer sslOut;
    private ByteBuffer sslIn;
    private ByteBuffer msgIn;

    NetConnectionWithSSL(NetManager netManager,
            NetNodeInfo remoteNodeInfo, Endianness remoteByteOrder,
            GUID remoteServerId, int remoteNodeInfoId,
            SocketChannel socketChannel, SSLEngine sslEngine)
            throws IOException {
        this(netManager, remoteNodeInfo, remoteByteOrder,
                remoteServerId, socketChannel, sslEngine);
    }

    NetConnectionWithSSL(NetManager netManager,
            NetNodeInfo remoteNodeInfo, Endianness remoteByteOrder,
            GUID remoteServerId, SocketChannel socketChannel,
            SSLEngine sslEngine) throws IOException {
        super(netManager, remoteNodeInfo, remoteByteOrder,
                remoteServerId, socketChannel);
        if (sslEngine == null) {
            throw new NullArgumentException("sslEngine");
        }
        this.sslEngine = sslEngine;
        this.packetBufferSize = sslEngine.getSession().getPacketBufferSize();
    }

    @Override
    void dispose() {
        this.sslEngine.closeOutbound();
        this.sslOut = null;
        this.sslIn = null;
        this.msgIn = null;

        super.dispose();
    }

    @Override
    Handshakor internalNewHandshakor() {
        return new HandshakorWithSSL(this.channel, this.sslEngine);
    }

    @Override
    WorkStatus send() throws Exception {
        State state = this.getState();
        if (state == State.BROKEN || state == State.DISPOSED) {
            throw new IllegalStateException(state.toString());
        }
        if (this.sslOut != null) {
            while (this.sslOut.hasRemaining()) {
                if (this.channel.write(this.sslOut) == 0) {
                    return WorkStatus.NO_DATA;
                }
            }
        }
        synchronized (this.toSend) {
            if (this.toSend.isEmpty()) {
                return WorkStatus.NO_DATA;
            }
            if (this.sslOut == null) {
                this.sslOut = ByteBuffer.allocateDirect(this.packetBufferSize);
                this.sslOut.limit(0);
            }
            ByteBuffer src;
            int written;
            SSLEngineResult sslResult;
            while (this.toSend.size() > 0) {
                written = 0;
                src = this.toSend.getFirst().buffer;
                Assertion.ASSERT(src.hasRemaining(), "缓冲区中意外的没有数据");
                do {
                    this.sslOut.clear();
                    sslResult = this.sslEngine.wrap(src, this.sslOut);
                    this.sslOut.flip();
                    written += sslResult.bytesConsumed();
                    if (sslResult.getStatus() != Status.OK) {
                        throw new IllegalStateException(sslResult.getStatus()
                                .toString());
                    }
                    do {
                        if (this.channel.write(this.sslOut) == 0) {
                            if (!src.hasRemaining()) {
                                this.toSend.removeFirst().free();
                            }
                            return WorkStatus.NO_DATA;
                        }
                    } while (this.sslOut.hasRemaining());
                } while (src.hasRemaining());
                this.toSend.removeFirst().free();
                RITestHelper.alldatasent += written; // for Test
            }
            return WorkStatus.OK;
        }
    }

    @Override
    WorkStatus receive(ByteBuffer buf) throws Exception {
        buf = null; // useless
        State state = this.getState();
        if (state == State.BROKEN || state == State.DISPOSED) {
            throw new IllegalStateException(state.toString());
        }

        if (this.sslIn == null) {
            this.sslIn = ByteBuffer.allocateDirect(this.packetBufferSize);
            this.sslIn.limit(0);
        }
        if (this.msgIn == null) {
            this.msgIn = ByteBuffer.allocateDirect(this.packetBufferSize);
            this.msgIn.limit(0);
        }

        if (this.sslIn.hasRemaining()) {
            this.sslIn.compact();
        } else {
            this.sslIn.clear();
        }
        int sslRead = this.channel.read(this.sslIn);
        this.sslIn.flip();
        if (sslRead < 0) {
            // 通道被关闭了
            throw new IllegalStateException("网络通道被关闭了");
        } else if (sslRead == 0) {
            // 没有读到任何数据，通道不可读。
            return WorkStatus.NO_DATA;
        } else {
            int once;
            SSLEngineResult sslResult;
            do {
                this.msgIn.clear();
                sslResult = this.sslEngine.unwrap(this.sslIn, this.msgIn);
                this.msgIn.flip();
                Status sslStatus = sslResult.getStatus();
                if (sslStatus == Status.BUFFER_UNDERFLOW) {
                    return WorkStatus.OK; // continue to read.
                } else if (sslResult.getStatus() != Status.OK) {
                    throw new IllegalStateException(sslResult.getStatus()
                            .toString());
                }
                once = sslResult.bytesProduced();
                if (once == 0) {
                    break;
                }
                RITestHelper.alldataread += once; // for Test
                do {
                    // 先确保数据包头信息填满
                    if (!this.header.isFull()) {
                        this.header.put(this.msgIn);
                    }
                    // 若包头信息已满，处理包数据
                    if (this.header.isFull()) {
                        if (this.packet == null) {
                            this.packet = new DataPacket(this, this.header
                                    .get(DATAGRAM_HEADER_CODE_POS), this.header
                                    .getInt(this.remoteEndian,
                                            DATAGRAM_HEADER_REQUESTID_POS),
                                    this.header.getInt(this.remoteEndian,
                                            DATAGRAM_HEADER_LENGTH_POS));
                        }
                        this.packet.put(this.msgIn);
                        if (this.packet.isFull()) {
                            this.received(this.packet);
                            this.packet = null;
                            this.header.clear();
                        }
                    }
                } while (this.msgIn.hasRemaining());
            } while (this.sslIn.hasRemaining());

            return WorkStatus.OK;
        }
    }
}
