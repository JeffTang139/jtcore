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
 * 查询凭据。
 * 
 * 该查询凭据中未定义任何有关查询键的信息，但提供了获取这些查询键的缺省方法。<br/>
 * 这些方法直接抛出NoSuchKeyException。子类可通过重写这些方法提供获取相应键值的能力。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
class QueryBy {
    /**
     * 查询结果的类型。
     */
    @SuppressWarnings("unchecked")
    final Class resultClass;

    /**
     * 查询凭据构造器。
     * 
     * @param resultClass
     *            查询结果的类型。
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
     * 获取查询结果的类型。
     * 
     * @return 查询结果的类型。
     */
    @SuppressWarnings("unchecked")
    final Class getResultClass() {
        return this.resultClass;
    }

    /**
     * 获取查询凭据的第一个键。<br/>
     * 该方法直接抛出NoSuchKeyException。子类可通过重写该方法提供获取相应键值的能力。
     * 
     * @return 查询凭据的第一个键。
     * @throws NoSuchKeyException
     *             没有相应的键。
     */
    Object getKey1() {
        throw new NoSuchKeyException("no key1");
    }

    /**
     * 获取查询凭据的第二个键。<br/>
     * 该方法直接抛出NoSuchKeyException。子类可通过重写该方法提供获取相应键值的能力。
     * 
     * @return 查询凭据的第二个键。
     * @throws NoSuchKeyException
     *             没有相应的键。
     */
    Object getKey2() {
        throw new NoSuchKeyException("no key2");
    }

    /**
     * 获取查询凭据的第三个键。<br/>
     * 该方法直接抛出NoSuchKeyException。子类可通过重写该方法提供获取相应键值的能力。
     * 
     * @return 查询凭据的第三个键。
     * @throws NoSuchKeyException
     *             没有相应的键。
     */
    Object getKey3() {
        throw new NoSuchKeyException("no key3");
    }

    /**
     * 获取查询凭据前三个键之后的那些键。<br/>
     * 该方法直接抛出NoSuchKeyException。子类可通过重写该方法提供获取相应键值的能力。
     * 
     * @return 查询凭据前三个键之后的那些键。
     * @throws NoSuchKeyException
     *             没有相应的键。
     */
    Object[] getOtherKeys() {
        throw new NoSuchKeyException("no other keys");
    }
}
