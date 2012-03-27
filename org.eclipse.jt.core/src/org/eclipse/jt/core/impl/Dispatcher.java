/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File Dispatcher.java
 * Date 2009-3-9
 */
package org.eclipse.jt.core.impl;

import java.util.LinkedList;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class Dispatcher extends NetManagerBased implements Runnable {

    private final LinkedList<DataPacket> received = new LinkedList<DataPacket>();

    private volatile boolean started = false;

    Dispatcher(NetManager netManager) {
        super(netManager);
    }

    final void dispatch(DataPacket src) {
        synchronized (this.received) {
            this.received.add(src);
            this.received.notifyAll();
        }
        if (!this.started) {
            RIUtil.startDaemon(this, "data-dispatcher");
            this.started = true;
        }
    }

    public final void run() {
        LinkedList<DataPacket> working = new LinkedList<DataPacket>();
        while (true) {
            synchronized (this.received) {
                while (this.received.isEmpty() && working.isEmpty()) {
                    try {
                        this.received.wait();
                    } catch (InterruptedException e) {
                        // XXX ���߳������ӹ���������ͬ���������ڣ���Ӧ�����˳���
                        // �޸��̵߳�������ֹͣ������ģʽ�����������Ծ����׽���ˡ�
                        e.printStackTrace();
                    }
                }
                working.addAll(this.received);
                this.received.clear();
            }

            while (working.size() > 0) {
                working.removeFirst().work();
            }
        }
    }
}
