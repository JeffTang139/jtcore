/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataReceiver.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

import java.nio.ByteBuffer;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class DataReceiver extends DataWorker {

    DataReceiver(NetManager netManager) {
        super(netManager);
    }

    private final ByteBuffer buf = ByteBuffer
            .allocateDirect(ByteBufferPool.DEFAULT_BUF_SIZE);

    @Override
    final void internalStart() {
        RIUtil.startDaemon(this, "data-receiver");
    }

    @Override
    final WorkStatus work(NetConnection netConnection) throws Throwable {
        this.buf.clear();
        return netConnection.receive(this.buf);
    }

    @Override
    final void whenNoDataResolved(NetConnection netConnection) {
        netConnection.registerR();
    }
}
