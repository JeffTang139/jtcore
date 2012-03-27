/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File LockerInfo.java
 * Date 2009-5-18
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class LockerInfo {
    final int clusterIndex;

    LockerInfo(int clusterIndex) {
        this.clusterIndex = clusterIndex;
    }
}
