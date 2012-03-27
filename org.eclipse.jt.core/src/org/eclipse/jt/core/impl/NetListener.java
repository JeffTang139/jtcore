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
			throw new IllegalArgumentException("端口号超出了取值范围： " + port);
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
	 * Jetty的连接监听程序由JettyServer负责。
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
			ConsoleLog.debugError("关闭远程服务调用的连接监听程序的Socket通道时出现异常：%s", ignore);
		}
	}

	void acceptSocketChannel(SocketChannel socketChannel) {
		RIUtil.startDaemon(new NetRsiHttpHeaderHandler(socketChannel,
				this.netManager.ACCEPTOR), "connectionHandler");
	}

	@Override
	final Thread doStart() {
		try {
			ConsoleLog.info("启动远程服务调用的连接监听程序［端口：%s］...", this.port);
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.socket().bind(
					new InetSocketAddress(this.port));
			ConsoleLog.info("远程服务调用的连接监听程序［端口：%s］已经启动", this.port);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
		return RIUtil.startDaemon(new Runnable() {
			public void run() {
				SocketChannel sc;
				while (true) {
					if (Thread.interrupted()) {
						ConsoleLog.info("远程服务调用的连接监听程序［端口：%s］被要求关闭",
								DnaNetListener.this.port);
						ConsoleLog.info("远程服务调用的连接监听程序［端口：%s］开始执行关闭操作...",
								DnaNetListener.this.port);
						DnaNetListener.this.close();
						ConsoleLog.info("远程服务调用的连接监听程序［端口：%s］已经关闭",
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
										"远程服务调用的连接监听程序［端口：%s］在设置接收到的Socket为无延迟工作时出现异常：%s",
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
