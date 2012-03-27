/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NetListener.java
 * Date Dec 7, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class NetListener extends NetManagerBased {
	final int port;
	private volatile Thread listening;

	NetListener(NetManager netManager, int port) {
		super(netManager);
		if (port < 0 || port > 0xFFFF) {
			throw new IllegalArgumentException("�˿ںų�����ȡֵ��Χ�� " + port);
		}
		this.port = port;
	}

	final void start() {
		this.listening = this.doStart();
	}

	final void stop() {
		Thread t = this.listening;
		if (t != null) {
			this.listening = null;
			t.interrupt();
		}
	}

	abstract Thread doStart();

	abstract boolean isSecure();
}

class JettyListener extends NetListener {
	JettyListener(NetManager netManager, int port) {
		super(netManager, port);
	}

	/**
	 * Jetty�����Ӽ���������JettyServer����
	 */
	@Override
	final Thread doStart() {
		return null;
	}

	@Override
	boolean isSecure() {
		return false;
	}
}

final class SecureJettyListener extends JettyListener {
	SecureJettyListener(NetManager netManager, int port) {
		super(netManager, port);
	}

	@Override
	boolean isSecure() {
		return true;
	}
}

class DnaNetListener extends NetListener {
	private ServerSocketChannel serverSocketChannel;

	DnaNetListener(NetManager netManager, int port) {
		super(netManager, port);
	}

	@Override
	boolean isSecure() {
		return false;
	}

	void close() {
		try {
			DnaNetListener.this.serverSocketChannel.close();
		} catch (Throwable ignore) {
			ConsoleLog.debugError("�ر�Զ�̷�����õ����Ӽ��������Socketͨ��ʱ�����쳣��%s", ignore);
		}
	}

	void acceptSocketChannel(SocketChannel socketChannel) {
		RIUtil.startDaemon(new NetRsiHttpHeaderHandler(socketChannel,
				this.netManager.ACCEPTOR), "connectionHandler");
	}

	@Override
	final Thread doStart() {
		try {
			ConsoleLog.info("����Զ�̷�����õ����Ӽ�������۶˿ڣ�%s��...", this.port);
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.socket().bind(
					new InetSocketAddress(this.port));
			ConsoleLog.info("Զ�̷�����õ����Ӽ�������۶˿ڣ�%s���Ѿ�����", this.port);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
		return RIUtil.startDaemon(new Runnable() {
			public void run() {
				SocketChannel sc;
				while (true) {
					if (Thread.interrupted()) {
						ConsoleLog.info("Զ�̷�����õ����Ӽ�������۶˿ڣ�%s�ݱ�Ҫ��ر�",
								DnaNetListener.this.port);
						ConsoleLog.info("Զ�̷�����õ����Ӽ�������۶˿ڣ�%s�ݿ�ʼִ�йرղ���...",
								DnaNetListener.this.port);
						DnaNetListener.this.close();
						ConsoleLog.info("Զ�̷�����õ����Ӽ�������۶˿ڣ�%s���Ѿ��ر�",
								DnaNetListener.this.port);
						break;
					}

					sc = null;
					try {
						sc = DnaNetListener.this.serverSocketChannel.accept();
						sc.socket().setTcpNoDelay(true);
						sc.configureBlocking(true);
						DnaNetListener.this.acceptSocketChannel(sc);
					} catch (IOException e) {
						if (sc == null) {
							throw Utils.tryThrowException(e);
						}
						try {
							sc.close();
						} catch (Exception ignore) {
						}
						ConsoleLog
								.debugError(
										"Զ�̷�����õ����Ӽ�������۶˿ڣ�%s�������ý��յ���SocketΪ���ӳٹ���ʱ�����쳣��%s",
										DnaNetListener.this.port, e);
						continue;
					}
				}
			}
		}, "accepter");
	}
}

final class SecureDnaNetListener extends DnaNetListener {
	SecureDnaNetListener(NetManager netManager, int port) {
		super(netManager, port);
	}

	@Override
	boolean isSecure() {
		return true;
	}

	@Override
	void close() {
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	void acceptSocketChannel(SocketChannel socketChannel) {
		// TODO parse http header
		this.netManager.ACCEPTOR.handleDetackedSocketChannel(socketChannel,
				null);
	}
}
