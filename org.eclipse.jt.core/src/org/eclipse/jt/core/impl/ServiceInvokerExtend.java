/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ServiceInvokerExtend.java
 * Date 2009-4-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.AsyncTreeNodeResult;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedResult;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.service.ServiceInvoker;

/**
 * FIXME 把所有方法整合进ServiceInvoker中。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface ServiceInvokerExtend extends ServiceInvoker {
    <TResult, TKey1, TKey2, TKey3> MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
            Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3,
            Object... otherKeys);

    <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
            Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3,
            Object... otherKeys);

    <TFacade> AsyncTreeNodeResult<TFacade> asyncGetTreeNode(
            Class<TFacade> facadeClass);

    <TFacade, TKey> OneKeyOverlappedTreeNodeResult<TFacade, TKey> asyncGetTreeNode(
            Class<TFacade> facadeClass, TKey key);

    <TFacade, TKey1, TKey2> TwoKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2> asyncGetTreeNode(
            Class<TFacade> facadeClass, TKey1 key1, TKey2 key2);

    <TFacade, TKey1, TKey2, TKey3> ThreeKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
            Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3);

    <TFacade, TKey1, TKey2, TKey3> MoreKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
            Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3,
            Object... otherKeys);
}
