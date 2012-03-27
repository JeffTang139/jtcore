/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterConfig.java
 * Date Nov 19, 2009
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterConfig {
    final NetNodeInfo master;
    final NetNodeInfo[] slaves;
    final int slavesBitmap;
    volatile int wholeMask;

    volatile Cluster cluster;
    private volatile int connectedBitmap;
    private volatile int successfulBitmap;

    ClusterConfig(NetNodeInfo master) {
        this.master = master;
        this.slaves = null;
        this.slavesBitmap = 0;
        this.wholeMask = 0;
    }

    ClusterConfig(NetNodeInfo[] slaves) {
        this.master = null;
        this.slaves = slaves;
        int len = slaves.length;
        int bitmap = 0x00000000; // 从后向前，第三位开始。
        for (int i = 0; i < len; i++) {
            slaves[i].index = i + 2;
            bitmap |= (1 << slaves[i].index);
        }
        this.slavesBitmap = bitmap;
        this.wholeMask = bitmap | (1 << 1);
    }

    boolean isThisMaster() {
        return this.master == null;
    }

    boolean isStarted() {
        return (this.successfulBitmap == this.slavesBitmap);
    }

    void connected(NetNodeInfo info) {
        // REMIND? 检查info是否已经连接。
        int index = this.indexOf(info);
        if (index < 0) {
            throw new IllegalArgumentException("非已配置的集群节点信息");
        }
        this.connectedBitmap |= (1 << info.index);
        ConsoleLog.init("Cluster-Slave[No.%s of %s] connected.", index + 1,
                this.slaves.length);
    }

    void broken(NetNodeInfo info) {
        int index = this.indexOf(info);
        if (index < 0) {
            throw new IllegalArgumentException("非已配置的集群节点信息");
        }
        this.connectedBitmap &= (~(1 << info.index));
        ConsoleLog.init("Cluster-Slave[No.%s of %s] aborted.", index + 1,
                this.slaves.length);
    }

    void succeeded(NetNodeInfo info) {
        // REMIND? 检查info是否已经连接。
        int index = this.indexOf(info);
        if (index < 0) {
            throw new IllegalArgumentException("非已配置的集群节点信息");
        }
        this.successfulBitmap |= (1 << info.index);
        if (this.successfulBitmap == this.slavesBitmap) {
            synchronized (this) {
                this.notifyAll();
            }
        }
        ConsoleLog.init("Cluster-Slave[No.%s of %s] succeeded.", index + 1,
                this.slaves.length);
    }

    NetNodeInfo[] otherSlaves(NetNodeInfo exclusive) {
        int index = this.indexOf(exclusive);
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        NetNodeInfo[] otherSlaves = new NetNodeInfo[this.slaves.length - 1];
        System.arraycopy(this.slaves, 0, otherSlaves, 0, index);
        System.arraycopy(this.slaves, index + 1, otherSlaves, index,
                this.slaves.length - index - 1);
        return otherSlaves;
    }

    private int indexOf(NetNodeInfo info) {
        if (info == null || this.slaves == null) {
            return -1;
        }
        for (int i = 0, len = this.slaves.length; i < len; i++) {
            if (info == this.slaves[i]) {
                return i;
            }
        }
        return -1;
    }
}
