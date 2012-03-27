/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteStub.java
 * Date 2009-2-17
 */
package org.eclipse.jt.core.impl;

/**
 * 远程请求存根的实现基类。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class RemoteRequestStubImpl extends RemoteRequestStubBase {

    RemoteRequestStubImpl(NetConnection connection,
            RemoteRequest<?> remoteRequest) {
        super(connection, remoteRequest);
    }

    @Override
    final void sendData() throws Throwable {
        RIUtil.send(this.id, this.netConnection, this.remoteRequest);
    }
}
