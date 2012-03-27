/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterNodeInfo.java
 * Date May 4, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.obja.StructField;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.NetConnection.State;
import org.eclipse.jt.core.jetty.ConstantsForDnaRsi;
import org.eclipse.jt.core.type.GUID;


/**
 * 集群节点信息。
 * 
 * @see Cluster
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
final class NetNodeInfo extends NetManagerBased {
    int index = Cluster.DEFAULT_MASTER_INDEX; // default is master.

    final int getNodeMask() {
        return (1 << this.index);
    }

    private volatile String host;
    private volatile InetAddress address;
    private volatile int port;
    @StructField(stateField = false)
    private transient Boolean isToSelf;

    /**
     * 节点是否是所在集群的主节点。
     */
    final boolean isMaster;

    /**
     * 到节点的连接。
     */
    @StructField(stateField = false)
    private transient NetConnection netConnection;
    @StructField(stateField = false)
    private volatile boolean secure;

    @StructField(stateField = false)
    transient Cluster owner;

    // final int id;

    // private static final IntIdGenerator idgen = new IntIdGenerator(0);

    private NetNodeInfo(NetManager netManager, String host, int port,
            boolean isMaster) {
        super(netManager);
        if (host == null || host.length() == 0) {
            throw new NullArgumentException("host");
        }
        if (port < 0 || port > 0xFFFF) {
            throw new IllegalArgumentException("端口号无效：" + port);
        }
        // this.id = idgen.next();
        this.host = host;
        this.port = port;
        this.isMaster = isMaster;
    }

    private NetNodeInfo(NetManager netManager, InetAddress address, int port,
            boolean isMaster) {
        super(netManager);
        if (address == null) {
            throw new NullArgumentException("address");
        }
        if (port < 0 || port > 0xFFFF) {
            throw new IllegalArgumentException("端口号无效：" + port);
        }
        // this.id = idgen.next();
        this.address = address;
        this.port = port;
        this.isMaster = isMaster;
    }

    static final NetNodeInfo createNNI(NetManager netManager, String host,
            int port) {
        return new NetNodeInfo(netManager, host, port, true);
    }

    static final NetNodeInfo createNNI(NetManager netManager,
            InetAddress address, int port) {
        return new NetNodeInfo(netManager, address, port, true);
    }

    static final NetNodeInfo createNNI(NetManager netManager, String host,
            int port, boolean isMaster) {
        return new NetNodeInfo(netManager, host, port, isMaster);
    }

    static final NetNodeInfo createNNI(NetManager netManager,
            InetAddress address, int port, boolean isMaster) {
        return new NetNodeInfo(netManager, address, port, isMaster);
    }

    public final InetAddress getAddress() {
        if (this.address != null) {
            return this.address;
        }
        if (this.host != null) {
            try {
                this.address = InetAddress.getByName(this.host);
                return this.address;
            } catch (UnknownHostException e) {
                throw Utils.tryThrowException(e);
            }
        }
        throw new IllegalStateException("节点信息无效");
    }

    public final String getHost() {
        if (this.host != null) {
            return this.host;
        }
        if (this.address != null) {
            this.host = this.address.getHostName();
            return this.host;
        }
        throw new IllegalStateException("节点信息无效");
    }

    public final int getPort() {
        return this.port;
    }

    public final boolean isSecure() {
        return this.secure;
    }

    final void setSecure(boolean secure) {
        this.secure = secure;
    }

    final boolean isToSelf() {
        if (this.isToSelf == null) {
            boolean toSelf = false;
            if (this.host != null) {
                toSelf = this.netManager.isToSelf(this.host, this.port);
            } else if (this.address != null) {
                toSelf = this.netManager.isToSelf(this.address, this.port);
            } else {
                throw new IllegalStateException("节点信息无效");
            }
            this.isToSelf = toSelf;
        }
        return this.isToSelf.booleanValue();
    }

    final boolean hasSameTarget(NetNodeInfo netNodeInfo) {
        if (this == netNodeInfo) {
            return true;
        }
        if (netNodeInfo == null) {
            return false;
        }
        if (this.port != netNodeInfo.port) {
            return false;
        }

        NetConnection nc = this.netConnection;
        GUID id1 = nc == null ? null : nc.remoteServerId;
        nc = netNodeInfo.netConnection;
        GUID id2 = nc == null ? null : nc.remoteServerId;
        if (id1 != null && id2 != null && id1.equals(id2)) {
            return true;
        }

        if (this.host != null && this.host.equals(netNodeInfo.host)) {
            return true;
        }
        if (this.address != null && this.address.equals(netNodeInfo.address)) {
            return true;
        }

        return false;
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    synchronized boolean isConnected() {
        NetConnection nc = this.netConnection;
        if (nc == null) {
            return false;
        }
        return nc.isConnected();
    }

    synchronized void checkConnected() {
        NetConnection nc = this.netConnection;
        if (nc == null) {
            throw new IllegalStateException("连接尚未建立");
        } else {
            nc.checkConnected();
        }
    }

    final NetNodeInfo reconnect() {
        NetNodeInfo cni = this.netManager.reconnect(this);
        if (cni != this) {
            this.netManager.remove(this);
        }
        return cni;
    }

    synchronized final NetConnection getConnection() {
        return this.netConnection;
    }

    // XXX 不合理
    final NetConnection ensureGetConnection() {
        NetNodeInfo nni = this;
        NetConnection nc = nni.getConnection();
        if (nc == null || !nc.isConnected()) {
            nni = nni.reconnect();
            nc = nni.getConnection();
            // check again
            if (nc == null || !nc.isConnected()) {
                nni = nni.reconnect();
                nc = nni.getConnection();
                if (nc == null || !nc.isConnected()) {
                    throw new UnsupportedOperationException("无法建立连接");
                }
            }
        }
        return nc;
    }

    synchronized final boolean tryBindConnection(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        if (this.netConnection == netConnection) {
            return true;
        }
        if (netConnection.state() != State.READY
                && netConnection.state() != State.CONNECTED) {
            throw new IllegalStateException("要绑定的连接的状态无效："
                    + netConnection.state());
        }
        if (netConnection.remoteNodeInfo != this) {
            throw new IllegalArgumentException("无效绑定");
        }
        if (this.netConnection != null
                && (this.netConnection.state() == State.CONNECTED || this.netConnection
                        .state() == State.READY)) {
            return false;
        }
        InetAddress remote = netConnection.getRemoteAddress();
        ConsoleLog.debugInfo("NodeInfoAddress(original:%s, new:%s)",
                this.address, remote);
        this.address = remote;
        this.netConnection = netConnection;
        return true;
    }

    synchronized final void unbindConnection(NetConnection netConnection) {
        if (this.netConnection == null) {
            return;
        }
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        if (this.netConnection != netConnection) {
            throw new IllegalArgumentException("连接对象不匹配");
        }
        this.netConnection = null;
    }

    /* ------------------------------------------------------------------- */

    final NetConnection openNewConnection() throws IOException {
        NetConnection nc = null;
        try {
            nc = this.openConnection();
        } catch (Throwable e) {
            try {
                nc = this.openConnectionWithSSL();
            } catch (Throwable ignore) {
                if (e instanceof ClosedChannelException) {
                    e = ignore;
                }
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                throw Utils.tryThrowException(e);
            }
        }
        return nc;
    }

    private SocketChannel openNewSocketChannel() throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(this
                .getAddress(), this.port));
        channel.socket().setTcpNoDelay(true);
        channel.configureBlocking(true);
        return channel;
    }

    private final NetConnection openConnection() throws IOException {
        SocketChannel channel = this.openNewSocketChannel();
        ByteBuffer rsiRequest = ConstantsForDnaRsi.getDnaCoreRsiHttpRequest();
        do {
            channel.write(rsiRequest);
        } while (rsiRequest.hasRemaining());

        final byte[] rsi_response_signal_data = NetManager
                .getRsiResponseSignalData();
        final int okLen = rsi_response_signal_data.length;
        byte[] okMsg = new byte[okLen];
        ByteBuffer httpResp = ByteBuffer.wrap(okMsg);
        int total = 0, read = 0;
        do {
            read = channel.read(httpResp);
            if (read < 0) {
                try {
                    channel.close();
                } catch (Throwable ignore) {
                }
                throw new ClosedChannelException();
            }
            total += read;
        } while (total < okLen);

        for (int i = 0; i < okLen; i++) {
            if (rsi_response_signal_data[i] != okMsg[i]) {
                try {
                    channel.close();
                } catch (Throwable ignore) {
                }
                throw new UnsupportedOperationException("RSI连接应答错误");
            }
        }

        /* --------------------------- */
        final byte[] msg = this.netManager.getClientInfo();
        ByteBuffer msgBuf = ByteBuffer.wrap(msg);
        do {
            channel.write(msgBuf);
        } while (msgBuf.hasRemaining());
        /* --------------------------- */
        msgBuf.clear();
        total = 0;
        do {
            read = channel.read(msgBuf);
            if (read < 0) {
                try {
                    channel.close();
                } catch (Throwable ignore) {
                }
                throw new ClosedChannelException();
            }
            total += read;
        } while (total < 17);
        Endianness remote = Endianness.parseEndianness(msg[0]);
        byte[] id = new byte[16];
        System.arraycopy(msg, 1, id, 0, 16);
        GUID remoteId = GUID.valueOf(id);
        /* --------------------------- */

        this.secure = false;
        return new NetConnection(this.netManager, this, remote, remoteId,
                channel);
    }

    private final NetConnection openConnectionWithSSL() throws IOException {
        SocketChannel channel = this.openNewSocketChannel();

        SSLEngine sslEngine = this.getSSLContext().createSSLEngine();
        sslEngine.setUseClientMode(true);
        final int pbSize = sslEngine.getSession().getPacketBufferSize();
        final ByteBuffer sslPacket = ByteBuffer.allocate(pbSize);
        final ByteBuffer temp = ByteBuffer.allocate(pbSize);

        ByteBuffer rsiRequest = ConstantsForDnaRsi.getDnaCoreRsiHttpRequest();

        SSLEngineResult sslResult = null;
        SSLEngineResult.HandshakeStatus hsStatus = null;
        while (rsiRequest.hasRemaining()) {
            sslPacket.clear();
            sslResult = sslEngine.wrap(rsiRequest, sslPacket);
            sslPacket.flip();
            do {
                channel.write(sslPacket);
            } while (sslPacket.hasRemaining());

            hsStatus = sslResult.getHandshakeStatus();
            handshake: while (true) {
                // System.out.println(hsStatus);
                switch (hsStatus) {
                case NOT_HANDSHAKING:
                    break handshake;
                case FINISHED:
                    break handshake;
                case NEED_WRAP:
                    break handshake;
                case NEED_UNWRAP:
                    sslPacket.clear();
                    if (channel.read(sslPacket) < 0) {
                        throw new ClosedChannelException();
                    }
                    sslPacket.flip();
                    temp.clear();
                    do {
                        sslResult = sslEngine.unwrap(sslPacket, temp);
                        hsStatus = sslResult.getHandshakeStatus();
                    } while (sslPacket.hasRemaining()
                            && hsStatus == HandshakeStatus.NEED_UNWRAP);
                    Assertion.ASSERT(!sslPacket.hasRemaining());
                    break;
                case NEED_TASK:
                    Runnable task;
                    while ((task = sslEngine.getDelegatedTask()) != null) {
                        task.run();
                    }
                    hsStatus = sslEngine.getHandshakeStatus();
                    break;
                default:
                    throw new IllegalStateException(
                            "unhandled TLS/SSL handshake status: "
                                    + sslResult.getHandshakeStatus());
                }
            }
        }

        final byte[] rsi_response_signal_data = NetManager
                .getRsiResponseSignalData();
        final int okLen = rsi_response_signal_data.length;
        int len = 0;
        temp.clear();
        do {
            sslPacket.clear();
            if (channel.read(sslPacket) < 0) {
                try {
                    channel.close();
                } catch (Throwable ignore) {
                }
                throw new ClosedChannelException();
            }
            sslPacket.flip();
            sslResult = sslEngine.unwrap(sslPacket, temp);
            len += sslResult.bytesProduced();
        } while (len < okLen);
        temp.flip();
        byte[] okMsg = new byte[okLen];
        temp.get(okMsg);
        for (int i = 0; i < okLen; i++) {
            if (rsi_response_signal_data[i] != okMsg[i]) {
                throw new UnsupportedOperationException("RSI连接应答错误");
            }
        }

        /* --------------------------- */
        final byte[] msg = this.netManager.getClientInfo();
        ByteBuffer msgBuf = ByteBuffer.wrap(msg);
        sslPacket.clear();
        sslEngine.wrap(msgBuf, sslPacket);
        sslPacket.flip();
        do {
            channel.write(sslPacket);
        } while (sslPacket.hasRemaining());
        /* --------------------------- */
        temp.clear();
        len = 0;
        do {
            sslPacket.clear();
            if (channel.read(sslPacket) < 0) {
                try {
                    channel.close();
                } catch (Throwable ignore) {
                }
                throw new ClosedChannelException();
            }
            sslPacket.flip();
            sslResult = sslEngine.unwrap(sslPacket, temp);
            len += sslResult.bytesProduced();
        } while (len < 17);
        temp.flip();
        temp.get(msg, 0, 17);
        Endianness remote = Endianness.parseEndianness(msg[0]);
        byte[] id = new byte[16];
        System.arraycopy(msg, 1, id, 0, 16);
        GUID remoteId = GUID.valueOf(id);
        /* --------------------------- */

        this.secure = true;
        return new NetConnectionWithSSL(this.netManager, this, remote,
                remoteId, channel, sslEngine);
    }

    private volatile SSLContext sslContext;

    private final SSLContext getSSLContext() {
        if (this.sslContext == null) {
            this.sslContext = instanceLooseSSLContext();
        }
        return this.sslContext;
    }

    private static SSLContext instanceLooseSSLContext() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            // TODO real trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /* ------------------------------------------------------------------- */
}
