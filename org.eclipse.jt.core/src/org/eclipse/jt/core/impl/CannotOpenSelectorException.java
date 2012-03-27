/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CannotOpenSelectorException.java
 * Date 2009-3-4
 */
package org.eclipse.jt.core.impl;


/**
 * 无法开启选择器异常。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class CannotOpenSelectorException extends RuntimeException {
    private static final long serialVersionUID = 4777739665938863455L;

    /**
     * @param cause
     *            开启选择器时出现的异常。
     */
    public CannotOpenSelectorException(Throwable cause) {
        super("无法开启选择器", cause);
    }
}
