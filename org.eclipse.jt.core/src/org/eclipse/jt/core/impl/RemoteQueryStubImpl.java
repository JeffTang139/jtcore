/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQueryStub.java
 * Date 2009-2-16
 */
package org.eclipse.jt.core.impl;

/**
 * Զ�̲�ѯ�����ʵ�֡�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteQueryStubImpl extends RemoteRequestStubImpl implements
        RemoteQueryStub {

    /**
     * Զ�̲�ѯ���صĽ����
     */
    private Object result;

    /**
     * Զ�̲�ѯ����Ĺ�������
     * 
     * @param connection
     *            ���ӡ�
     * @param remoteQuery
     *            Զ������
     */
    RemoteQueryStubImpl(NetConnection connection, RemoteQueryBase<?> remoteQuery) {
        super(connection, remoteQuery);
    }

    RemoteQueryStubImpl(NetConnection connection,
            RemoteStructQuery remoteStructQuery) {
        super(connection, remoteStructQuery);
    }

    /**
     * ��ȡԶ�̲�ѯ���صĽ����
     * 
     * @return Զ�̲�ѯ���صĽ����
     * @throws RemoteException
     *             Զ�̵��ù����г����쳣��
     */
    public Object getResult() throws RemoteException {
        this.internalCheckException();
        return this.result;
    }

    /**
     * ���Զ�̲�ѯ���صĽ���Ƿ�Ϊ�գ�<code>null</code>����
     * 
     * @return ���Զ�̲�ѯ���صĽ��Ϊ�գ���Ϊ<code>true</code>������Ϊ<code>false</code>��
     */
    public boolean isResultNull() throws RemoteException {
        this.internalCheckException();
        return this.result == null;
    }

    /**
     * ����Զ�̲�ѯ���صĽ����
     * 
     * @param result
     *            Զ�̲�ѯ���صĽ����
     */
    public void setResult(Object result) {
        this.result = result;
    }
}
