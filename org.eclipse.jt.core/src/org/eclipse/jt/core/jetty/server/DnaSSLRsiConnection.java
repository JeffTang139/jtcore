/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSSLRsiConnection.java
 * Date Dec 14, 2009
 */
package org.eclipse.jt.core.jetty.server;

import java.io.IOException;

import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jt.core.jetty.DetachedSocketChannelWithSSLHandler;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSSLRsiConnection extends DnaSSLHttpConnection {
    public DnaSSLRsiConnection(Connector connector, EndPoint endpoint,
            Server server, DetachedSocketChannelWithSSLHandler dscHandler) {
        super(connector, endpoint, server, dscHandler);
    }

    @Override
    void handleUnrsiRequest() throws IOException {
        throw new IOException("不是有效的远程服务调用的连接请求");
    }
}
