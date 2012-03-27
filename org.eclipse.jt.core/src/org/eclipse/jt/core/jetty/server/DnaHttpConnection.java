/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaHttpConnection.java
 * Date 2009-10-16
 */
package org.eclipse.jt.core.jetty.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.eclipse.jetty.http.Generator;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jt.core.jetty.ConstantsForDnaRsi;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;
import org.eclipse.jt.core.jetty.io.nio.DnaSelectChannelEndPoint;
import org.eclipse.jt.core.jetty.server.nio.DnaSelectChannelConnector;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaHttpConnection extends HttpConnection {
	private final DetachedSocketChannelHandler dscHandler;
	private volatile boolean isDnaRsiConnection = false;

	public DnaHttpConnection(DnaSelectChannelConnector connector,
			EndPoint endpoint, Server server,
			DetachedSocketChannelHandler dscHandler) {
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
			if (this._endp instanceof DnaSelectChannelEndPoint) {
				final DnaSelectChannelEndPoint endp = (DnaSelectChannelEndPoint) this._endp;
				SocketChannel channel = endp.tryDetachSocketChannel();
				if (channel != null) {
					channel.socket().setTcpNoDelay(true);
					channel.configureBlocking(true);
					if (this.dscHandler != null) {
						this.dscHandler.handleDetackedSocketChannel(channel);
					} else {
						channel.close();
					}
					return;
				}
			}
		}
		this.handleUnrsiRequest();
	}

	void handleUnrsiRequest() throws IOException {
		try {
			super.handleRequest();
		} finally {
			final HttpParser parser = (HttpParser) this._parser;
			final long request = parser != null ? parser.getContentRead() : 0;
			final Generator generator = this._generator;
			final long respose = generator != null ? generator
					.getContentWritten() : 0;
			((DnaSelectChannelConnector) this._connector).onHTTPBytes(request,
					respose);
		}
	}
}
