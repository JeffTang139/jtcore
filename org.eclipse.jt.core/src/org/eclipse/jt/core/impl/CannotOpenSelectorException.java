/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CannotOpenSelectorException.java
 * Date 2009-3-4
 */
package org.eclipse.jt.core.impl;


/**
 * �޷�����ѡ�����쳣��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class CannotOpenSelectorException extends RuntimeException {
    private static final long serialVersionUID = 4777739665938863455L;

    /**
     * @param cause
     *            ����ѡ����ʱ���ֵ��쳣��
     */
    public CannotOpenSelectorException(Throwable cause) {
        super("�޷�����ѡ����", cause);
    }
}
