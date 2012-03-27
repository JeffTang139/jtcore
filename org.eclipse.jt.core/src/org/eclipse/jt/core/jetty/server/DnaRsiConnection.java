/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaRsiConnection.java
 * Date Dec 14, 2009
 */
package org.eclipse.jt.core.jetty.server;

import java.io.IOException;

import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;
import org.eclipse.jt.core.jetty.server.nio.DnaSelectChannelConnector;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaRsiConnection extends DnaHttpConnection {
	public DnaRsiConnection(DnaSelectChannelConnector connector,
			EndPoint endpoint, Server server,
			DetachedSocketChannelHandler dscHandler) {
		super(connector, endpoint, server, dscHandler);
	}

	@Override
	void handleUnrsiRequest() throws IOException {
		throw new IOException("不是有效的远程服务调用的连接请求");
	}
}