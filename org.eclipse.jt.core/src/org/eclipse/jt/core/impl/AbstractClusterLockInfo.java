/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteLockInfo.java
 * Date 2009-5-18
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class AbstractClusterLockInfo implements
        RemoteRequest<ClusterNodeRequestStubImpl> {
    final int lockType;

    final int groupId;

    AbstractClusterLockInfo(int lockType, int resourceGroupId) {
        this.lockType = lockType;
        this.groupId = resourceGroupId;
    }

    // ///////////////////////////////////////////////////

    final ClusterNodeLockRequestStubImpl newStub(NetConnection netConnection,
            NewAcquirer<?, ?> localLock) {
        return new ClusterNodeLockRequestStubImpl(netConnection, this,
                localLock);
    }

    public final ClusterNodeRequestStubImpl newStub(NetConnection netConnection) {
        throw new UnsupportedOperationException();
    }

    public final PacketCode getPacketCode() {
        return PacketCode.CLUSTER_LOCK_REQUEST;
    }

    public final void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.writeDataOnly(this);
    }

    public final RemoteReturn execute(ContextImpl<?, ?, ?> context)
            throws Throwable {
        return this.exec(context) ? RemoteLockReturn.TRUE
                : RemoteLockReturn.FALSE;
    }

    abstract boolean exec(ContextImpl<?, ?, ?> context) throws Throwable;
}
