/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteException.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.net.InetAddress;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 远程异常。<br/>
 * 当远程请求在远程主机的处理过程中出现异常并返回后，用该异常封装返回的异常信息。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class RemoteException extends RuntimeException {
    private static final long serialVersionUID = -2296371108053677280L;

    /**
     * 远程请求。
     */
    final RemoteRequest<?> remoteRequest;
    /**
     * 远程主机的地址。
     */
    final InetAddress address;
    /**
     * 远程主机的端口号。
     */
    final int port;

    /**
     * 远程异常的构造器。
     * 
     * @param remoteRequest
     *            远程请求。
     * @param address
     *            远程主机的地址。
     * @param port
     *            远程主机的端口号。
     * @param cause
     *            在远程主机中发生的异常。
     */
    RemoteException(RemoteRequest<?> remoteRequest, InetAddress address,
            int port, ThrowableAdapter cause) {
        super("远程请求（" + remoteRequest + "）在远程主机（" + address.getHostAddress()
                + ":" + port + "）处理的过程中出现异常", cause);
        if (remoteRequest == null) {
            throw new NullArgumentException("remoteRequest");
        }
        this.remoteRequest = remoteRequest;
        this.address = address;
        this.port = port;
    }

    /**
     * 什么都不做，直接返回该异常实例。
     */
    @Override
    public final Throwable fillInStackTrace() {
        return this;
    }

    /**
     * 不支持的操作。
     * 
     * @throws UnsupportedOperationException
     *             调用该方法时。
     */
    @Override
    public final Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }
}
