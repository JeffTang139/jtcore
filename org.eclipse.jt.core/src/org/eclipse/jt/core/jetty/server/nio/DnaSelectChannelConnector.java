/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSelectChannelConnector.java
 * Date 2009-10-16
 */
package org.eclipse.jt.core.jetty.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.Timeout.Task;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;
import org.eclipse.jt.core.jetty.io.nio.DnaSelectChannelEndPoint;
import org.eclipse.jt.core.jetty.server.DnaHttpConnection;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSelectChannelConnector extends SelectChannelConnector {
	final DetachedSocketChannelHandler dscHandler;
	final HTTPBytesRecorder bytesRecorder;

	public final void onHTTPBytes(long request, long respose) {
		if (this.bytesRecorder != null) {
			this.bytesRecorder.onHTTPBytes(request, respose);
		}
	}

	public DnaSelectChannelConnector(DetachedSocketChannelHandler handler,
			HTTPBytesRecorder bytesRecorder) {
		if (handler == null) {
			throw new NullPointerException();
		}
		this.dscHandler = handler;
		this.bytesRecorder = bytesRecorder;
	}

	@Override
	protected Connection newConnection(SocketChannel channel,
			final SelectChannelEndPoint endpoint) {
		return new DnaHttpConnection(DnaSelectChannelConnector.this, endpoint,
				this.getServer(), this.dscHandler) {
			@Override
			public void cancelTimeout(Task task) {
				endpoint.getSelectSet().cancelTimeout(task);
			}

			@Override
			public void scheduleTimeout(Task task, long timeoutMs) {
				endpoint.getSelectSet().scheduleTimeout(task, timeoutMs);
			}
		};
	}

	@Override
	protected SelectChannelEndPoint newEndPoint(SocketChannel channel,
			SelectSet selectSet, SelectionKey key) throws IOException {
		return new DnaSelectChannelEndPoint(channel, selectSet, key);
	}

	@Override
	public void connectionClosed(HttpConnection connection) {
		super.connectionClosed(connection);
	}
}
