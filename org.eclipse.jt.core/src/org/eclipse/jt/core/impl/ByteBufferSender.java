/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferSender.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * FIXME �����֡�
 * 
 * �����ֽڻ������ķ�������
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ByteBufferSender {
    /**
     * ���͸������ֽڻ�������װ�����е����ݡ�
     * 
     * @param src
     *            �ֽڻ�������װ�������з�װ��Ҫ���͵����ݣ���
     */
    void toSend(ByteBufferWrapper src);
}
