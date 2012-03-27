/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequestStub.java
 * Date 2009-2-17
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface RemoteRequestStub {

    int id();

    PacketCode requestPacketCode();

    Throwable getException();

    boolean noException();
}
