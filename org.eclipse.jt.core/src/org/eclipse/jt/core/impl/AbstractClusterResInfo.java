/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AbstractResourceInfo.java
 * Date May 6, 2009
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class AbstractClusterResInfo implements
        RemoteRequest<ClusterNodeRequestStubImpl> {

    final Class<?> facadeClass;
    // 必须得可以序列化
    final Object categoryOrId;

    final long itemId;

    AbstractClusterResInfo(Object categoryOrId, Class<?> facadeClass,
            long resourceItemId) {
        if (categoryOrId == null) {
            throw new NullArgumentException("categoryOrId");
        }
        if (facadeClass == null) {
            throw new NullArgumentException("facadeClass");
        }
        this.categoryOrId = categoryOrId;
        this.facadeClass = facadeClass;
        this.itemId = resourceItemId;
    }

    // ///////////////////////////////////////////////////

    public final ClusterNodeRequestStubImpl newStub(NetConnection netConnection) {
        return new VoidClusterNodeRequestStubImpl(netConnection, this);
    }

    public final PacketCode getPacketCode() {
        return PacketCode.CLUSTER_DATA_REQUEST;
    }

    public final void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.writeDataOnly(this);
    }

    public final RemoteReturn execute(ContextImpl<?, ?, ?> context)
            throws Throwable {
        this.exec(context);
        return VoidReturn.VOID;
    }

    abstract void exec(ContextImpl<?, ?, ?> context) throws Throwable;

    private static final class VoidClusterNodeRequestStubImpl extends
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
