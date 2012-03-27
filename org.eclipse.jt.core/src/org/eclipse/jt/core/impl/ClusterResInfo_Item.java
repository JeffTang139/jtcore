/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResourceItem_Info.java
 * Date May 6, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
final class ClusterResInfo_Item extends AbstractClusterResInfo {
    Object resourceKeys;
    Object resourceImpl;

    ClusterResInfo_Item(Object categoryOrId, Class<?> facadeClass,
            long resourceItemId, ResourceItem.State state, Action action) {
        super(categoryOrId, facadeClass, resourceItemId);
        if (state == null) {
            throw new NullArgumentException("state");
        }
        if (action == null) {
            throw new NullArgumentException("action");
        }
        this.state = state;
        this.action = action;
    }

    final ResourceItem.State state;

    final Action action;

    static enum Action {
        INIT, COMMIT, ROLLBACK, PUT;
    }

    @Override
    void exec(ContextImpl<?, ?, ?> context) throws Throwable {
        // TODO Auto-generated method stub
    }
}
