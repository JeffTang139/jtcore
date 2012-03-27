/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyQueryBy.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ˫����ѯƾ�ݡ�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class TwoKeyQueryBy extends OneKeyQueryBy {

    /**
     * ��ѯƾ���еĵڶ�������
     */
    final Object key2;

    /**
     * ˫����ѯƾ�ݵĹ�������
     * 
     * @param resultClass
     *            ��ѯ��������͡�
     * @param key1
     *            ��ѯƾ���еĵ�һ������
     * @param key2
     *            ��ѯƾ���еĵڶ�������
     */
    TwoKeyQueryBy(Class<?> resultClass, Object key1, Object key2) {
        super(resultClass, key1);
        if (key2 == null) {
            throw new NullArgumentException("key2");
        }
        this.key2 = key2;
    }

    @Override
    public String toString() {
        return "{" + this.resultClass.getName() + "(" + this.key + ","
                + this.key2 + ")}";
    }

    /**
     * ��ȡ��ѯƾ���еĵڶ�������
     * 
     * @return ��ѯƾ���еĵڶ�������
     */
    @Override
    final Object getKey2() {
        return this.key2;
    }
}
