/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTaskStub.java
 * Date 2009-2-17
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.Task;

/**
 * 远程任务存根。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface RemoteTaskStub extends RemoteRequestStub {
    /**
     * 获取远程任务执行完毕后返回的任务对象。
     * 
     * @return 远程任务执行完毕后返回的任务对象。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    @SuppressWarnings("unchecked")
    Task getReturnedTask() throws RemoteException;
}
