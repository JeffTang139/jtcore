/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CannotBuildConnectionException.java
 * Date 2009-4-14
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class CannotBuildConnectionException extends RuntimeException {
    private static final long serialVersionUID = -1840610215641733547L;

    public CannotBuildConnectionException() {
        super("无法建立连接");
    }

    public CannotBuildConnectionException(Throwable cause) {
        super("无法建立连接", cause);
    }

}
