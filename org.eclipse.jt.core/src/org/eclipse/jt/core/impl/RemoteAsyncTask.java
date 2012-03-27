/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteAsyncTask.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Task;

/**
 * 远程异步处理的任务。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteAsyncTask<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
        extends RemoteAsyncHandle implements AsyncTask<TTask, TMethod> {
    /**
     * 远程任务。
     */
    final RemoteTask remoteTask;

    /**
     * 远程异步处理的任务的构造器。
     * 
     * @param remoteTask
     *            远程任务。
     * @param remoteTaskStub
     *            远程任务存根。
     */
    RemoteAsyncTask(RemoteTask remoteTask, RemoteTaskStubImpl remoteTaskStub) {
        super(remoteTaskStub);
        if (remoteTask == null) {
            throw new NullArgumentException("remoteTask");
        }
        this.remoteTask = remoteTask;
    }

    /**
     * 获取远程任务存根。
     * 
     * @return 远程任务存根。
     */
    private RemoteTaskStubImpl remoteTaskStub() {
        return (RemoteTaskStubImpl) this.remoteRequestStub;
    }

    /**
     * 获取远程任务对应的处理方法。
     * 
     * @return 远程任务对应的处理方法。
     */
    @SuppressWarnings("unchecked")
    public TMethod getMethod() {
        return (TMethod) this.remoteTask.getMethod();
    }

    /**
     * 获取远程任务执行完毕后的任务对象。
     * 
     * @return 远程任务执行完毕后的任务对象。
     * @throws IllegalStateException
     *             远程任务尚未执行完毕。
     * @throws RemoteException
     *             远程任务执行时出现异常。
     */
    @SuppressWarnings("unchecked")
    public TTask getTask() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return (TTask) this.remoteTaskStub().getReturnedTask();
    }
}
