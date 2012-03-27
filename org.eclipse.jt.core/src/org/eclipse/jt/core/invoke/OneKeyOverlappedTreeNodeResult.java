/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * 单键的树节点查询结果。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface OneKeyOverlappedTreeNodeResult<TResult, TKey> extends
        AsyncTreeNodeResult<TResult> {
    /**
     * 获取查询条件中的（第一个）键。
     * 
     * @return 查询条件中的（第一个）键。
     */
    TKey getKey1();
}
