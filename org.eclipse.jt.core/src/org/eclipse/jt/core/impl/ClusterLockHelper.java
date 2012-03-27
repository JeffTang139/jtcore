/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterLockHelper.java
 * Date 2009-5-18
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterLockHelper {
    static LockerInfo acquireExclusiveLock(int clusterIndex,
            ResourceGroupAcquirerHolder<RemoteResGroupHandle> holder,
            ResourceGroup<?, ?, ?> group) {
        RemoteResGroupHandle handle = new RemoteResGroupHandle(clusterIndex,
                holder);
        LockerInfo li = handle.acquireExclusiveLock(group);
        if (li.clusterIndex == clusterIndex) {
            holder.putAcquirer(handle);
        }
        return li;
    }
}
