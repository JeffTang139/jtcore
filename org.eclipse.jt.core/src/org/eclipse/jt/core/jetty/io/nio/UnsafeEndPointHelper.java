/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File UnsafeEndPointHelper.java
 * Date 2009-11-9
 */
package org.eclipse.jt.core.jetty.io.nio;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.PrivilegedAction;

import org.eclipse.jetty.http.ssl.SslSelectChannelEndPoint;
import org.eclipse.jetty.io.nio.ChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jt.core.impl.ConsoleLog;

import sun.misc.Unsafe;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class UnsafeEndPointHelper {
    private UnsafeEndPointHelper() {
    }

    static final ByteChannel closedByteChannel = new ByteChannel() {
        public int read(ByteBuffer dst) throws IOException {
            throw new ClosedChannelException();
        }

        public void close() throws IOException {
        }

        public boolean isOpen() {
            return false;
        }

        public int write(ByteBuffer src) throws IOException {
            throw new ClosedChannelException();
        }
    };

    static final void resetFields(DnaSelectChannelEndPoint endpoint) {
        if (endpoint != null) {
            if (os_channel > 0) {
                // change super's SocketChannel into a 'closed' ByteChannel.
                Unsf.unsafe.putObject(endpoint, os_channel, closedByteChannel);
            } else {
                throw new UnsupportedOperationException();
            }
            if (UnsafeEndPointHelper.os_interestOps > 0) {
                // change to interest nothing.
                Unsf.unsafe.putInt(endpoint, os_interestOps, 0);
            }

            endpoint.canBeClosed = true;
        }
    }

    static final void resetFields(DnaSSLSelectChannelEndPoint endpoint) {
        if (endpoint != null) {
            if (os_channel > 0 && os_engine > 0) {
                // change super's SocketChannel into a 'closed' ByteChannel.
                Unsf.unsafe.putObject(endpoint, os_channel, closedByteChannel);
                // erase super's SSLEngine field.
                Unsf.unsafe.putObject(endpoint, os_engine, null);
            } else {
                throw new UnsupportedOperationException();
            }
            if (UnsafeEndPointHelper.os_interestOps > 0) {
                // change to interest nothing.
                Unsf.unsafe.putInt(endpoint, os_interestOps, 0);
            }

            endpoint.canBeClosed = true;
        }
    }

    static final void closedSuperEndPoint(SelectChannelEndPoint endpoint,
            SocketChannel socketChannelFromEndpoint) {
        if (endpoint != null) {
            // unregister the SocketChannel from Jetty.
            Selector selector = null;
            if (os_selector > 0 && socketChannelFromEndpoint != null) {
                SelectSet set = endpoint.getSelectSet();
                selector = (Selector) Unsf.unsafe.getObject(set, os_selector);
                if (selector != null) {
                    SelectionKey key = socketChannelFromEndpoint
                            .keyFor(selector);
                    if (key != null && key.isValid()) {
                        key.cancel();
                        selector.wakeup();
                    }
                }
            } else if (os_key > 0) {
                SelectionKey key = (SelectionKey) Unsf.unsafe.getObject(
                        endpoint, os_key);
                if (key != null && key.isValid()) {
                    selector = key.selector();
                    key.cancel();
                    selector.wakeup();
                }
            } else {
                throw new UnsupportedOperationException();
            }

            // XXX unnecessary ?
            // try {
            // if (selector != null) {
            // selector.selectNow(); // too slowly in linux in uncertain
            // circumstance.
            // }
            // } catch (IOException ignore) {
            // }

            // close the endpoint.
            try {
                endpoint.close();
            } catch (Exception ignore) {
                if (ignore instanceof NullPointerException) {
                    ConsoleLog.debugError(
                            "Close Jetty's SSLEndPoint: [%s] is OK!", ignore);
                } else {
                    ignore.printStackTrace();
                }
            }
        }
    }

    private static final long os_engine = Unsf.tryGetFieldOffset(
            SslSelectChannelEndPoint.class, "_engine");

    private static final long os_interestOps = Unsf.tryGetFieldOffset(
            SelectChannelEndPoint.class, "_interestOps");
    private static final long os_key = Unsf.tryGetFieldOffset(
            SelectChannelEndPoint.class, "_key");

    private static final long os_channel = Unsf.tryGetFieldOffset(
            ChannelEndPoint.class, "_channel");

    private static final long os_selector = Unsf.tryGetFieldOffset(
            SelectorManager.SelectSet.class, "_selector");

    private static final class Unsf {
        public static final Unsafe unsafe;

        private Unsf() {
        }

        static {
            Unsafe us;
            try {
                final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                java.security.AccessController
                        .doPrivileged(new PrivilegedAction<Object>() {
                            public Object run() {
                                f.setAccessible(true);
                                return null;
                            }
                        });
                us = (Unsafe) f.get(null);
            } catch (Throwable e) {
                us = null;
            }
            unsafe = us;
        }

        static long tryGetFieldOffset(Class<?> clazz, String fieldName) {
            if (Unsf.unsafe != null) {
                try {
                    return Unsf.unsafe.objectFieldOffset(clazz
                            .getDeclaredField(fieldName));
                } catch (Throwable e) {
                    return 0;
                }
            }
            return 0;
        }
    }
}
