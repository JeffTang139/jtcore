/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequest.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

/**
 * Զ������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteRequest<TRemoteRequestStub extends RemoteRequestStubBase>
// RemoteRequestStubImpl �������ʹ�ýӿڣ����ڲ�Ϊ�˷��㣬��RemoteRequestĿǰ�������⣬�����Ȳ���ʵ�����ˡ�
        extends RemoteCommand {
    RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable;

    TRemoteRequestStub newStub(NetConnection netConnection);
}
