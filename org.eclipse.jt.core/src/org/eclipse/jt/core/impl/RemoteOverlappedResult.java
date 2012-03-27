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
 * Զ�̲�ѯ�����ʵ���ࡣ
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
        RemoteAsyncHandle implements
        MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
    /**
     * Զ�̲�ѯ��
     */
    final RemoteQuery remoteQuery;

    /**
     * Զ�̲�ѯ����Ĺ�������
     * 
     * @param remoteQuery
     *            Զ�̲�ѯ��
     * @param remoteQueryStub
     *            Զ�̲�ѯ�Ĵ����
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
     * ��ȡԶ�̲�ѯ�Ĵ������
     * 
     * @return Զ�̲�ѯ�Ĵ������
     */
    private RemoteQueryStubImpl remoteQueryStub() {
        return (RemoteQueryStubImpl) this.remoteRequestStub;
    }

    /**
     * ��ȡ��ѯ��������͡�
     * 
     * @return ��ѯ��������͡�
     */
    @SuppressWarnings("unchecked")
    public Class<TResult> getResultClass() {
        return this.remoteQuery.queryBy.getResultClass();
    }

    /**
     * ��ȡ��ѯƾ���еĵ�һ������
     * 
     * @return ��ѯƾ���еĵ�һ������
     */
    @SuppressWarnings("unchecked")
    public TKey1 getKey1() {
        return (TKey1) this.remoteQuery.queryBy.getKey1();
    }

    /**
     * ��ȡ��ѯƾ���еĵڶ�������
     * 
     * @return ��ѯƾ���еĵڶ�������
     */
    @SuppressWarnings("unchecked")
    public TKey2 getKey2() {
        return (TKey2) this.remoteQuery.queryBy.getKey2();
    }

    /**
     * ��ȡ��ѯƾ���еĵ���������
     * 
     * @return ��ѯƾ���еĵ���������
     */
    @SuppressWarnings("unchecked")
    public TKey3 getKey3() {
        return (TKey3) this.remoteQuery.queryBy.getKey3();
    }

    /**
     * ��ȡ��ѯƾ����ǰ������֮��ļ���
     * 
     * @return ��ѯƾ����ǰ������֮��ļ���
     */
    public Object[] getOtherKeys() {
        return this.remoteQuery.queryBy.getOtherKeys();
    }

    /**
     * ��ȡԶ�̲�ѯִ����Ϻ󷵻صĽ����
     * 
     * @return Զ�̲�ѯִ����Ϻ󷵻صĽ����
     * @throws IllegalStateException
     *             ִ�й�����δ��ɡ�
     * @throws RemoteException
     *             Զ�̵��ù����г����쳣��
     */
    @SuppressWarnings("unchecked")
    public TResult getResult() throws IllegalStateException,
            MissingObjectException {
        this.internalCheckStateForResultOK();
        return (TResult) this.remoteQueryStub().getResult();
    }

    /**
     * ���Զ�̲�ѯִ����Ϻ󷵻صĽ���Ƿ�Ϊ�գ�<code>null</code>����
     * 
     * @return Զ�̲�ѯִ����Ϻ󷵻صĽ�����Ϊ�գ���Ϊ<code>true</code>������Ϊ<code>false</code>��
     * @throws IllegalStateException
     *             ִ�й�����δ��ɡ�
     * @throws RemoteException
     *             Զ�̵��ù����г����쳣��
     */
    public boolean isNull() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return this.remoteQueryStub().isResultNull();
    }
}
