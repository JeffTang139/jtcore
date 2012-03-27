/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteReturn.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Task;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class RemoteReturn implements RemoteCommand {
    final Object data;

    RemoteReturn(Object data) {
        this.data = data;
    }

    public void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.writeDataOnly(this);
    }

    abstract void setReturn(ReturnReceivable receiver);
}

final class VoidReturn extends RemoteReturn {

    static final VoidReturn VOID = new VoidReturn();

    VoidReturn() {
        super(null);
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        // nothing.
    }

    public PacketCode getPacketCode() {
        return PacketCode.VOID_RETURN;
    }
}

@StructClass
final class QueryReturn extends RemoteReturn {
    QueryReturn(Object result) {
        super(result);
    }

    public final PacketCode getPacketCode() {
        return PacketCode.QUERY_RETURN;
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        receiver.setResult(this.data);
    }
}

@StructClass
final class StructReturn extends RemoteReturn {
    StructReturn(Object result) {
        super(result);
    }

    @Override
    public void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.serialize(this);
    }

    public final PacketCode getPacketCode() {
        return PacketCode.STRUCT_RETURN;
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        receiver.setResult(this.data);
    }
}

@StructClass
final class TaskReturn extends RemoteReturn {
    @SuppressWarnings("unchecked")
    TaskReturn(Task result) {
        super(result);
    }

    public final PacketCode getPacketCode() {
        return PacketCode.TASK_RETURN;
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        receiver.setResult(this.data);
    }
}

@StructClass
class ExceptionReturn extends RemoteReturn {
    ExceptionReturn(ThrowableAdapter exception) {
        super(exception);
        if (exception == null) {
            throw new NullArgumentException("exception");
        }
    }

    public PacketCode getPacketCode() {
        return PacketCode.EXCEPTION_RETURN;
    }

    @Override
    public final void writeTo(StructuredObjectSerializer serializer)
            throws IOException, StructDefineNotFoundException {
        serializer.serialize(this);
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        receiver.setRemoteException((ThrowableAdapter) this.data);
    }
}

@StructClass
final class RemoteForceCancelStub extends ExceptionReturn {
    RemoteForceCancelStub(Throwable exception) {
        super(exception == null ? null : new ThrowableAdapter(exception));
    }

    @Override
    public final PacketCode getPacketCode() {
        return PacketCode.FORCE_CANCEL_STUB;
    }
}

final class RemoteLockReturn extends RemoteReturn {
    static final RemoteLockReturn TRUE = new RemoteLockReturn(true);
    static final RemoteLockReturn FALSE = new RemoteLockReturn(false);

    RemoteLockReturn(boolean locked) {
        super(locked);
    }

    @Override
    void setReturn(ReturnReceivable receiver) {
        receiver.setResult(this.data);
    }

    public PacketCode getPacketCode() {
        return PacketCode.CLUSTER_LOCK_RETURN;
    }
}
