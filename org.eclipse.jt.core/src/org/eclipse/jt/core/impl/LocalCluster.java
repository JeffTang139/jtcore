/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File LocalCluster.java
 * Date 2009-6-10
 */
package org.eclipse.jt.core.impl;

import java.util.List;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class LocalCluster {
    private final Cluster local;

    LocalCluster(Cluster localCluster) {
        this.local = localCluster;
    }

    void broadcast(ClusterResInfo_Item resItemInfo) {
        if (resItemInfo != null) {
            this.local.postRequest(resItemInfo);
        }
    }

    void broadcast(ClusterResInfo_TreeEntry resTreeEntryInfo) {
        if (resTreeEntryInfo != null) {
            this.local.postRequest(resTreeEntryInfo);
        }
    }

    void broadcast(ClusterResInfo_RefEntry resRefEntryInfo) {
        if (resRefEntryInfo != null) {
            this.local.postRequest(resRefEntryInfo);
        }
    }

    void broadcastTreeEntryInfos(
            List<ClusterResInfo_TreeEntry> resTreeEntryInfos) {
        // XXX 整体发送？
        if (resTreeEntryInfos != null) {
            final int size = resTreeEntryInfos.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.local.postRequest(resTreeEntryInfos.get(i));
                }
            }
        }
    }

    void broadcastRefEntryInfos(List<ClusterResInfo_RefEntry> resRefEntryInfos) {
        // XXX 整体发送？
        if (resRefEntryInfos != null) {
            final int size = resRefEntryInfos.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.local.postRequest(resRefEntryInfos.get(i));
                }
            }
        }
    }
}
