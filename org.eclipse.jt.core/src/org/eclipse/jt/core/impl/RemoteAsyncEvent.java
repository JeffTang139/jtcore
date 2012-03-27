/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteAsyncEvent.java
 * Date 2009-4-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Event;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteAsyncEvent extends RemoteAsyncHandle implements AsyncEvent {
    final RemoteEvent remoteEvent;

    RemoteAsyncEvent(RemoteEvent remoteEvent,
            RemoteEventStubImpl remoteEventStub) {
        super(remoteEventStub);
        if (remoteEvent == null) {
            throw new NullArgumentException("remoteEvent");
        }
        this.remoteEvent = remoteEvent;
    }

    public final Event getEvent() {
        return this.remoteEvent.event;
    }

    public final boolean needWait() {
        return this.remoteEvent.wait;
    }
}
