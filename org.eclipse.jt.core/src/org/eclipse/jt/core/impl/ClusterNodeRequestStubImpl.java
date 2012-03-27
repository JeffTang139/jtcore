/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterNodeRequestStubImpl.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ClusterNodeRequestStubImpl extends RemoteRequestStubBase {

    ClusterNodeRequestStubImpl(NetConnection netConnection,
            RemoteRequest<?> remoteRequest) {
        super(netConnection, remoteRequest);
    }

    @Override
    final void sendData() throws Throwable {
        // nothing.
    }

    final void sendData(ByteBufferWrapper data) {
        this.netConnection.toSend(data);
    }

    static final class VoidClusterNodeRequestStubImpl extends
            ClusterNodeRequestStubImpl {
        VoidClusterNodeRequestStubImpl(NetConnection netConnection,
                RemoteRequest<?> remoteRequest) {
            super(netConnection, remoteRequest);
        }

        public void setResult(Object result) {
            // nothing.
        }
    }
}
