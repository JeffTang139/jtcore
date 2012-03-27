/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataSender.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class DataSender extends DataWorker {

    DataSender(NetManager netManager) {
        super(netManager);
    }

    @Override
    final void internalStart() {
        RIUtil.startDaemon(this, "data-sender");
    }

    @Override
    final WorkStatus work(NetConnection netConnection) throws Throwable {
        return netConnection.send();
    }

    @Override
    final void whenNoDataResolved(NetConnection netConnection) {
        netConnection.registerW();
    }
}
