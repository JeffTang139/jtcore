/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTaskStub.java
 * Date 2009-2-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.Task;

/**
 * 远程任务存根的实现。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteTaskStubImpl extends RemoteRequestStubImpl implements
        RemoteTaskStub {

    /**
     * 远程任务执行后返回的任务对象。
     */
    @SuppressWarnings("unchecked")
    private Task returnedTask;

    RemoteTaskStubImpl(NetConnection connection, RemoteTask remoteTask) {
        super(connection, remoteTask);
    }

    @SuppressWarnings("unchecked")
    public Task getReturnedTask() throws RemoteException {
        this.internalCheckException();
        return this.returnedTask;
    }

    @SuppressWarnings("unchecked")
    public void setResult(Object result) {
        if (result instanceof Task) {
            this.returnedTask = (Task) result;
        } else {
            this.setLocalException(new UnknownObjectException(
                    result == null ? null : result.toString()));
        }
    }
}
