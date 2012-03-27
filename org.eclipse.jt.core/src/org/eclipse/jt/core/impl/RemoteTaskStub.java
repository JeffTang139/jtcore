/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTaskStub.java
 * Date 2009-2-17
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.Task;

/**
 * Զ����������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface RemoteTaskStub extends RemoteRequestStub {
    /**
     * ��ȡԶ������ִ����Ϻ󷵻ص��������
     * 
     * @return Զ������ִ����Ϻ󷵻ص��������
     * @throws RemoteException
     *             Զ�̵��ù����г����쳣��
     */
    @SuppressWarnings("unchecked")
    Task getReturnedTask() throws RemoteException;
}
