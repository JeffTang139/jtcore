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
 * Զ���첽���������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteAsyncTask<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
        extends RemoteAsyncHandle implements AsyncTask<TTask, TMethod> {
    /**
     * Զ������
     */
    final RemoteTask remoteTask;

    /**
     * Զ���첽���������Ĺ�������
     * 
     * @param remoteTask
     *            Զ������
     * @param remoteTaskStub
     *            Զ����������
     */
    RemoteAsyncTask(RemoteTask remoteTask, RemoteTaskStubImpl remoteTaskStub) {
        super(remoteTaskStub);
        if (remoteTask == null) {
            throw new NullArgumentException("remoteTask");
        }
        this.remoteTask = remoteTask;
    }

    /**
     * ��ȡԶ����������
     * 
     * @return Զ����������
     */
    private RemoteTaskStubImpl remoteTaskStub() {
        return (RemoteTaskStubImpl) this.remoteRequestStub;
    }

    /**
     * ��ȡԶ�������Ӧ�Ĵ�������
     * 
     * @return Զ�������Ӧ�Ĵ�������
     */
    @SuppressWarnings("unchecked")
    public TMethod getMethod() {
        return (TMethod) this.remoteTask.getMethod();
    }

    /**
     * ��ȡԶ������ִ����Ϻ���������
     * 
     * @return Զ������ִ����Ϻ���������
     * @throws IllegalStateException
     *             Զ��������δִ����ϡ�
     * @throws RemoteException
     *             Զ������ִ��ʱ�����쳣��
     */
    @SuppressWarnings("unchecked")
    public TTask getTask() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return (TTask) this.remoteTaskStub().getReturnedTask();
    }
}
