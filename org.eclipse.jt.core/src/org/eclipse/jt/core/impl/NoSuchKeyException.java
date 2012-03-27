/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NoSuchKeyException.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

/**
 * 缺失查询键的异常。<br/>
 * 当没有某个查询键，但又要使用它的时候，抛出该异常。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class NoSuchKeyException extends RuntimeException {
    private static final long serialVersionUID = -7833530876165748447L;

    /**
     * @param message
     *            异常信息，一般要说明缺失的是哪一个键。
     */
    public NoSuchKeyException(String message) {
        super(message);
    }
}
