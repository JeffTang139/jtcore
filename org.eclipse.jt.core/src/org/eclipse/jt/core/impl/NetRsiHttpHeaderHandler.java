/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NetRsiHttpHeaderHandler.java
 * Date Dec 10, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.jetty.DetachedSocketChannelHandler;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class NetRsiHttpHeaderHandler implements Runnable {
	private final SocketChannel socketChannel;
	private final DetachedSocketChannelHandler handler;

	NetRsiHttpHeaderHandler(SocketChannel socketChannel,
	        DetachedSocketChannelHandler socketChannelHandler) {
		if (socketChannel == null) {
			throw new NullArgumentException("socketChannel");
		}
		if (socketChannelHandler == null) {
			throw new NullArgumentException("socketChannelHandler");
		}
		this.socketChannel = socketChannel;
		this.handler = socketChannelHandler;
	}

	public void run() {
		// XXX manage buffers
		ByteBuffer buf = ByteBuffer.allocate(128);
		// XXX to parse http header in normal way
		try {
			this.socketChannel.configureBlocking(false);
			int read;
			long zeroStartAt = 0;
			do {
				read = this.socketChannel.read(buf);
				if (read < 0) { // closed
					this.socketChannel.close();
					return;
				} else if (read == 0) { // no data
					if (zeroStartAt == 0) {
						zeroStartAt = System.currentTimeMillis();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							this.socketChannel.close();
							ConsoleLog.debugInfo("���ӳ���Ҫ����ֹ��%s", e);
							return;
						}
					} else {
						if (System.currentTimeMillis() - zeroStartAt > 1000 * 60 * 2) {
							this.socketChannel.close(); // refuse
							return;
						}
					}
				}
			} while (buf.position() < 4);
			buf.flip();
			if (buf.get() != 'H' || buf.get() != 'E' || buf.get() != 'A'
			        || buf.get() != 'D') {
				this.socketChannel.close(); // refuse
				return;
			}

			while (buf.hasRemaining() && buf.get() != 'U') {
			}

			// int start = buf.position() - 1;

			this.handler.handleDetackedSocketChannel(this.socketChannel);
		} catch (IOException e) {
			ConsoleLog.debugError("Զ�̷�����õķ������ڴ����������������ͨ��ʱ�����쳣��", e);
		}

		// TODO Auto-generated method stub
	}
}
