/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Connection.java
 * Date 2009-2-16
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;
import org.eclipse.jt.core.impl.DataWorker.WorkStatus;
import org.eclipse.jt.core.type.GUID;


/**
 * 连接对象，表示到某个远程机器的连接。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
class NetConnection extends NetManagerBased implements ByteBufferSender {
    final int id;
    final int remoteId;

    final NetNodeInfo remoteNodeInfo;

    final Endianness remoteEndian;
    final GUID remoteServerId;

    volatile SocketChannel channel;

    private static final IntIdGenerator idgen = new IntIdGenerator(0);

    NetConnection(NetManager netManager, NetNodeInfo remoteNodeInfo,
            Endianness remoteByteOrder, GUID remoteServerId,
            SocketChannel socketChannel) throws IOException {
        super(netManager);
        if (remoteNodeInfo == null) {
            throw new NullArgumentException("remoteNodeInfo");
        }
        if (remoteByteOrder == null) {
            throw new NullArgumentException("remoteByteOrder");
        }
        if (remoteServerId == null) {
            throw new NullArgumentException("remoteServerId");
        }
        if (socketChannel == null) {
            throw new NullArgumentException("socketChannel");
        }
        this.id = idgen.next();
        this.remoteNodeInfo = remoteNodeInfo;
        this.remoteEndian = remoteByteOrder;
        this.remoteServerId = remoteServerId;
        this.channel = socketChannel;
        this.state = State.CONNECTED;
        this.remoteId = this.initRemoteId();
        this.netManager.putNetConnection(this);
    }

    // FIXME 未能支持SSL
    final int initRemoteId() throws IOException {
        byte[] id = new byte[4];
        Endianness.LOCAL_ENDIAN.putInt(id, 0, this.id);
        ByteBuffer buf = ByteBuffer.wrap(id);
        do {
            this.channel.write(buf);
        } while (buf.hasRemaining());
        buf.clear();
        do {
            if (this.channel.read(buf) < 0) {
                try {
                    this.channel.close();
                } catch (Throwable ignore) {
                }
                throw new ClosedChannelException();
            }
        } while (buf.hasRemaining());
        return this.remoteEndian.getInt(id, 0);
    }

    private Handshakor handshakor;

    Handshakor internalNewHandshakor() {
        return new Handshakor(this.channel);
    }

    final synchronized Handshakor getHandshakor() {
        if (this.state == State.READY) {
            throw new IllegalStateException("已经是就绪状态，不能再握手");
        }
        if (this.handshakor == null) {
            this.handshakor = this.internalNewHandshakor();
        }
        return this.handshakor;
    }

    final synchronized void releaseHandshakor() {
        this.handshakor = null;
    }

    final InetAddress getRemoteAddress() {
        SocketChannel sc = this.channel;
        if (sc == null) {
            throw new IllegalStateException();
        }
        return sc.socket().getInetAddress();
    }

    void dispose() {
        this.state = State.DISPOSED;
        this.netManager.removeNetConnection(this);

        this.unregisterR();
        this.unregisterW();

        try {
            this.remoteNodeInfo.unbindConnection(this);
        } catch (Throwable ignore) {
        }

        if (this.channel != null) {
            try {
                this.channel.close();
            } catch (Exception ignore) {
            }
            this.channel = null;
        }

        if (this.structAdapterSet != null) {
            this.structAdapterSet.clear();
        }

        this.comins.clear();
        this.gouts.clear();
        this.failureCominIDs.clear();
        this.header.clear();
        this.packet = null;
    }

    /**
     * 网络通道被中断。通知所有任务失败。注销连接对象。
     */
    final void broken(final Throwable exception) {
        if (this.state == State.BROKEN || this.state == State.DISPOSED) {
            return;
        }
        this.state = State.BROKEN;
        this.gouts.visitAll(new ValueVisitor<RemoteRequestStubBase>() {
            public void visit(int key, RemoteRequestStubBase value) {
                try {
                    value.fail(exception);
                } catch (Throwable ignore) {
                }
            }
        });
        this.comins.visitAll(new ValueVisitor<RemoteRequestHandler>() {
            public void visit(int key, RemoteRequestHandler value) {
                try {
                    value.cancel();
                } catch (Throwable ignore) {
                }
            }
        });
        this.dispose();
        this.netManager.decreaseConnectedSize(this);
        ConsoleLog.info("与主机[%s:%s]的连接断开了。", this.remoteNodeInfo.getAddress()
                .getHostAddress(), this.remoteNodeInfo.getPort());
        // help test
        // if (exception != null) {
        // exception.printStackTrace();
        // }
    }

    // ------------------------------------------------------------------------
    // 远程请求
    // ------------------------------------------------------------------------

    final <TRemoteRequestStub extends RemoteRequestStubBase> TRemoteRequestStub postRequest(
            RemoteRequest<TRemoteRequestStub> remoteRequest) {
        this.checkConnected();
        TRemoteRequestStub requestStub = remoteRequest.newStub(this);
        this.addRemoteRequestStub(requestStub);
        requestStub.send();
        return requestStub;
    }

    final ClusterNodeLockRequestStubImpl postLockRequest(
            AbstractClusterLockInfo clusterLockInfo, NewAcquirer<?, ?> localLock) {
        this.checkConnected();
        ClusterNodeLockRequestStubImpl requestStub = clusterLockInfo.newStub(
                this, localLock);
        this.addRemoteRequestStub(requestStub);
        requestStub.send();
        return requestStub;
    }

    // ------------------------------------------------------------------------
    // 辅助对象
    // ------------------------------------------------------------------------

    private StructAdapterSet structAdapterSet;

    /**
     * 在集群对象初始化之前不能使用此方法。
     */
    final StructAdapterSet getStructAdapterSet() {
        if (this.structAdapterSet == null) {
            Cluster c = this.remoteNodeInfo.owner;
            if (c != null) {
                this.structAdapterSet = c.getStructAdapterSet();
            } else {
                this.structAdapterSet = new StructAdapterSet(
                        this.netManager.application);
            }
        }
        return this.structAdapterSet;
    }

    final StructDefineProvider structDefineProvider = new StructDefineProvider() {
        public StructAdapter getStructDefine(StructSummary structSummary)
                throws StructDefineNotFoundException {
            if (structSummary == null) {
                throw new NullArgumentException("structSummary");
            }

            try {
                RemoteQueryStubImpl stub = NetConnection.this
                        .postRequest(new RemoteStructQuery(structSummary));
                stub.syncWork();
                StructAdapter sa = (StructAdapter) stub.getResult();
                if (sa == null) {
                    throw new NullPointerException();
                }
                return sa;
            } catch (Throwable e) {
                throw new StructDefineNotFoundException(
                        structSummary.defineName, e);
            }
        }
    };

    private final IDGen stubIdGen = new IDGen();

    final int nextStubId() {
        return this.stubIdGen.next();
    }

    private static final class IDGen {
        private int nextId;

        IDGen() {
            // 末位为0
            this.nextId = (((int) System.nanoTime()) & 0xFFFFFFFE);
        }

        final synchronized int next() {
            return this.nextId += 2;
        }
    }

    // ------------------------------------------------------------------------
    // 消息转发
    // ------------------------------------------------------------------------

    /**
     * 通知本连接，网络通道已处于可写状态，能够向通道写数据了。
     */
    final void wakeupW() {
        this.netManager.notifyWriteable(this);
    }

    private boolean needPendWritable = false;

    final void registerW() {
        synchronized (this.toSend) {
            if (this.toSend.isEmpty()) {
                this.needPendWritable = true;
            } else {
                this.netManager.pendWriteable(this); // 需注意防止死锁。
                this.needPendWritable = false;
            }
        }
    }

    final void unregisterW() {
        this.netManager.unregisterWriteable(this);
        this.needPendWritable = true;
    }

    /**
     * 通知本连接，网络通道已处于可读状态，能够从通道读取数据了。
     */
    final void wakeupR() {
        this.netManager.notifyReadable(this);
    }

    final void registerR() {
        this.netManager.pendReadable(this);
    }

    final void unregisterR() {
        this.netManager.unregisterReadable(this);
    }

    // ------------------------------------------------------------------------
    // 数据传输
    // ------------------------------------------------------------------------

    final void nofityForceCancelHandler(int requestId) {
        if (this.state == State.READY) {
            ByteBufferWrapper wrapper = this.netManager.getBuffer();
            ByteBuffer buf = wrapper.buffer;
            putDataPacketHeaderData(buf, PacketCode.FORCE_CANCEL_HANDLER, 0,
                    requestId, 0);
            buf.flip();
            this.toSend(wrapper);
        }
    }

    final void nofityForceCancelStub(int requestId, Throwable exception) {
        if (this.state == State.READY) {
            try {
                RIUtil.send(requestId, this, new RemoteForceCancelStub(
                        exception));
            } catch (Throwable ignore) {
                try {
                    RIUtil.send(requestId, this,
                            new RemoteForceCancelStub(null));
                } catch (Throwable ignr) {
                }
            }
        }
    }

    /**
     * 只有子类能够直接使用这个字段。
     */
    final LinkedList<ByteBufferWrapper> toSend = new LinkedList<ByteBufferWrapper>();

    public final void toSend(ByteBufferWrapper src) {
        ByteBuffer buf = src.buffer;
        if (buf.hasRemaining()) {
            buf.mark();
            synchronized (this.toSend) {
                this.toSend.add(src);
                if (this.needPendWritable) {
                    this.netManager.pendWriteable(this);
                    this.needPendWritable = false;
                }
            }
        }
    }

    static final int DATAGRAM_HEADER_LENGTH = 13;
    static final int DATAGRAM_HEADER_CODE_POS = 0;
    static final int DATAGRAM_HEADER_INFO_POS = 1;
    static final int DATAGRAM_HEADER_REQUESTID_POS = 5;
    static final int DATAGRAM_HEADER_LENGTH_POS = 9;

    static void putDataPacketHeaderData(ByteBuffer buf, PacketCode packetCode,
            int info, int requestId, int dataLength) {
        buf.put(packetCode.code);
        buf.putInt(0); // xxxxx 尚未使用。
        buf.putInt(requestId);
        buf.putInt(dataLength); // 目前已有的使用这里都是0, 其值需要装填数据之后重新设置。
    }

    /**
     * 数据包的结构
     * 
     * <pre>
     * +----+----+----+----+----+----+----+----+----+----+----+----+----+---(data_len)---+
     * |code|       xxxxx       |    request_id     |     data_len      |    data ...    |
     * +----+----+----+----+----+----+----+----+----+----+----+----+----+----------------+
     * </pre>
     * 
     * @return
     * @throws Exception
     */
    WorkStatus send() throws Exception {
        if (this.state == State.BROKEN || this.state == State.DISPOSED) {
            throw new IllegalStateException(this.state.toString());
        }
        // XXX 可考虑使用两个待发送列表，那样就可以不用同步了
        synchronized (this.toSend) {
            ByteBuffer src;
            int size, remaining;
            if (this.toSend.isEmpty()) {
                return WorkStatus.NO_DATA;
            }
            while (this.toSend.size() > 0) {
                size = 0;
                src = this.toSend.getFirst().buffer;
                remaining = src.remaining();
                Assertion.ASSERT(remaining != 0, "缓冲区中意外的没有数据");
                size = this.channel.write(src);

                RITestHelper.alldatasent += size;

                if (size == 0)
                // 未发送数据，通道处于不可写状态，应把本通道从写队列中注销。
                {
                    return WorkStatus.NO_DATA;
                } else if (remaining == size)
                // 数据已完全发送
                {
                    this.toSend.removeFirst().free();
                } else {
                    /*
                     * 这种情况说明，仅发送了缓冲区中的部分数据，测试中没有出现过这种情况。
                     * 但为了保证数据能被完整发送这里不做任何处理，剩下的数据会被继续发送。
                     */
                    Assertion.ASSERT(size > 0 && size < remaining);
                }
            }

            return WorkStatus.OK;
        }
    }

    /**
     * Only for this class and sub-classes.
     */
    final DataPacket header = new DataPacket(this, (byte) -1, -1,
            DATAGRAM_HEADER_LENGTH);
    /**
     * Only for this class and sub-classes.
     */
    DataPacket packet;

    /**
     * Only for this class and sub-classes.
     */
    final void received(DataPacket dataPacket) {
        this.netManager.dispatch(dataPacket);
    }

    WorkStatus receive(ByteBuffer buf) throws Exception {
        if (this.state == State.BROKEN || this.state == State.DISPOSED) {
            throw new IllegalStateException(this.state.toString());
        }

        buf.clear();
        int size = 0;
        size = this.channel.read(buf);

        RITestHelper.alldataread += size;

        buf.flip();
        if (size < 0) {
            // 通道被关闭了
            throw new IllegalStateException("网络通道被关闭了");
        } else if (size == 0) {
            // 没有读到任何数据，通道不可读。
            return WorkStatus.NO_DATA;
        } else {
            while (size > 0) {
                // 先确保数据包头信息填满
                if (!this.header.isFull()) {
                    size -= this.header.put(buf);
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
                    size -= this.packet.put(buf);
                    if (this.packet.isFull()) {
                        this.received(this.packet);
                        this.packet = null;
                        this.header.clear();
                    }
                }
            }

            return WorkStatus.OK;
        }
    }

    // ------------------------------------------------------------------------
    // 数据包分发
    // ------------------------------------------------------------------------

    final void forceCancelHandler(int requestId) {
        this.putFailureCominIds(requestId);
        RemoteRequestHandler handler = this.comins.get(requestId);
        if (handler != null) {
            try {
                handler.cancel();
            } catch (Throwable ignore) {
            }
            this.comins.remove(handler.id);
        }
    }

    final void forceCancelStub(DataPacket dataPacket) {
        // TODO implement.

        RemoteRequestStubBase request = this.gouts.remove(dataPacket.requestId);
        if (request != null) {
            request.fail(new RuntimeException("REMOTE EXCEPTION"));
        }
    }

    final void handOut(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        PacketCode code = PacketCode.valueOf(dataPacket.code);
        Assertion.ASSERT(code != null,
                "警告！ - 数据包请求代码错误，原因可能是数据包接收或拆解出现错误。请求代码：" + dataPacket.code);
        code.handle(this, dataPacket);
    }

    final void handOutRequest(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        RemoteRequestHandler handler = this.comins.get(dataPacket.requestId);
        if (this.failureCominIDs.contains(dataPacket.requestId)) {
            if (dataPacket.capacity() == 0) // 结束包
            {
                this.failureCominIDs.remove(dataPacket.requestId);
            }
            if (handler != null) {
                handler.receiveRequestData(dataPacket);
            }
        } else {
            if (handler == null) {
                handler = new RemoteRequestHandler(this, dataPacket.requestId);
                this.comins.put(handler.id, handler);
                this.netManager.application.overlappedManager
                        .startWork(handler);
            }
            handler.receiveRequestData(dataPacket);
        }
    }

    final void handOutReturn(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        RemoteRequestStubBase request = this.gouts.get(dataPacket.requestId);
        if (request != null) {
            request.receiveReturn(dataPacket);
        }

        // if request == null then
        // 远程请求返回的数据包的请求编号没有匹配的任务。
        // 数据传送故障、数据拆解错误、任务被强行取消、任务已经结束？
    }

    /*
     * 异常只向请求端回发。
     */
    final void handOutException(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        RemoteRequestStubBase request = this.gouts.get(dataPacket.requestId);
        if (request != null) {
            request.receiveException(dataPacket);
        }

        // if request == null then
        // 远程请求返回的数据包的请求编号没有匹配的任务。
        // 数据传送故障、数据拆解错误、任务被强行取消、任务已经结束？
    }

    // ------------------------------------------------------------------------
    // 内部存储
    // ------------------------------------------------------------------------

    // 失败的接收到的外来请求的编号。
    private final SortedIntSet failureCominIDs = new SortedIntSet();

    // 向外发送的请求
    private final IntKeyTable<RemoteRequestStubBase> gouts = new IntKeyTable<RemoteRequestStubBase>();

    // 接收到的外来请求
    private final IntKeyTable<RemoteRequestHandler> comins = new IntKeyTable<RemoteRequestHandler>();

    final void releaseRemoteRequestHandler(RemoteRequestHandler handler) {
        if (handler != null) {
            this.comins.remove(handler.id);
        }
    }

    final void releaseRemoteRequestStub(RemoteRequestStubBase stub) {
        if (stub != null) {
            this.gouts.remove(stub.id);
        }
    }

    final void putFailureCominIds(int requestId) {
        this.failureCominIDs.put(requestId);
    }

    final void removeFailureCominIds(int requestId) {
        this.failureCominIDs.remove(requestId);
    }

    /**
     * 添加远程请求的存根。<br/>
     * 连接对象维护一个基于此连接的远程请求队列，未完成的远程请求都在此队列中。
     * 
     * @param remoteStub
     */
    final void addRemoteRequestStub(RemoteRequestStubBase remoteStub) {
        this.gouts.put(remoteStub.id, remoteStub);
    }

    // --------------------------------------------------------------------
    // ------------------------ State -------------------------------------
    private volatile State state = State.INITED;

    final State getState() {
        return this.state;
    }

    final void checkConnected() {
        if (this.state != State.READY && this.state != State.CONNECTED) {
            throw new IllegalStateException(this.state.toString());
        }
        SocketChannel sc = this.channel;
        if (sc == null || !sc.isConnected()) {
            RuntimeException e = new IllegalStateException("连接意外断开了");
            this.broken(e);
            throw e;
        }
    }

    final boolean isConnected() {
        if (this.state != State.CONNECTED && this.state != State.READY) {
            return false;
        }
        SocketChannel sc = this.channel;
        if (sc != null && sc.isConnected()) {
            return true;
        }
        return false;
    }

    final boolean isReady() {
        if (this.state != State.READY) {
            return false;
        }
        SocketChannel sc = this.channel;
        if (sc != null && sc.isConnected()) {
            return true;
        }
        return false;
    }

    final State state() {
        return this.state;
    }

    final void ready() {
        this.state = State.READY;
        this.netManager.increaseConnectedSize(this);
        this.releaseHandshakor();
    }

    static enum State {
        INITED, CONNECTED, READY, BROKEN, DISPOSED
    }

    // ------------------------ State -------------------------------------
    // --------------------------------------------------------------------

    // =========================================================================
    // TODO Cluster Related
    // -------------------------------------------------------------------------
    // private ResourceGroupAcquirerHolder<RemoteResourceGroupHandle>
    // remoteResGroupHandles;
    //
    // final ResourceGroupAcquirerHolder<RemoteResourceGroupHandle>
    // getRemoteResGroupHandles() {
    // if (this.remoteResGroupHandles == null) {
    // this.remoteResGroupHandles = new
    // ResourceGroupAcquirerHolder<RemoteResourceGroupHandle>(
    // null, null);
    // }
    // return this.remoteResGroupHandles;
    // }
}
