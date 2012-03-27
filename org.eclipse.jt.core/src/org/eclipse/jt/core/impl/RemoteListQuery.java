/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteListQuery.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * 远程结果列表查询。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class RemoteListQuery extends RemoteQueryBase<RemoteListQueryStubImpl> {

    private RemoteListQuery(QueryBy queryBy) {
        super(queryBy);
    }

    @Override
    @SuppressWarnings("unchecked")
    abstract List execQuery(ContextImpl<?, ?, ?> context);

    public RemoteListQueryStubImpl newStub(NetConnection netConnection) {
        if (netConnection == null) {
            throw new NullArgumentException("newConnection");
        }
        return new RemoteListQueryStubImpl(netConnection, this);
    }

    static RemoteListQuery buildListQuery(Class<?> resultClass) {
        return new NoKeyRemoteListQuery(resultClass);
    }

    static RemoteListQuery buildListQuery(Class<?> resultClass, Object key) {
        return new OneKeyRemoteListQuery(resultClass, key);
    }

    static RemoteListQuery buildListQuery(Class<?> resultClass, Object key1,
            Object key2) {
        return new TwoKeyRemoteListQuery(resultClass, key1, key2);
    }

    static RemoteListQuery buildListQuery(Class<?> resultClass, Object key1,
            Object key2, Object key3) {
        return new ThreeKeyRemoteListQuery(resultClass, key1, key2, key3);
    }

    static RemoteListQuery buildListQuery(Class<?> resultClass, Object key1,
            Object key2, Object key3, Object... otherKeys) {
        return new MoreKeyRemoteListQuery(resultClass, key1, key2, key3,
                otherKeys);
    }

    // //////////////////////////

    private static final class NoKeyRemoteListQuery extends RemoteListQuery {
        NoKeyRemoteListQuery(Class<?> resultClass) {
            super(new QueryBy(resultClass));
        }

        @SuppressWarnings("unchecked")
        @Override
        final List execQuery(ContextImpl<?, ?, ?> context) {
            return context.getList(this.queryBy.resultClass);
        }
    }

    private static final class OneKeyRemoteListQuery extends RemoteListQuery {
        OneKeyRemoteListQuery(Class<?> resultClass, Object key) {
            super(new OneKeyQueryBy(resultClass, key));
        }

        @SuppressWarnings("unchecked")
        @Override
        final List execQuery(ContextImpl<?, ?, ?> context) {
            return context.getList(this.queryBy.resultClass, this.queryBy
                    .getKey1());
        }
    }

    private static final class TwoKeyRemoteListQuery extends RemoteListQuery {
        TwoKeyRemoteListQuery(Class<?> resultClass, Object key1, Object key2) {
            super(new TwoKeyQueryBy(resultClass, key1, key2));
        }

        @SuppressWarnings("unchecked")
        @Override
        final List execQuery(ContextImpl<?, ?, ?> context) {
            return context.getList(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2());
        }
    }

    private static final class ThreeKeyRemoteListQuery extends RemoteListQuery {
        ThreeKeyRemoteListQuery(Class<?> resultClass, Object key1, Object key2,
                Object key3) {
            super(new ThreeKeyQueryBy(resultClass, key1, key2, key3));
        }

        @SuppressWarnings("unchecked")
        @Override
        final List execQuery(ContextImpl<?, ?, ?> context) {
            return context.getList(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3());
        }
    }

    private static final class MoreKeyRemoteListQuery extends RemoteListQuery {
        MoreKeyRemoteListQuery(Class<?> resultClass, Object key1, Object key2,
                Object key3, Object[] otherKeys) {
            super(new MoreKeyQueryBy(resultClass, key1, key2, key3, otherKeys));
        }

        @SuppressWarnings("unchecked")
        @Override
        final List execQuery(ContextImpl<?, ?, ?> context) {
            return context.getList(this.queryBy.resultClass, this.queryBy
                    .getKey1(), this.queryBy.getKey2(), this.queryBy.getKey3(),
                    this.queryBy.getOtherKeys());
        }
    }
}
