/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQuery.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 远程查询。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class RemoteQuery extends RemoteQueryBase<RemoteQueryStubImpl> {
    /**
     * 远程查询构造器。
     * 
     * @param queryBy
     *            查询凭据。
     */
    RemoteQuery(QueryBy queryBy) {
        super(queryBy);
    }

    public final RemoteQueryStubImpl newStub(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("netConnection");
        }
        return new RemoteQueryStubImpl(netConnection, this);
    }

    // ///////////////////////////////////

    static RemoteQuery buildQuery(Class<?> resultClass) {
        return new NoKeyRemoteQuery(resultClass);
    }

    static RemoteQuery buildQuery(Class<?> resultClass, Object key) {
        return new OneKeyRemoteQuery(resultClass, key);
    }

    static RemoteQuery buildQuery(Class<?> resultClass, Object key1, Object key2) {
        return new TwoKeyRemoteQuery(resultClass, key1, key2);
    }

    static RemoteQuery buildQuery(Class<?> resultClass, Object key1,
            Object key2, Object key3) {
        return new ThreeKeyRemoteQuery(resultClass, key1, key2, key3);
    }

    static RemoteQuery buildQuery(Class<?> resultClass, Object key1,
            Object key2, Object key3, Object... otherKeys) {
        return new MoreKeyRemoteQuery(resultClass, key1, key2, key3, otherKeys);
    }

    // ////////////////////////////////

    private static final class NoKeyRemoteQuery extends RemoteQuery {
        NoKeyRemoteQuery(Class<?> resultClass) {
            super(new QueryBy(resultClass));
        }

        @SuppressWarnings("unchecked")
        @Override
        final Object execQuery(ContextImpl<?, ?, ?> context) {
            return context.find(this.queryBy.resultClass);
        }
    }

    private static final class OneKeyRemoteQuery extends RemoteQuery {
        OneKeyRemoteQuery(Class<?> resultClass, Object key) {
            super(new OneKeyQueryBy(resultClass, key));
        }

        @SuppressWarnings("unchecked")
        @Override
        final Object execQuery(ContextImpl<?, ?, ?> context) {
            return context.find(this.queryBy.resultClass, this.queryBy
                    .getKey1());
        }
    }

    private static final class TwoKeyRemoteQuery extends RemoteQuery {
        TwoKeyRemoteQuery(Class<?> resultClass, Object key1, Object key2) {
            super(new TwoKeyQueryBy(resultClass, key1, key2));
        }

        @SuppressWarnings("unchecked")
        @Override
        final Object execQuery(ContextImpl<?, ?, ?> context) {
            return context.find(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2());
        }
    }

    private static final class ThreeKeyRemoteQuery extends RemoteQuery {
        ThreeKeyRemoteQuery(Class<?> resultClass, Object key1, Object key2,
                Object key3) {
            super(new ThreeKeyQueryBy(resultClass, key1, key2, key3));
        }

        @SuppressWarnings("unchecked")
        @Override
        final Object execQuery(ContextImpl<?, ?, ?> context) {
            return context.find(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3());
        }
    }

    private static final class MoreKeyRemoteQuery extends RemoteQuery {
        MoreKeyRemoteQuery(Class<?> resultClass, Object key1, Object key2,
                Object key3, Object[] otherKeys) {
            super(new MoreKeyQueryBy(resultClass, key1, key2, key3, otherKeys));
        }

        @SuppressWarnings("unchecked")
        @Override
        final Object execQuery(ContextImpl<?, ?, ?> context) {
            return context.find(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3(),
                    this.queryBy.getOtherKeys());
        }
    }
}
