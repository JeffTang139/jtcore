/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterInvoker.java
 * Date May 14, 2009
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterInvoker {
    void acquireExclusiveLock() {
        // 1. lock self
        // 2. lock other nodes
        // 3. if 2 ok then return else wait
    }

    void releaseLock() {

    }

}
