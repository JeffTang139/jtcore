/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterRequestHandler.java
 * Date 2009-5-25
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterRequestHandler extends Work {
    final NetConnection connection;
    final int id;

    ClusterRequestHandler(NetConnection netConnection, int requestId) {
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        this.connection = netConnection;
        this.id = requestId;
    }
}
