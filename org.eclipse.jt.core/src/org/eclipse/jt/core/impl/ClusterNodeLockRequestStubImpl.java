/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterNodeLockRequestStubImpl.java
 * Date 2009-7-6
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterNodeLockRequestStubImpl extends ClusterNodeRequestStubImpl {
    final NewAcquirer<?, ?> localLock;

    ClusterNodeLockRequestStubImpl(NetConnection netConnection,
            AbstractClusterLockInfo remoteLockRequest,
            NewAcquirer<?, ?> localLock) {
        super(netConnection, remoteLockRequest);
        if (localLock == null) {
            throw new NullArgumentException("localLock");
        }
        this.localLock = localLock;
    }

    public void setResult(Object result) {
        boolean locked = result == null ? false : ((Boolean) result)
                .booleanValue();
        if (locked
                && ((AbstractClusterLockInfo) this.remoteRequest).lockType != 0) {
            this.localLock
                    .localReceiveRemoteLocked(this.netConnection.remoteNodeInfo
                            .getNodeMask());
        }
    }
}
