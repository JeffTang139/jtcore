/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RIUtil.java
 * Date 2009-3-24
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RIUtil {
    private RIUtil() {
    }

    static final int DEFAULT_PORT = 9897;

    static final RemoteCommand parse(final NetConnection connection,
            final FinishableLinkedList<DataPacket> dataSrc) throws Throwable {
        DataPacketDeserializer in = new DataPacketDeserializer(dataSrc,
                connection.remoteEndian);
        SODeserializer sod = new SODeserializer(connection
                .getStructAdapterSet(), in, connection.structDefineProvider);
        Object obj = sod.deserialize();
        if (obj instanceof RemoteCommand) {
            return (RemoteCommand) obj;
        } else {
            throw new UnknownObjectException("无法识别的数据：[" + obj.getClass() + "]"
                    + obj);
        }
    }

    static final void send(final int requestId, final NetConnection connection,
            final RemoteCommand command) throws Throwable {
        ByteBufferSerializer bbs = new ByteBufferSerializer(connection,
                connection.netManager, command.getPacketCode(), requestId);
        try {
            StructuredObjectSerializer sos = new SOSerializer(bbs);
            command.writeTo(sos);
            sos.close();
        } catch (Throwable e) {
            bbs.dispose();
            throw e;
        }
    }

    // XXX 整改远程调用的线程模式
    static final Thread startDaemon(Runnable runnable) {
        return startDaemon(runnable, "runable");
    }

    // XXX 整改远程调用的线程模式
    static final Thread startDaemon(Runnable runnable, String threadName) {
        if (threadName == null) {
            return startDaemon(runnable);
        } else {
            Thread t = new Thread(runnable, "d&a-ri-" + threadName);
            t.setDaemon(true);
            t.start();
            return t;
        }
    }
}
