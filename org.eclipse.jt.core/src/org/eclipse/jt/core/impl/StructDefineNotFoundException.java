/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructDefineNotFoundException.java
 * Date 2008-12-1
 */
package org.eclipse.jt.core.impl;

/**
 * �Ҳ����ṹ������쳣��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class StructDefineNotFoundException extends Exception {
    private static final long serialVersionUID = -6245137099257691859L;

    /**
     * @param structDefineName
     *            �ṹ��������֡�
     */
    public StructDefineNotFoundException(String structDefineName) {
        super("�Ҳ����ṹ���壺" + structDefineName);
    }

    /**
     * @param cnfException
     *            �Ҳ������쳣��
     */
    public StructDefineNotFoundException(ClassNotFoundException cnfException) {
        super(cnfException);
    }

    /**
     * @param structDefineName
     *            �ṹ��������֡�
     * @param cause
     *            ԭ���쳣��
     */
    public StructDefineNotFoundException(String structDefineName,
            Throwable cause) {
        super("�Ҳ����ṹ���壺" + structDefineName, cause);
    }
}
