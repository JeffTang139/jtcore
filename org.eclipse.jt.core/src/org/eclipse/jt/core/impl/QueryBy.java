/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File QueryBy.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ��ѯƾ�ݡ�
 * 
 * �ò�ѯƾ����δ�����κ��йز�ѯ������Ϣ�����ṩ�˻�ȡ��Щ��ѯ����ȱʡ������<br/>
 * ��Щ����ֱ���׳�NoSuchKeyException�������ͨ����д��Щ�����ṩ��ȡ��Ӧ��ֵ��������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class QueryBy {
    /**
     * ��ѯ��������͡�
     */
    @SuppressWarnings("unchecked")
    final Class resultClass;

    /**
     * ��ѯƾ�ݹ�������
     * 
     * @param resultClass
     *            ��ѯ��������͡�
     */
    QueryBy(Class<?> resultClass) {
        if (resultClass == null) {
            throw new NullArgumentException("resultClass");
        }
        this.resultClass = resultClass;
    }

    @Override
    public String toString() {
        return "{" + this.resultClass.getName() + "}";
    }

    /**
     * ��ȡ��ѯ��������͡�
     * 
     * @return ��ѯ��������͡�
     */
    @SuppressWarnings("unchecked")
    final Class getResultClass() {
        return this.resultClass;
    }

    /**
     * ��ȡ��ѯƾ�ݵĵ�һ������<br/>
     * �÷���ֱ���׳�NoSuchKeyException�������ͨ����д�÷����ṩ��ȡ��Ӧ��ֵ��������
     * 
     * @return ��ѯƾ�ݵĵ�һ������
     * @throws NoSuchKeyException
     *             û����Ӧ�ļ���
     */
    Object getKey1() {
        throw new NoSuchKeyException("no key1");
    }

    /**
     * ��ȡ��ѯƾ�ݵĵڶ�������<br/>
     * �÷���ֱ���׳�NoSuchKeyException�������ͨ����д�÷����ṩ��ȡ��Ӧ��ֵ��������
     * 
     * @return ��ѯƾ�ݵĵڶ�������
     * @throws NoSuchKeyException
     *             û����Ӧ�ļ���
     */
    Object getKey2() {
        throw new NoSuchKeyException("no key2");
    }

    /**
     * ��ȡ��ѯƾ�ݵĵ���������<br/>
     * �÷���ֱ���׳�NoSuchKeyException�������ͨ����д�÷����ṩ��ȡ��Ӧ��ֵ��������
     * 
     * @return ��ѯƾ�ݵĵ���������
     * @throws NoSuchKeyException
     *             û����Ӧ�ļ���
     */
    Object getKey3() {
        throw new NoSuchKeyException("no key3");
    }

    /**
     * ��ȡ��ѯƾ��ǰ������֮�����Щ����<br/>
     * �÷���ֱ���׳�NoSuchKeyException�������ͨ����д�÷����ṩ��ȡ��Ӧ��ֵ��������
     * 
     * @return ��ѯƾ��ǰ������֮�����Щ����
     * @throws NoSuchKeyException
     *             û����Ӧ�ļ���
     */
    Object[] getOtherKeys() {
        throw new NoSuchKeyException("no other keys");
    }
}
