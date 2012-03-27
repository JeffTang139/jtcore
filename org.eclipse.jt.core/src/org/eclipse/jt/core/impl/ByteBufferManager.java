/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferManager.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * 字节缓冲区管理器。
 * 
 * FIXME 改名字。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ByteBufferManager {
    /**
     * 获取一个字节缓冲区的包装对象。
     * 
     * @return 字节缓冲区的包装对象。
     */
    ByteBufferWrapper getBuffer();
}
