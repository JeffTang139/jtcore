/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ThrowableAdapter.java
 * Date 2009-4-2
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;

/**
 * �쳣�������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
public final class ThrowableAdapter extends Throwable {
    private static final long serialVersionUID = -6673649961965477135L;

    /**
     * ʵ�ʵ��쳣����������������֡�
     */
    private final String exceptionClassName;

    /**
     * �쳣�������Ĺ�������
     * 
     * @param exception
     *            ʵ�ʵ��쳣����
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
     * ��ȡʵ�ʵ��쳣�����������֡�
     * 
     * @return ʵ�ʵ��쳣�����������֡�
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
     * ʲô��������ֱ�ӷ��ظ��쳣��������ʵ����
     */
    @Override
    public final ThrowableAdapter fillInStackTrace() {
        return this;
    }

    /**
     * ��֧�ָò�����
     * 
     * @throws UnsupportedOperationException
     *             ���ø÷���ʱ��
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
