/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteOverlappedResultList.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedResultList;


/**
 * Զ�̲�ѯ����б�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3> extends
        RemoteAsyncHandle implements
        MoreKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {

    /**
     * Զ�̽���б��ѯ��
     */
    final RemoteListQuery remoteListQuery;

    /**
     * Զ�̲�ѯ����б�Ĺ�������
     * 
     * @param remoteListQuery
     *            Զ�̽���б��ѯ��
     * @param remoteListQueryStub
     *            Զ�̽���б��ѯ�����
     */
    RemoteOverlappedResultList(RemoteListQuery remoteListQuery,
            RemoteListQueryStubImpl remoteListQueryStub) {
        super(remoteListQueryStub);
        if (remoteListQuery == null) {
            throw new NullArgumentException("remoteListQuery");
        }
        this.remoteListQuery = remoteListQuery;
    }

    /**
     * ��ȡԶ�̽���б��ѯ�������
     * 
     * @return Զ�̽���б��ѯ�������
     */
    private RemoteListQueryStubImpl remoteListQueryStub() {
        return (RemoteListQueryStubImpl) this.remoteRequestStub;
    }

    /**
     * ��ȡ��ѯ����б��еĶ�������͡�
     * 
     * @return ��ѯ����б��еĶ�������͡�
     */
    @SuppressWarnings("unchecked")
    public Class<TResult> getResultClass() {
        return this.remoteListQuery.queryBy.getResultClass();
    }

    /**
     * ��ȡ��ѯƾ���еĵ�һ������
     * 
     * @return ��ѯƾ���еĵ�һ������
     */
    @SuppressWarnings("unchecked")
    public TKey1 getKey1() {
        return (TKey1) this.remoteListQuery.queryBy.getKey1();
    }

    /**
     * ��ȡ��ѯƾ���еĵڶ�������
     * 
     * @return ��ѯƾ���еĵڶ�������
     */
    @SuppressWarnings("unchecked")
    public TKey2 getKey2() {
        return (TKey2) this.remoteListQuery.queryBy.getKey2();
    }

    /**
     * ��ȡ��ѯƾ���еĵ���������
     * 
     * @return ��ѯƾ���еĵ���������
     */
    @SuppressWarnings("unchecked")
    public TKey3 getKey3() {
        return (TKey3) this.remoteListQuery.queryBy.getKey3();
    }

    /**
     * ��ȡ��ѯƾ����ǰ������֮��ļ���
     * 
     * @return ��ѯƾ������������֮��ļ���
     */
    public Object[] getOtherKeys() {
        return this.remoteListQuery.queryBy.getOtherKeys();
    }

    /**
     * ��ȡԶ�̲�ѯִ����Ϻ󷵻صĽ���б�
     * 
     * @return Զ�̲�ѯִ����Ϻ󷵻صĽ���б�
     * @throws IllegalStateException
     *             ִ�й�����δ��ϡ�
     * @throws RemoteException
     *             Զ�̵��ù����г����쳣��
     */
    @SuppressWarnings("unchecked")
    public List<TResult> getResultList() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return this.remoteListQueryStub().getResultList();
    }
}
