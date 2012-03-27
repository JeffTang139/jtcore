/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyQueryBy.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 三键查询凭据。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class ThreeKeyQueryBy extends TwoKeyQueryBy {

    /**
     * 查询凭据中的第三个键。
     */
    final Object key3;

    /**
     * 三键查询凭据的构造器。
     * 
     * @param resultClass
     *            查询结果的类型。
     * @param key1
     *            查询凭据的第一个键。
     * @param key2
     *            查询凭据的第二个键。
     * @param key3
     *            查询凭据的第三个键。
     */
    ThreeKeyQueryBy(Class<?> resultClass, Object key1, Object key2, Object key3) {
        super(resultClass, key1, key2);
        if (key3 == null) {
            throw new NullArgumentException("key3");
        }
        this.key3 = key3;
    }

    @Override
    public String toString() {
        return "{" + this.resultClass.getName() + "(" + this.key + ","
                + this.key2 + "," + this.key3 + ")}";
    }

    /**
     * 获取查询凭据中的第三个键。
     * 
     * @return 查询凭据中的第三个键。
     */
    @Override
    final Object getKey3() {
        return this.key3;
    }
}
