/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ReadableDaemon.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

import java.nio.channels.SelectionKey;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 可读通道的监护程序。<br/>
 * 本程序负责维护一个等待从通道中读取数据的连接对象的队列。如果队列中有连接，则把连接中的通道注册到选择器中。
 * 本程序同时负责从选择器中选出处于可读状态的通道，并通知相应的连接对象已经可以从通道读取数据了。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ReadableDaemon extends SelectorBased {

	/**
	 * 构造可读通道的监护对象。
	 * 
	 * @param connectionManager
	 *            连接管理器。
	 * @throws NullArgumentException
	 *             连接管理器为空。
	 * @throws CannotOpenSelectorException
	 *             选择器未能开启。
	 */
	ReadableDaemon(NetManager connectionManager) throws NullArgumentException {
		super(connectionManager, SelectionKey.OP_READ);
	}

	@Override
	final void internalStart() {
		RIUtil.startDaemon(this, "readable-dm");
	}

	@Override
	final void wakeupToWork(NetConnection netConnection) {
		netConnection.wakeupR();
	}
}
