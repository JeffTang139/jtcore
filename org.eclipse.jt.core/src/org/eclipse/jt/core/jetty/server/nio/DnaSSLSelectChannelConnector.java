/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DnaSslSelectChannelConnector.java
 * Date 2009-11-3
 */
package org.eclipse.jt.core.jetty.server.nio;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.PrivilegedAction;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.ThreadLocalBuffers;
import org.eclipse.jetty.io.nio.DirectNIOBuffer;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager.SelectSet;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.thread.Timeout.Task;
import org.eclipse.jt.core.jetty.DetachedSocketChannelWithSSLHandler;
import org.eclipse.jt.core.jetty.io.nio.DnaSSLSelectChannelEndPoint;
import org.eclipse.jt.core.jetty.server.DnaSSLHttpConnection;

import sun.misc.Unsafe;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DnaSSLSelectChannelConnector extends SslSelectChannelConnector {
    private Buffers sslBuffers;

    final DetachedSocketChannelWithSSLHandler dscHandler;

    public DnaSSLSelectChannelConnector(
            DetachedSocketChannelWithSSLHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.dscHandler = handler;
    }

    @Override
    protected Connection newConnection(SocketChannel channel,
            final SelectChannelEndPoint endpoint) {
        DnaSSLHttpConnection httpConnection = new DnaSSLHttpConnection(
                DnaSSLSelectChannelConnector.this, endpoint, this.getServer(),
                this.dscHandler) {
            @Override
            public void cancelTimeout(Task task) {
                endpoint.getSelectSet().cancelTimeout(task);
            }

            @Override
            public void scheduleTimeout(Task task, long timeoutMs) {
                endpoint.getSelectSet().scheduleTimeout(task, timeoutMs);
            }
        };
        ((HttpParser) httpConnection.getParser()).setForceContentBuffer(true);
        return httpConnection;
    }

    @Override
    protected SelectChannelEndPoint newEndPoint(SocketChannel channel,
            SelectSet selectSet, SelectionKey key) throws IOException {
        return new DnaSSLSelectChannelEndPoint(this.sslBuffers, channel,
                selectSet, key, this.createSSLEngine());
    }

    @Override
    protected void connectionClosed(HttpConnection connection) {
        super.connectionClosed(connection);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (os_sslBuffers > 0) {
            this.sslBuffers = (Buffers) Unsf.unsafe.getObject(this,
                    os_sslBuffers);
        }

        if (this.sslBuffers == null) {
            SSLEngine engine = this.createSSLEngine();
            SSLSession ssl_session = engine.getSession();

            ThreadLocalBuffers buffers = new ThreadLocalBuffers() {
                @Override
                protected Buffer newBuffer(int size) {
                    // JettyTODO indirect?
                    return new DirectNIOBuffer(size);
                }

                @Override
                protected Buffer newHeader(int size) {
                    // JettyTODO indirect?
                    return new DirectNIOBuffer(size);
                }

                @Override
                protected boolean isHeader(Buffer buffer) {
                    return true;
                }
            };
            buffers.setBufferSize(ssl_session.getApplicationBufferSize());
            buffers.setHeaderSize(ssl_session.getApplicationBufferSize());
            this.sslBuffers = buffers;
        }
    }

    static final long os_sslBuffers = Unsf.tryGetFieldOffset(
            SslSelectChannelConnector.class, "_sslBuffers");

    static final class Unsf {
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
