/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteEvent.java
 * Date 2009-4-16
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Event;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
final class RemoteEvent implements RemoteRequest<RemoteEventStubImpl> {
    final Event event;
    final boolean wait;

    RemoteEvent(Event event, boolean wait) {
        if (event == null) {
            throw new NullArgumentException("event");
        }
        this.event = event;
        this.wait = wait;
    }

    public RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable {
        if (this.wait) {
            context.dispatch(this.event);
        } else {
            context.occur(this.event);
        }
        return VoidReturn.VOID;
    }

    public final PacketCode getPacketCode() {
        return PacketCode.EVENT_REQUEST;
    }

    public void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.writeDataOnly(this);
    }

    public RemoteEventStubImpl newStub(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("newConnection");
        }
        return new RemoteEventStubImpl(netConnection, this);
    }
}
