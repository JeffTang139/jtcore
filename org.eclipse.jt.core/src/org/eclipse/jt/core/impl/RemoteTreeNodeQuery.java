/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTreeNodeQuery.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 远程树节点查询。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class RemoteTreeNodeQuery extends
        RemoteQueryBase<RemoteTreeNodeQueryStubImpl> {

    private RemoteTreeNodeQuery(QueryBy queryBy) {
        super(queryBy);
    }

    @Override
    @SuppressWarnings("unchecked")
    abstract TreeNode execQuery(ContextImpl<?, ?, ?> context);

    public RemoteTreeNodeQueryStubImpl newStub(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("newConnection");
        }
        return new RemoteTreeNodeQueryStubImpl(netConnection, this);
    }

    // ///////////////////////////////////

    static RemoteTreeNodeQuery buildTreeNodeQuery(Class<?> resultClass) {
        return new NoKeyRemoteTreeNodeQuery(resultClass);
    }

    static RemoteTreeNodeQuery buildTreeNodeQuery(Class<?> resultClass,
            Object key) {
        return new OneKeyRemoteTreeNodeQuery(resultClass, key);
    }

    static RemoteTreeNodeQuery buildTreeNodeQuery(Class<?> resultClass,
            Object key1, Object key2) {
        return new TwoKeyRemoteTreeNodeQuery(resultClass, key1, key2);
    }

    static RemoteTreeNodeQuery buildTreeNodeQuery(Class<?> resultClass,
            Object key1, Object key2, Object key3) {
        return new ThreeKeyRemoteTreeNodeQuery(resultClass, key1, key2, key3);
    }

    static RemoteTreeNodeQuery buildTreeNodeQuery(Class<?> resultClass,
            Object key1, Object key2, Object key3, Object... otherKeys) {
        return new MoreKeyRemoteTreeNodeQuery(resultClass, key1, key2, key3,
                otherKeys);
    }

    // /////////////////////////

    private static final class NoKeyRemoteTreeNodeQuery extends
            RemoteTreeNodeQuery {
        NoKeyRemoteTreeNodeQuery(Class<?> resultClass) {
            super(new QueryBy(resultClass));
        }

        @SuppressWarnings("unchecked")
        @Override
        final TreeNode execQuery(ContextImpl<?, ?, ?> context) {
            return context.getTreeNode(this.queryBy.resultClass);
        }
    }

    private static final class OneKeyRemoteTreeNodeQuery extends
            RemoteTreeNodeQuery {
        OneKeyRemoteTreeNodeQuery(Class<?> resultClass, Object key) {
            super(new OneKeyQueryBy(resultClass, key));
        }

        @SuppressWarnings("unchecked")
        @Override
        final TreeNode execQuery(ContextImpl<?, ?, ?> context) {
            return context.getTreeNode(this.queryBy.resultClass, this.queryBy
                    .getKey1());
        }
    }

    private static final class TwoKeyRemoteTreeNodeQuery extends
            RemoteTreeNodeQuery {
        TwoKeyRemoteTreeNodeQuery(Class<?> resultClass, Object key1, Object key2) {
            super(new TwoKeyQueryBy(resultClass, key1, key2));
        }

        @SuppressWarnings("unchecked")
        @Override
        final TreeNode execQuery(ContextImpl<?, ?, ?> context) {
            return context.getTreeNode(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2());
        }
    }

    private static final class ThreeKeyRemoteTreeNodeQuery extends
            RemoteTreeNodeQuery {
        ThreeKeyRemoteTreeNodeQuery(Class<?> resultClass, Object key1,
                Object key2, Object key3) {
            super(new ThreeKeyQueryBy(resultClass, key1, key2, key3));
        }

        @SuppressWarnings("unchecked")
        @Override
        final TreeNode execQuery(ContextImpl<?, ?, ?> context) {
            return context.getTreeNode(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3());
        }
    }

    private static final class MoreKeyRemoteTreeNodeQuery extends
            RemoteTreeNodeQuery {
        MoreKeyRemoteTreeNodeQuery(Class<?> resultClass, Object key1,
                Object key2, Object key3, Object[] otherKeys) {
            super(new MoreKeyQueryBy(resultClass, key1, key2, key3, otherKeys));
        }

        @SuppressWarnings("unchecked")
        @Override
        final TreeNode execQuery(ContextImpl<?, ?, ?> context) {
            return context.getTreeNode(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3(),
                    this.queryBy.getOtherKeys());
        }
    }
}
