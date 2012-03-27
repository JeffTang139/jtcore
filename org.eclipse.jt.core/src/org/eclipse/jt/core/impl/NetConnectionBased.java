/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionBased.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * �������ӵĶ���ĳ�����ࡣ
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class NetConnectionBased {
    final NetConnection netConnection;

    /**
     * @param netConnection
     *            ���Ӷ���
     * @throws NullArgumentException
     *             ���ָ�������Ӷ���Ϊ�գ�<code>null</code>����
     */
    NetConnectionBased(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        this.netConnection = netConnection;
    }
}
