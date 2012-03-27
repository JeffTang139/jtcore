/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ReturnReceivable.java
 * Date 2009-3-17
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ReturnReceivable {
    void setResult(Object result);

    void setRemoteException(ThrowableAdapter exception);
}
