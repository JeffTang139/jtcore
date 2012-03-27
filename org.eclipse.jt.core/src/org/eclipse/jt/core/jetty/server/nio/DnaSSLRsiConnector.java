/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSSLRsiConnector.java
 * Date Dec 14, 2009
 */
package org.eclipse.jt.core.jetty.server.nio;

import java.nio.channels.SocketChannel;

import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.util.thread.Timeout.Task;
import org.eclipse.jt.core.jetty.DetachedSocketChannelWithSSLHandler;
import org.eclipse.jt.core.jetty.server.DnaSSLRsiConnection;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSSLRsiConnector extends DnaSSLSelectChannelConnector {
    public DnaSSLRsiConnector(DetachedSocketChannelWithSSLHandler handler) {
        super(handler);
    }

    @Override
    protected Connection newConnection(SocketChannel channel,
            final SelectChannelEndPoint endpoint) {
        DnaSSLRsiConnection rsiConnection = new DnaSSLRsiConnection(
                DnaSSLRsiConnector.this, endpoint, this.getServer(),
                this.dscHandler) {
            @Override
            public void cancelTimeout(Task task) {
                endpoint.getSelectSet().cancelTimeout(task);
            }

            @Override
            public void scheduleTimeout(Task task, long timeoutMs) {
                endpoint.getSelectSet().scheduleTimeout(task, timeoutMs);
            }
        };
        ((HttpParser) rsiConnection.getParser()).setForceContentBuffer(true);
        return rsiConnection;
    }
}
