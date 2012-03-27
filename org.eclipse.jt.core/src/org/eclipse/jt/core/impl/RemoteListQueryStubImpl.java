/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteListQueryStubImpl.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程结果列表查询存根的实现。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteListQueryStubImpl extends RemoteRequestStubImpl implements
        RemoteListQueryStub {

    @SuppressWarnings("unchecked")
    private List resultList = EMPTY;

    RemoteListQueryStubImpl(NetConnection connection,
            RemoteListQuery remoteListQuery) {
        super(connection, remoteListQuery);
    }

    @SuppressWarnings("unchecked")
    private static final List EMPTY = new ArrayList(0);

    @SuppressWarnings("unchecked")
    public List getResultList() throws RemoteException {
        this.internalCheckException();
        return this.resultList;
    }

    @SuppressWarnings("unchecked")
    public void setResult(Object result) {
        if (result instanceof List) {
            this.resultList = (List) result;
        } else {
            this.setLocalException(new UnknownObjectException(
                    result == null ? null : result.toString()));
        }
    }
}
