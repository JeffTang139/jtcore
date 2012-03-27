/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQueryStub.java
 * Date 2009-2-16
 */
package org.eclipse.jt.core.impl;

/**
 * 远程查询存根的实现。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteQueryStubImpl extends RemoteRequestStubImpl implements
        RemoteQueryStub {

    /**
     * 远程查询返回的结果。
     */
    private Object result;

    /**
     * 远程查询存根的构造器。
     * 
     * @param connection
     *            连接。
     * @param remoteQuery
     *            远程请求。
     */
    RemoteQueryStubImpl(NetConnection connection, RemoteQueryBase<?> remoteQuery) {
        super(connection, remoteQuery);
    }

    RemoteQueryStubImpl(NetConnection connection,
            RemoteStructQuery remoteStructQuery) {
        super(connection, remoteStructQuery);
    }

    /**
     * 获取远程查询返回的结果。
     * 
     * @return 远程查询返回的结果。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    public Object getResult() throws RemoteException {
        this.internalCheckException();
        return this.result;
    }

    /**
     * 检查远程查询返回的结果是否为空（<code>null</code>）。
     * 
     * @return 如果远程查询返回的结果为空，则为<code>true</code>，否则为<code>false</code>。
     */
    public boolean isResultNull() throws RemoteException {
        this.internalCheckException();
        return this.result == null;
    }

    /**
     * 设置远程查询返回的结果。
     * 
     * @param result
     *            远程查询返回的结果。
     */
    public void setResult(Object result) {
        this.result = result;
    }
}
