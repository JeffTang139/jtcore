/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * 双键树节点查询结果。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> extends
        OneKeyOverlappedTreeNodeResult<TResult, TKey1> {
    /**
     * 获取查询条件中的第二个键。
     * 
     * @return 查询条件中的第二个键。
     */
    TKey2 getKey2();
}
