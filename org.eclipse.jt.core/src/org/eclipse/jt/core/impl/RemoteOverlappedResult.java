/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteOverlappedResult.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedResult;
import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 远程查询结果的实现类。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
        RemoteAsyncHandle implements
        MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
    /**
     * 远程查询。
     */
    final RemoteQuery remoteQuery;

    /**
     * 远程查询结果的构造器。
     * 
     * @param remoteQuery
     *            远程查询。
     * @param remoteQueryStub
     *            远程查询的存根。
     */
    RemoteOverlappedResult(RemoteQuery remoteQuery,
            RemoteQueryStubImpl remoteQueryStub) {
        super(remoteQueryStub);
        if (remoteQuery == null) {
            throw new NullArgumentException("remoteQuery");
        }
        this.remoteQuery = remoteQuery;
    }

    /**
     * 获取远程查询的存根对象。
     * 
     * @return 远程查询的存根对象。
     */
    private RemoteQueryStubImpl remoteQueryStub() {
        return (RemoteQueryStubImpl) this.remoteRequestStub;
    }

    /**
     * 获取查询结果的类型。
     * 
     * @return 查询结果的类型。
     */
    @SuppressWarnings("unchecked")
    public Class<TResult> getResultClass() {
        return this.remoteQuery.queryBy.getResultClass();
    }

    /**
     * 获取查询凭据中的第一个键。
     * 
     * @return 查询凭据中的第一个键。
     */
    @SuppressWarnings("unchecked")
    public TKey1 getKey1() {
        return (TKey1) this.remoteQuery.queryBy.getKey1();
    }

    /**
     * 获取查询凭据中的第二个键。
     * 
     * @return 查询凭据中的第二个键。
     */
    @SuppressWarnings("unchecked")
    public TKey2 getKey2() {
        return (TKey2) this.remoteQuery.queryBy.getKey2();
    }

    /**
     * 获取查询凭据中的第三个键。
     * 
     * @return 查询凭据中的第三个键。
     */
    @SuppressWarnings("unchecked")
    public TKey3 getKey3() {
        return (TKey3) this.remoteQuery.queryBy.getKey3();
    }

    /**
     * 获取查询凭据中前三个键之后的键。
     * 
     * @return 查询凭据中前三个键之后的键。
     */
    public Object[] getOtherKeys() {
        return this.remoteQuery.queryBy.getOtherKeys();
    }

    /**
     * 获取远程查询执行完毕后返回的结果。
     * 
     * @return 远程查询执行完毕后返回的结果。
     * @throws IllegalStateException
     *             执行过程尚未完成。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    @SuppressWarnings("unchecked")
    public TResult getResult() throws IllegalStateException,
            MissingObjectException {
        this.internalCheckStateForResultOK();
        return (TResult) this.remoteQueryStub().getResult();
    }

    /**
     * 检测远程查询执行完毕后返回的结果是否为空（<code>null</code>）。
     * 
     * @return 远程查询执行完毕后返回的结果如果为空，则为<code>true</code>，否则为<code>false</code>。
     * @throws IllegalStateException
     *             执行过程尚未完成。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    public boolean isNull() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return this.remoteQueryStub().isResultNull();
    }
}
