/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferManager.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * �ֽڻ�������������
 * 
 * FIXME �����֡�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ByteBufferManager {
    /**
     * ��ȡһ���ֽڻ������İ�װ����
     * 
     * @return �ֽڻ������İ�װ����
     */
    ByteBufferWrapper getBuffer();
}
