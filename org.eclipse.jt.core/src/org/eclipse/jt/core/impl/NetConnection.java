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
 * ���Ӷ��󣬱�ʾ��ĳ��Զ�̻��������ӡ�
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

    // FIXME δ��֧��SSL
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
            throw new IllegalStateException("�Ѿ��Ǿ���״̬������������");
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
     * ����ͨ�����жϡ�֪ͨ��������ʧ�ܡ�ע�����Ӷ���
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
        ConsoleLog.info("������[%s:%s]�����ӶϿ��ˡ�", this.remoteNodeInfo.getAddress()
                .getHostAddress(), this.remoteNodeInfo.getPort());
        // help test
        // if (exception != null) {
        // exception.printStackTrace();
        // }
    }

    // ------------------------------------------------------------------------
    // Զ������
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
    // ��������
    // ------------------------------------------------------------------------

    private StructAdapterSet structAdapterSet;

    /**
     * �ڼ�Ⱥ�����ʼ��֮ǰ����ʹ�ô˷�����
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
            // ĩλΪ0
            this.nextId = (((int) System.nanoTime()) & 0xFFFFFFFE);
        }

        final synchronized int next() {
            return this.nextId += 2;
        }
    }

    // ------------------------------------------------------------------------
    // ��Ϣת��
    // ------------------------------------------------------------------------

    /**
     * ֪ͨ�����ӣ�����ͨ���Ѵ��ڿ�д״̬���ܹ���ͨ��д�����ˡ�
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
                this.netManager.pendWriteable(this); // ��ע���ֹ������
                this.needPendWritable = false;
            }
        }
    }

    final void unregisterW() {
        this.netManager.unregisterWriteable(this);
        this.needPendWritable = true;
    }

    /**
     * ֪ͨ�����ӣ�����ͨ���Ѵ��ڿɶ�״̬���ܹ���ͨ����ȡ�����ˡ�
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
    // ���ݴ���
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
     * ֻ�������ܹ�ֱ��ʹ������ֶΡ�
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
        buf.putInt(0); // xxxxx ��δʹ�á�
        buf.putInt(requestId);
        buf.putInt(dataLength); // Ŀǰ���е�ʹ�����ﶼ��0, ��ֵ��Ҫװ������֮���������á�
    }

    /**
     * ���ݰ��Ľṹ
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
        // XXX �ɿ���ʹ�������������б������Ϳ��Բ���ͬ����
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
                Assertion.ASSERT(remaining != 0, "�������������û������");
                size = this.channel.write(src);

                RITestHelper.alldatasent += size;

                if (size == 0)
                // δ�������ݣ�ͨ�����ڲ���д״̬��Ӧ�ѱ�ͨ����д������ע����
                {
                    return WorkStatus.NO_DATA;
                } else if (remaining == size)
                // ��������ȫ����
                {
                    this.toSend.removeFirst().free();
                } else {
                    /*
                     * �������˵�����������˻������еĲ������ݣ�������û�г��ֹ����������
                     * ��Ϊ�˱�֤�����ܱ������������ﲻ���κδ���ʣ�µ����ݻᱻ�������͡�
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
            // ͨ�����ر���
            throw new IllegalStateException("����ͨ�����ر���");
        } else if (size == 0) {
            // û�ж����κ����ݣ�ͨ�����ɶ���
            return WorkStatus.NO_DATA;
        } else {
            while (size > 0) {
                // ��ȷ�����ݰ�ͷ��Ϣ����
                if (!this.header.isFull()) {
                    size -= this.header.put(buf);
                }
                // ����ͷ��Ϣ���������������
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
    // ���ݰ��ַ�
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
                "���棡 - ���ݰ�����������ԭ����������ݰ����ջ�����ִ���������룺" + dataPacket.code);
        code.handle(this, dataPacket);
    }

    final void handOutRequest(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        RemoteRequestHandler handler = this.comins.get(dataPacket.requestId);
        if (this.failureCominIDs.contains(dataPacket.requestId)) {
            if (dataPacket.capacity() == 0) // ������
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
        // Զ�����󷵻ص����ݰ���������û��ƥ�������
        // ���ݴ��͹��ϡ����ݲ���������ǿ��ȡ���������Ѿ�������
    }

    /*
     * �쳣ֻ������˻ط���
     */
    final void handOutException(DataPacket dataPacket) {
        Assertion.ASSERT(dataPacket.connection == this);
        RemoteRequestStubBase request = this.gouts.get(dataPacket.requestId);
        if (request != null) {
            request.receiveException(dataPacket);
        }

        // if request == null then
        // Զ�����󷵻ص����ݰ���������û��ƥ�������
        // ���ݴ��͹��ϡ����ݲ���������ǿ��ȡ���������Ѿ�������
    }

    // ------------------------------------------------------------------------
    // �ڲ��洢
    // ------------------------------------------------------------------------

    // ʧ�ܵĽ��յ�����������ı�š�
    private final SortedIntSet failureCominIDs = new SortedIntSet();

    // ���ⷢ�͵�����
    private final IntKeyTable<RemoteRequestStubBase> gouts = new IntKeyTable<RemoteRequestStubBase>();

    // ���յ�����������
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
     * ���Զ������Ĵ����<br/>
     * ���Ӷ���ά��һ�����ڴ����ӵ�Զ��������У�δ��ɵ�Զ�������ڴ˶����С�
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
            RuntimeException e = new IllegalStateException("��������Ͽ���");
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
