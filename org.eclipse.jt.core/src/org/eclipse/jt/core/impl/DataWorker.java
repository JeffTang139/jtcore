/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DataWorker.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class DataWorker extends NetManagerBased implements Runnable {

    private final LinkedList<NetConnection> pending = new LinkedList<NetConnection>();

    private volatile Thread runner;

    private volatile boolean started = false;

    DataWorker(NetManager netManager) {
        super(netManager);
    }

    final boolean isRunning() {
        return (this.runner != null);
    }

    /**
     * �����Ӽ���ȴ����ݴ���Ķ����С� ��ʱ�������е�ͨ���Ѿ��򿪲����ڿ��õ�״̬��
     * �˷��������Ӽ������һ���ȴ��������ݵĶ��У������ܱ�֤���ϾͿ��Զ�д���ݡ�
     */
    final void queue(NetConnection netConnection) {
        boolean needRunning = false;
        synchronized (this.pending) {
            /*
             * ����жϵ�Ч�ʱȽϵ͡� ��һ������¶����е����Ӷ��󲻻��кܶ࣬������ʱ������ô��һ�á�
             */
            if (!this.pending.contains(netConnection)) {
                this.pending.add(netConnection);
                this.pending.notifyAll();
                needRunning = true;
            }
        }
        if (needRunning) {
            this.ensureRunning();
        }
    }

    private void ensureRunning() {
        if (!this.started) {
            synchronized (this) {
                if (!this.started) {
                    this.internalStart();
                    this.started = true;
                }
            }
        }
    }

    abstract void internalStart();

    /**
     * ��ָ��������ִ��ʵ�ʵ����ݲ�������д�ȣ���
     * 
     * @return �������ͨ�Ĺ���״̬��
     */
    abstract WorkStatus work(NetConnection netConnection) throws Throwable;

    abstract void whenNoDataResolved(NetConnection netConnection);

    public final void run() {
        this.runner = Thread.currentThread();
        final Set<NetConnection> queued = new HashSet<NetConnection>(); // û�б�֤˳��
        Iterator<NetConnection> i;
        NetConnection c;
        RUN: while (true) {
            /*
             * ����ȴ����У��Ѵ��ڿɲ���״̬�����Ӽ��빤�����С�
             */
            synchronized (this.pending) {
                while (this.pending.isEmpty() && queued.isEmpty()) {
                    try {
                        this.pending.wait();
                    } catch (InterruptedException e) {
                        break RUN;
                    }
                }
                while (this.pending.size() > 0) {
                    queued.add(this.pending.removeFirst());
                }
            }

            /*
             * �����������У�ʹÿ����ִ�в��������Ӷ��л��ᴦ�����ݲ�������
             */
            for (i = queued.iterator(); i.hasNext();) {
                c = i.next();
                try {
                    if (this.work(c) == WorkStatus.NO_DATA) {
                        i.remove();
                        this.whenNoDataResolved(c);
                    }
                } catch (Throwable e) {
                    ConsoleLog.debugError(
                            "Exception from send()/receive() : %s", e);
                    i.remove();
                    c.broken(e);
                }
            }
        }
        this.runner = null;
    }

    static enum WorkStatus {
        OK, NO_DATA;
    }
}
