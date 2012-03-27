/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSslHttpConnection.java
 * Date 2009-11-3
 */
package org.eclipse.jt.core.jetty.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jt.core.jetty.ConstantsForDnaRsi;
import org.eclipse.jt.core.jetty.DetachedSocketChannelWithSSLHandler;
import org.eclipse.jt.core.jetty.io.nio.DnaSSLSelectChannelEndPoint;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSSLHttpConnection extends HttpConnection {
    private final DetachedSocketChannelWithSSLHandler dscHandler;
    private volatile boolean isDnaRsiConnection = false;

    public DnaSSLHttpConnection(Connector connector, EndPoint endpoint,
            Server server, DetachedSocketChannelWithSSLHandler dscHandler) {
        super(connector, endpoint, server);
        this.dscHandler = dscHandler;
    }

    public boolean isDnaRsiConnection() {
        return this.isDnaRsiConnection;
    }

    @Override
    protected void handleRequest() throws IOException {
        final String userAgent = this._requestFields
                .getStringField(ConstantsForDnaRsi.HFN_USER_AGENT);

        if (userAgent != null
                && userAgent.equals(ConstantsForDnaRsi.HFV_USER_AGENT)) {
            this.isDnaRsiConnection = true;
            if (this._endp instanceof DnaSSLSelectChannelEndPoint) {
                final DnaSSLSelectChannelEndPoint endp = (DnaSSLSelectChannelEndPoint) this._endp;
                SocketChannel channel = endp.tryDetachSocketChannel();
                if (channel != null) {
                    channel.socket().setTcpNoDelay(true);
                    channel.configureBlocking(true);
                    if (this.dscHandler != null) {
                        this.dscHandler.handleDetackedSocketChannel(channel,
                                endp.getEngine());
                    } else {
                        channel.close();
                        endp.getEngine().closeOutbound();
                    }
                    return;
                }
            }
        }

        this.handleUnrsiRequest();
    }

    void handleUnrsiRequest() throws IOException {
        super.handleRequest();
    }
}
