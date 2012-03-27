/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionManagerBased.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructField;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 基于网络管理器的对象的抽象基类。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class NetManagerBased {
    @StructField(stateField = false)
    final transient NetManager netManager;

    /**
     * 以指定的连接管理器构造该类的对象。
     * 
     * @param netManager
     *            网络管理器。
     * @throws NullArgumentException
     *             当提供的网络管理器为空（<code>null</code>）时。
     */
    NetManagerBased(NetManager netManager) {
        if (netManager == null) {
            throw new NullArgumentException("netManager");
        }
        this.netManager = netManager;
    }
}
