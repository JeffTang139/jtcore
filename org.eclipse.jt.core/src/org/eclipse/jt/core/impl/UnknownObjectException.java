/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File UnknownObjectException.java
 * Date 2009-4-14
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class UnknownObjectException extends RuntimeException {
    private static final long serialVersionUID = -8268452639501643928L;

    UnknownObjectException(String message) {
        super(message);
    }
}
