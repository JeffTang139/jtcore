/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IllegalDeclarationException.java
 * Date 2008-10-28
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class IllegalDeclarationException extends RuntimeException {
    private static final long serialVersionUID = 7604830180392368337L;

    /**
     * 
     */
    public IllegalDeclarationException() {
    }

    /**
     * @param message
     */
    public IllegalDeclarationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public IllegalDeclarationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public IllegalDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }
}
