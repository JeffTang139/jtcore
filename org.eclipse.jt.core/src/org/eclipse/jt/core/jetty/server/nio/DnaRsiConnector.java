/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaRsiConnector.java
 * Date Dec 14, 2009
 */
package org.eclipse.jt.core.jetty.server.nio;

import java.nio.channels.SocketChannel;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.util.thread.Timeout.Task;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;
import org.eclipse.jt.core.jetty.server.DnaRsiConnection;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class DnaRsiConnector extends DnaSelectChannelConnector {
	public DnaRsiConnector(DetachedSocketChannelHandler handler,
			HTTPBytesRecorder bytesRecorder) {
		super(handler, bytesRecorder);
	}

	@Override
	protected Connection newConnection(SocketChannel channel,
			final SelectChannelEndPoint endpoint) {
		return new DnaRsiConnection(DnaRsiConnector.this, endpoint, this
				.getServer(), this.dscHandler) {
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
}
