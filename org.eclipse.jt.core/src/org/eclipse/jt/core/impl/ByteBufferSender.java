/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferSender.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * FIXME 改名字。
 * 
 * 基于字节缓冲区的发送器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ByteBufferSender {
    /**
     * 发送给定的字节缓冲区包装对象中的数据。
     * 
     * @param src
     *            字节缓冲区包装对象（其中封装需要发送的数据）。
     */
    void toSend(ByteBufferWrapper src);
}
