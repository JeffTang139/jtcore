/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteEventStubImpl.java
 * Date 2009-4-16
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteEventStubImpl extends RemoteRequestStubImpl implements
        RemoteEventStub {
    RemoteEventStubImpl(NetConnection connection, RemoteEvent remoteEvent) {
        super(connection, remoteEvent);
    }

    public void setResult(Object result) {
        // 没有任何数据需要返回
    }
}
