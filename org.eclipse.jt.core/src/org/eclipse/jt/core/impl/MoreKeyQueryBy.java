/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyQueryBy.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * �����ѯƾ�ݡ�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class MoreKeyQueryBy extends ThreeKeyQueryBy {

    /**
     * ��ѯƾ����ǰ������֮�����Щ����
     */
    private final Object[] keys;

    /**
     * �����ѯƾ�ݹ�������
     * 
     * @param resultClass
     *            ��ѯ��������͡�
     * @param key1
     *            ��һ����ѯ����
     * @param key2
     *            �ڶ�����ѯ����
     * @param key3
     *            ��������ѯ����
     * @param otherKeys
     *            ǰ����֮�����Щ��ѯ����
     */
    MoreKeyQueryBy(Class<?> resultClass, Object key1, Object key2, Object key3,
            Object[] otherKeys) {
        super(resultClass, key1, key2, key3);
        if (otherKeys == null || otherKeys.length == 0) {
            throw new NullArgumentException("otherKeys");
        }
        this.keys = otherKeys;
    }

    @Override
    public String toString() {
        return "{" + this.resultClass.getName() + "(" + this.key + ","
                + this.key2 + "," + this.key3 + ", ...)}";
    }

    /**
     * ��ȡ��ѯƾ����ǰ������֮�����Щ����
     * 
     * @return ��ѯƾ����ǰ������֮�����Щ����
     */
    @Override
    final Object[] getOtherKeys() {
        return this.keys.clone();
    }
}
