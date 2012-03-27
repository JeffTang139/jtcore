/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File SelectorBased.java
 * Date 2009-2-26
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.NetConnection.State;


/**
 * ����ѡ�����������Ķ���ĳ�����ࡣ<br/>
 * ��Ĭ���ṩһ��ͬ�������ڵ�ѡ����֮�⣬������ά��һ���ȴ����С�<br/>
 * ������п�����ʱ�洢�ȴ�ע�ᵽѡ���������ӡ�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class SelectorBased extends NetManagerBased implements
        Runnable {
    final Selector selector;
    final int interestOp;

    private volatile boolean started = false;

    // XXX �洢����
    private final LinkedList<NetConnection> pending = new LinkedList<NetConnection>();

    /**
     * �������<br/>
     * ���������Զ����ڲ�����һ��ѡ�������뱾���������ͬ���������ڡ�
     * 
     * @param connectionManager
     *            ���ӹ�������
     * @throws NullArgumentException
     *             ָ�������ӹ�����Ϊ�ա�
     * @throws CannotOpenSelectorException
     *             �޷�����ѡ������
     */
    SelectorBased(NetManager connectionManager, int interestOp) {
        super(connectionManager);
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new CannotOpenSelectorException(e);
        }
        this.interestOp = interestOp;
    }

    /**
     * ��ָ�����Ӽ��뵽�ȴ�ע�ᵽѡ�����Ķ����С�
     * 
     * @param connection
     *            ���Ӷ���
     * @throws NullArgumentException
     *             ָ�������Ӷ���Ϊ�ա�
     */
    final void queue(final NetConnection connection) {
        if (connection == null) {
            throw new NullArgumentException("connection");
        }
        synchronized (this.pending) {
            if (!this.pending.contains(connection)) // XXX ����жϵ�Ч�ʲ���
            {
                this.pending.add(connection);
                this.pending.notify();
            }
        }
        this.ensureRunning();
        this.selector.wakeup();
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

    final void unregister(NetConnection connection) {
        SocketChannel channel = connection.channel;
        if (channel != null) {
            SelectionKey key = channel.keyFor(this.selector);
            if (key != null) {
                synchronized (this.pending) {
                    this.pending.remove(connection);
                    key.cancel();
                }
            }
        }
    }

    /**
     * �Ѵ��ڵȴ������е����Ӱ�ָ���Ĳ�������ע�ᵽѡ�����С�
     * 
     * @param selOp
     * @throws IOException
     */
    final void processPending() throws IOException {
        synchronized (this.pending) {
            NetConnection c;
            SocketChannel channel;
            SelectionKey key;
            while (this.pending.size() > 0) {
                c = this.pending.removeFirst();
                channel = c.channel;
                if (channel != null) {
                    try {
                        key = channel.keyFor(this.selector);
                        if (key != null) {
                            key.interestOps(this.interestOp);
                        } else {
                            channel.register(this.selector, this.interestOp, c);
                        }
                    } catch (ClosedChannelException e) {
                        c.broken(e);
                    }
                } else if (c.state() == State.READY) {
                    throw new IllegalStateException("����ͨ�������ָ��");
                }
            }
        }
    }

    final void blockIfEmptyPending() throws InterruptedException {
        synchronized (this.pending) {
            while (this.pending.isEmpty()) {
                this.pending.wait();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////

    abstract void wakeupToWork(NetConnection netConnection);

    // /////////////////////////////////////////////////////////////////////////

    private volatile Throwable exception;

    final Throwable getException() {
        return this.exception;
    }

    private volatile Thread runner;

    final void stop() {
        synchronized (this) {
            if (this.started) {
                if (this.runner != null) {
                    try {
                        this.runner.interrupt();
                    } catch (Throwable ignore) {
                    }
                    this.runner = null;
                }
                this.started = false;
            }
        }
    }

    public final void run() {
        this.runner = Thread.currentThread();
        int selectedCount;
        while (true) {
            selectedCount = 0;
            try {
                selectedCount = this.selector.select();
            } catch (ClosedSelectorException e) {
                this.exception = e;
                this.runner = null;
                break;
            } catch (IOException e) {
                this.exception = e;
                this.runner = null;
                break;
            }

            if (selectedCount > 0) {
                Set<SelectionKey> selected = this.selector.selectedKeys();
                SelectionKey key;
                NetConnection c;
                for (Iterator<SelectionKey> i = selected.iterator(); i
                        .hasNext();) {
                    key = i.next();
                    i.remove();
                    if (key.isValid()) {
                        key.interestOps(0);
                        c = (NetConnection) key.attachment();
                        if (c.state() == State.READY) {
                            this.wakeupToWork(c);
                        } else {
                            key.cancel();
                            c.broken(new IllegalStateException(c.state()
                                    .toString()));
                        }
                    }
                }
            }

            try {
                this.processPending();
            } catch (IOException e) {
                this.exception = e;
                this.runner = null;
                break;
            }
        }

        System.err.format("%1$tY-%1$tm-%1$td %1$tT FATAL - ͨ��״̬�໤�߳���ֹ��%2$s%n",
                new Date(), this.exception);
    }
}
