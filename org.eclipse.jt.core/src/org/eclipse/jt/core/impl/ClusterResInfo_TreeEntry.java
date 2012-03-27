/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceTreeEntry_Info.java
 * Date May 6, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterResInfo_TreeEntry extends AbstractClusterResInfo {
    final long parentItemId;

    // REMIND? 必须得可以序列化
    final Object treeId;

    ClusterResInfo_TreeEntry(Object categoryOrId, Class<?> facadeClass,
            Object treeId, long parentItemId, long childItemId, Action action) {
        super(categoryOrId, facadeClass, childItemId);
        if (treeId == null) {
            treeId = None.NONE;
        }
        if (action == null) {
            throw new NullArgumentException("action");
        }
        this.treeId = treeId;
        this.parentItemId = parentItemId;
        this.action = action;
    }

    final Action action;

    static enum Action {
        INIT, ADD, MOVE, DELETE
    }

    @Override
    void exec(ContextImpl<?, ?, ?> context) throws Throwable {
        // TODO Auto-generated method stub
    }
}
