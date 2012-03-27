/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * 三键树节点查询结果。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3>
        extends TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
    /**
     * 获取查询条件中的第三个键。
     * 
     * @return 查询条件中的第三个键。
     */
    TKey3 getKey3();
}
