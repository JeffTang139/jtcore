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
 * 多键查询凭据。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class MoreKeyQueryBy extends ThreeKeyQueryBy {

    /**
     * 查询凭据中前三个键之后的那些键。
     */
    private final Object[] keys;

    /**
     * 多键查询凭据构造器。
     * 
     * @param resultClass
     *            查询结果的类型。
     * @param key1
     *            第一个查询键。
     * @param key2
     *            第二个查询键。
     * @param key3
     *            第三个查询键。
     * @param otherKeys
     *            前三个之后的那些查询键。
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
     * 获取查询凭据中前三个键之后的那些键。
     * 
     * @return 查询凭据中前三个键之后的那些键。
     */
    @Override
    final Object[] getOtherKeys() {
        return this.keys.clone();
    }
}
