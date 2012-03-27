/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ThrowableAdapter.java
 * Date 2009-4-2
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;

/**
 * 异常适配对象。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
public final class ThrowableAdapter extends Throwable {
    private static final long serialVersionUID = -6673649961965477135L;

    /**
     * 实际的异常对象所属的类的名字。
     */
    private final String exceptionClassName;

    /**
     * 异常适配对象的构造器。
     * 
     * @param exception
     *            实际的异常对象。
     */
    ThrowableAdapter(Throwable exception) {
        super(exception.getMessage());
        this.exceptionClassName = exception.getClass().getName();
        super.setStackTrace(exception.getStackTrace());
        Throwable c = exception.getCause();
        if (c == null || c == exception) {
            super.initCause(null);
        } else {
            super.initCause(new ThrowableAdapter(c));
        }
    }

    /**
     * 获取实际的异常对象的类的名字。
     * 
     * @return 实际的异常对象的类的名字。
     */
    public final String getExceptionCalssName() {
        return this.exceptionClassName;
    }

    /**
     * @see java.lang.Throwable.toString()
     */
    @Override
    public final String toString() {
        String message = this.getLocalizedMessage();
        return (message != null) ? (this.exceptionClassName + ": " + message)
                : this.exceptionClassName;
    }

    /**
     * @see java.lang.Throwable.getCause()
     */
    @Override
    public ThrowableAdapter getCause() {
        return (ThrowableAdapter) super.getCause();
    }

    /**
     * 什么都不做，直接返回该异常适配对象的实例。
     */
    @Override
    public final ThrowableAdapter fillInStackTrace() {
        return this;
    }

    /**
     * 不支持该操作。
     * 
     * @throws UnsupportedOperationException
     *             调用该方法时。
     */
    @Override
    public final Throwable initCause(Throwable cause) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.lang.Throwable.setStackTrace(StackTraceElement[] stackTrace)
     */
    @Override
    public final void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
    }
}
