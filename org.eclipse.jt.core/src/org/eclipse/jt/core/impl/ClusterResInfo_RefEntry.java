/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ResRefEntry_Info.java
 * Date May 6, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterResInfo_RefEntry extends AbstractClusterResInfo {
    final long holderItemId;
    final Class<?> holderFacadeClass;
    // REMIND? 必须得可以序列化
    final Object holderCategoryId;

    ClusterResInfo_RefEntry(Object refResCategoryOrId,
            Class<?> refResFacadeClass, long refResItemId,
            Object holderResCategoryOrId, Class<?> holderResFacadeClass,
            long holderResItemId, Action action) {
        super(refResCategoryOrId, refResFacadeClass, refResItemId);
        if (holderResCategoryOrId == null) {
            throw new NullArgumentException("holderResCategoryOrId");
        }
        if (holderResFacadeClass == null) {
            throw new NullArgumentException("holderResFacadeClass");
        }
        if (action == null) {
            throw new NullArgumentException("action");
        }
        this.holderCategoryId = holderResCategoryOrId;
        this.holderFacadeClass = holderResFacadeClass;
        this.holderItemId = holderResItemId;
        this.action = action;
    }

    final Action action;

    static enum Action {
        ADD, DELETE
    }

    @Override
    void exec(ContextImpl<?, ?, ?> context) throws Throwable {
        // TODO Auto-generated method stub
    }
}
