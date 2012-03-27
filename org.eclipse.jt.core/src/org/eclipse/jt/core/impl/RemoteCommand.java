/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteCommand.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

/**
 * Զ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteCommand {
    /**
     * ��ȡԶ����������ݰ��Ĵ��롣
     * 
     * @return Զ����������ݰ��Ĵ��롣
     */
    PacketCode getPacketCode();

    void writeTo(StructuredObjectSerializer serializer) throws IOException,
            StructDefineNotFoundException;
}
