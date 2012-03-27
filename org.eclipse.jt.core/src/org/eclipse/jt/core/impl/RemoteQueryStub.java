/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQueryStub.java
 * Date 2009-2-17
 */
package org.eclipse.jt.core.impl;

/**
 * 远程查询存根。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteQueryStub extends RemoteRequestStub {

    /**
     * 获取远程查询执行完毕后返回的结果。
     * 
     * @return 远程查询执行完毕后返回的结果。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    Object getResult() throws RemoteException;

    /**
     * 检查远程查询执行完毕后返回的结果是否为空（<code>null</code>）。
     * 
     * @return 如果远程查询执行完毕后返回的结果为空，则为<code>true</code>，否则为<code>false</code>。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    boolean isResultNull() throws RemoteException;
}
