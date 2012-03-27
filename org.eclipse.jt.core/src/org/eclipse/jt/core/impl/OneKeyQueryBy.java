/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyQueryBy.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * ������ѯƾ�ݡ�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class OneKeyQueryBy extends QueryBy {
    /**
     * ��ѯƾ���еģ���һ��������
     */
    final Object key;

    /**
     * ������ѯƾ�ݹ�������
     * 
     * @param resultClass
     *            ��ѯ��������͡�
     * @param key
     *            ��ѯƾ���еģ���һ������ֵ��
     */
    OneKeyQueryBy(Class<?> resultClass, Object key) {
        super(resultClass);
        if (key == null) {
            throw new NullArgumentException("key");
        }
        this.key = key;
    }

    @Override
    public String toString() {
        return "{" + this.resultClass.getName() + "(" + this.key + ")}";
    }

    /**
     * ��ȡ��ѯƾ���еģ���һ��������
     * 
     * @return ��ѯƾ���еģ���һ��������
     */
    @Override
    final Object getKey1() {
        return this.key;
    }
}
