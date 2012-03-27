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
     * 把连接加入等待数据处理的队列中。 这时，连接中的通道已经打开并处于可用的状态。
     * 此方法把连接加入的是一个等待处理数据的队列，并不能保证马上就可以读写数据。
     */
    final void queue(NetConnection netConnection) {
        boolean needRunning = false;
        synchronized (this.pending) {
            /*
             * 这个判断的效率比较低。 但一般情况下队列中的连接队象不会有很多，所以暂时可以这么用一用。
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
     * 让指定的连接执行实际的数据操作（读写等）。
     * 
     * @return 如果连接通的工作状态。
     */
    abstract WorkStatus work(NetConnection netConnection) throws Throwable;

    abstract void whenNoDataResolved(NetConnection netConnection);

    public final void run() {
        this.runner = Thread.currentThread();
        final Set<NetConnection> queued = new HashSet<NetConnection>(); // 没有保证顺序
        Iterator<NetConnection> i;
        NetConnection c;
        RUN: while (true) {
            /*
             * 处理等待队列，把处于可操作状态的连接加入工作队列。
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
             * 遍历工作队列，使每个可执行操作的连接都有机会处理数据操作任务。
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
