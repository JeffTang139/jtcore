/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSelectChannelEndPoint.java
 * Date 2009-10-19
 */
package org.eclipse.jt.core.jetty.io.nio;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jt.core.jetty.server.DnaHttpConnection;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSelectChannelEndPoint extends SelectChannelEndPoint {
    private final DnaHttpConnection dnaConnection;
    volatile boolean canBeClosed = false;

    public DnaSelectChannelEndPoint(SocketChannel channel, SelectSet selectSet,
            SelectionKey key) {
        super(channel, selectSet, key);
        Connection c = this.getConnection();
        if (c instanceof DnaHttpConnection) {
            this.dnaConnection = (DnaHttpConnection) c;
        } else {
            this.dnaConnection = null;
        }
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
