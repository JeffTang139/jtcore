/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSslSelectChannelEndPoint.java
 * Date 2009-11-3
 */
package org.eclipse.jt.core.jetty.io.nio;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.http.ssl.SslSelectChannelEndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jt.core.jetty.server.DnaSSLHttpConnection;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSSLSelectChannelEndPoint extends SslSelectChannelEndPoint {
    private final DnaSSLHttpConnection dnaConnection;
    private final SSLEngine engine;
    volatile boolean canBeClosed = false;

    public DnaSSLSelectChannelEndPoint(Buffers buffers, SocketChannel channel,
            SelectSet selectSet, SelectionKey key, SSLEngine engine)
            throws IOException {
        super(buffers, channel, selectSet, key, engine);
        this.engine = engine;
        Connection c = this.getConnection();
        if (c instanceof DnaSSLHttpConnection) {
            this.dnaConnection = (DnaSSLHttpConnection) c;
        } else {
            this.dnaConnection = null;
        }
    }

    public SSLEngine getEngine() {
        return this.engine;
    }

    @Override
    public void close() throws IOException {
        if (!this.isDnaRsiConnection() || this.canBeClosed) {
            super.close();
        }
    }

    public boolean isDnaRsiConnection() {
        return (this.dnaConnection != null && this.dnaConnection
                .isDnaRsiConnection());
    }

    public SocketChannel tryDetachSocketChannel() {
        // get the SocketChannel.
        final SocketChannel socketChannel;
        ByteChannel channel = super.getChannel();
        if (channel instanceof SocketChannel) {
            socketChannel = (SocketChannel) channel;
        } else {
            socketChannel = null;
        }

        if (socketChannel != null && this.isDnaRsiConnection()) {
            super._socket = null;
            UnsafeEndPointHelper.resetFields(this);
            UnsafeEndPointHelper.closedSuperEndPoint(this, socketChannel);
        }

        return socketChannel;
    }

    @Override
    public ByteChannel getChannel() {
        if (this.isDnaRsiConnection()) {
            return UnsafeEndPointHelper.closedByteChannel;
        } else {
            return super.getChannel();
        }
    }
}
