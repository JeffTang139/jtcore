/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File WriteDaemon.java
 * Date 2009-2-20
 */
package org.eclipse.jt.core.impl;

import java.nio.channels.SelectionKey;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 可写通道的监护程序。<br/>
 * 本程序负责维护一个等待向通道中写入数据的连接对象的队列。如果队列中有连接，则把连接中的通道注册到选择器中。
 * 本程序同时负责从选择器中选出处于可写状态的通道，并通知相应的连接对象已经可以向通道写入数据了。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class WriteableDaemon extends SelectorBased implements Runnable {

	/**
	 * 构造可写通道的监护对象。
	 * 
	 * @param connectionManager
	 *            连接管理器。
	 * @throws NullArgumentException
	 *             连接管理器为空。
	 * @throws CannotOpenSelectorException
	 *             选择器未能开启。
	 */
	WriteableDaemon(NetManager connectionManager) throws NullArgumentException {
		super(connectionManager, SelectionKey.OP_WRITE);
	}

	@Override
	final void internalStart() {
		RIUtil.startDaemon(this, "writable-dm");
	}

	@Override
	final void wakeupToWork(NetConnection netConnection) {
		netConnection.wakeupW();
	}
}
