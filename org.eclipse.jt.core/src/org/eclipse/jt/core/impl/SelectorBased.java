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
 * 基于选择器来工作的对象的抽象基类。<br/>
 * 除默认提供一个同生命周期的选择器之外，还负责维护一个等待队列。<br/>
 * 这个队列可以暂时存储等待注册到选择器的连接。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class SelectorBased extends NetManagerBased implements
        Runnable {
    final Selector selector;
    final int interestOp;

    private volatile boolean started = false;

    // XXX 存储待定
    private final LinkedList<NetConnection> pending = new LinkedList<NetConnection>();

    /**
     * 构造对象。<br/>
     * 本操作会自动在内部开启一个选择器，与本对象具有相同的生命周期。
     * 
     * @param connectionManager
     *            连接管理器。
     * @throws NullArgumentException
     *             指定的连接管理器为空。
     * @throws CannotOpenSelectorException
     *             无法开启选择器。
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
     * 把指定连接加入到等待注册到选择器的队列中。
     * 
     * @param connection
     *            连接对象。
     * @throws NullArgumentException
     *             指定的连接对象为空。
     */
    final void queue(final NetConnection connection) {
        if (connection == null) {
            throw new NullArgumentException("connection");
        }
        synchronized (this.pending) {
            if (!this.pending.contains(connection)) // XXX 这个判断的效率不高
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
     * 把处于等待队列中的连接按指定的操作代码注册到选择器中。
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
                    throw new IllegalStateException("连接通道意外空指针");
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

        System.err.format("%1$tY-%1$tm-%1$td %1$tT FATAL - 通道状态监护线程中止：%2$s%n",
                new Date(), this.exception);
    }
}
