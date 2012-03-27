/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedTreeNodeResult;

/**
 * 远程树节点查询结果。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3>
        extends RemoteAsyncHandle implements
        MoreKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> {

    /**
     * 远程树节点查询。
     */
    final RemoteTreeNodeQuery remoteTreeNodeQuery;

    /**
     * 远程树节点查询结果的构造器。
     * 
     * @param remoteTreeNodeQuery
     *            远程树节点查询。
     * @param remoteTreeNodeQueryStub
     *            远程树节点查询存根。
     */
    RemoteOverlappedTreeNodeResult(RemoteTreeNodeQuery remoteTreeNodeQuery,
            RemoteTreeNodeQueryStubImpl remoteTreeNodeQueryStub) {
        super(remoteTreeNodeQueryStub);
        if (remoteTreeNodeQuery == null) {
            throw new NullArgumentException("remoteTreeNodeQuery");
        }
        this.remoteTreeNodeQuery = remoteTreeNodeQuery;
    }

    /**
     * 获取远程树节点查询存根。
     * 
     * @return 远程树节点查询存根。
     */
    private RemoteTreeNodeQueryStubImpl remoteTreeNodeQueryStub() {
        return (RemoteTreeNodeQueryStubImpl) this.remoteRequestStub;
    }

    /**
     * 获取查询凭据中的第一个键。
     * 
     * @return 查询凭据中的第一个键。
     */
    @SuppressWarnings("unchecked")
    public TKey1 getKey1() {
        return (TKey1) this.remoteTreeNodeQuery.queryBy.getKey1();
    }

    /**
     * 获取查询凭据中的第二个键。
     * 
     * @return 查询凭据中的第二个键。
     */
    @SuppressWarnings("unchecked")
    public TKey2 getKey2() {
        return (TKey2) this.remoteTreeNodeQuery.queryBy.getKey2();
    }

    /**
     * 获取查询凭据中的第三个键。
     * 
     * @return 查询凭据中的第三个键。
     */
    @SuppressWarnings("unchecked")
    public TKey3 getKey3() {
        return (TKey3) this.remoteTreeNodeQuery.queryBy.getKey3();
    }

    /**
     * 获取查询凭据中前三个键之后的键。
     * 
     * @return 查询凭据中前三个键之后的键。
     */
    public Object[] getOtherKeys() {
        return this.remoteTreeNodeQuery.queryBy.getOtherKeys();
    }

    /**
     * 获取树节点中的数据的类型。
     * 
     * @return 树节点中的数据的类型。
     */
    @SuppressWarnings("unchecked")
    public Class<TFacade> getFacadeClass() {
        return this.remoteTreeNodeQuery.queryBy.getResultClass();
    }

    /**
     * 获取远程查询结束后返回的树节点结果。
     * 
     * @return 远程查询结束后返回的树节点结果。
     * @throws IllegalStateException
     *             执行过程尚未完毕。
     * @throws RemoteException
     *             远程调用过程中出现异常。
     */
    @SuppressWarnings("unchecked")
    public TreeNode<TFacade> getTreeNode() throws IllegalStateException {
        this.internalCheckStateForResultOK();
        return this.remoteTreeNodeQueryStub().getTreeNode();
    }
}
