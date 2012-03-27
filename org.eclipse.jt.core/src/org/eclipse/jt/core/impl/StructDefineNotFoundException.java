/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructDefineNotFoundException.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

/**
 * 找不到结构定义的异常。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class StructDefineNotFoundException extends Exception {
    private static final long serialVersionUID = -6245137099257691859L;

    /**
     * @param structDefineName
     *            结构定义的名字。
     */
    public StructDefineNotFoundException(String structDefineName) {
        super("找不到结构定义：" + structDefineName);
    }

    /**
     * @param cnfException
     *            找不到类异常。
     */
    public StructDefineNotFoundException(ClassNotFoundException cnfException) {
        super(cnfException);
    }

    /**
     * @param structDefineName
     *            结构定义的名字。
     * @param cause
     *            原因异常。
     */
    public StructDefineNotFoundException(String structDefineName,
            Throwable cause) {
        super("找不到结构定义：" + structDefineName, cause);
    }
}
