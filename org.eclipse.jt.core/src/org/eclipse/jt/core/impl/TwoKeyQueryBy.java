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
 * 双键查询凭据。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class TwoKeyQueryBy extends OneKeyQueryBy {

    /**
     * 查询凭据中的第二个键。
     */
    final Object key2;

    /**
     * 双键查询凭据的构造器。
     * 
     * @param resultClass
     *            查询结果的类型。
     * @param key1
     *            查询凭据中的第一个键。
     * @param key2
     *            查询凭据中的第二个键。
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
     * 获取查询凭据中的第二个键。
     * 
     * @return 查询凭据中的第二个键。
     */
    @Override
    final Object getKey2() {
        return this.key2;
    }
}
