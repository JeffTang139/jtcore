/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CycleReferenceException.java
 * Date 2009-1-5
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class CycleReferenceException extends RuntimeException {
    private static final long serialVersionUID = 6132253774261757423L;

    public CycleReferenceException() {
        super();
    }

    public CycleReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleReferenceException(String message) {
        super(message);
    }

    public CycleReferenceException(Throwable cause) {
        super(cause);
    }
}
