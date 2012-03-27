/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionBased.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 基于连接的对象的抽象基类。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class NetConnectionBased {
    final NetConnection netConnection;

    /**
     * @param netConnection
     *            连接对象。
     * @throws NullArgumentException
     *             如果指定的连接对象为空（<code>null</code>）。
     */
    NetConnectionBased(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        this.netConnection = netConnection;
    }
}
