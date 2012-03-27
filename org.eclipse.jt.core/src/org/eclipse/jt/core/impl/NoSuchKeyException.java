/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NoSuchKeyException.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.impl;

/**
 * ȱʧ��ѯ�����쳣��<br/>
 * ��û��ĳ����ѯ��������Ҫʹ������ʱ���׳����쳣��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class NoSuchKeyException extends RuntimeException {
    private static final long serialVersionUID = -7833530876165748447L;

    /**
     * @param message
     *            �쳣��Ϣ��һ��Ҫ˵��ȱʧ������һ������
     */
    public NoSuchKeyException(String message) {
        super(message);
    }
}
