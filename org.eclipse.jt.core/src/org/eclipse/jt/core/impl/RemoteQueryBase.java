/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQueryBase.java
 * Date 2009-4-16
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
abstract class RemoteQueryBase<TRemoteQueryStub extends RemoteRequestStubImpl>
        implements RemoteRequest<TRemoteQueryStub> {
    /**
     * ²éÑ¯Æ¾¾Ý¡£
     */
    final QueryBy queryBy;

    RemoteQueryBase(QueryBy queryBy) {
        if (queryBy == null) {
            throw new NullArgumentException("queryBy");
        }
        this.queryBy = queryBy;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.queryBy;
    }

    public final void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.writeDataOnly(this);
    }

    public final PacketCode getPacketCode() {
        return PacketCode.QUERY_REQUEST;
    }

    public final RemoteReturn execute(ContextImpl<?, ?, ?> context)
            throws Throwable {
        return (new QueryReturn(this.execQuery(context)));
    }

    abstract Object execQuery(ContextImpl<?, ?, ?> context);
}
