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
 * Զ���쳣��<br/>
 * ��Զ��������Զ�������Ĵ�������г����쳣�����غ��ø��쳣��װ���ص��쳣��Ϣ��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class RemoteException extends RuntimeException {
    private static final long serialVersionUID = -2296371108053677280L;

    /**
     * Զ������
     */
    final RemoteRequest<?> remoteRequest;
    /**
     * Զ�������ĵ�ַ��
     */
    final InetAddress address;
    /**
     * Զ�������Ķ˿ںš�
     */
    final int port;

    /**
     * Զ���쳣�Ĺ�������
     * 
     * @param remoteRequest
     *            Զ������
     * @param address
     *            Զ�������ĵ�ַ��
     * @param port
     *            Զ�������Ķ˿ںš�
     * @param cause
     *            ��Զ�������з������쳣��
     */
    RemoteException(RemoteRequest<?> remoteRequest, InetAddress address,
            int port, ThrowableAdapter cause) {
        super("Զ������" + remoteRequest + "����Զ��������" + address.getHostAddress()
                + ":" + port + "������Ĺ����г����쳣", cause);
        if (remoteRequest == null) {
            throw new NullArgumentException("remoteRequest");
        }
        this.remoteRequest = remoteRequest;
        this.address = address;
        this.port = port;
    }

    /**
     * ʲô��������ֱ�ӷ��ظ��쳣ʵ����
     */
    @Override
    public final Throwable fillInStackTrace() {
        return this;
    }

    /**
     * ��֧�ֵĲ�����
     * 
     * @throws UnsupportedOperationException
     *             ���ø÷���ʱ��
     */
    @Override
    public final Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }
}
