/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File DetachedSocketChannelWithSSLHandler.java
 * Date 2009-11-9
 */
package org.eclipse.jt.core.jetty;

import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

/**
 * A handler for the detached <code>java.nio.channels.SocketChannel</code> and
 * <code>javax.net.ssl.SSLEngine</code> from Jetty.
 * 
 * The handler will receive a <code>SocketChannel</code> object and a
 * <code>SSLEngine</code> which were detached from Jetty, and do other works on
 * them.
 * 
 * NOTE:<br/>
 * The <code>SocketChannel</code> and <code>SSLEngine</code> objects had
 * completed the handshake process that the TLS/SSL requires.
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface DetachedSocketChannelWithSSLHandler {
    /**
     * Receive a <code>SocketChannel</code> object and a <code>SSLEngine</code>
     * object detached from Jetty, which had completed the handshake process
     * that the TLS/SSL requires.
     * 
     * @param socketChannel
     *            the channel in blocking mode detached from Jetty and not
     *            <code>null</code>.
     * @param sslEngine
     *            the <code>SSLEngine</code> object detached from Jetty and not
     *            <code>null</code>.
     */
    void handleDetackedSocketChannel(SocketChannel socketChannel,
            SSLEngine sslEngine);
}
