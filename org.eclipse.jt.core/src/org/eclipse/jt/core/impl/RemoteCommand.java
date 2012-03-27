/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteCommand.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * 远程命令。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteCommand {
    /**
     * 获取远程命令的数据包的代码。
     * 
     * @return 远程命令的数据包的代码。
     */
    PacketCode getPacketCode();

    void writeTo(StructuredObjectSerializer serializer) throws IOException,
            StructDefineNotFoundException;
}
