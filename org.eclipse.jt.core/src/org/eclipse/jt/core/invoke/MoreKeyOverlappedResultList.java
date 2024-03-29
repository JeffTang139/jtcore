/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedResultList.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.invoke;


/**
 * 多键查询结果列表。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface MoreKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3>
        extends ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {
    /**
     * 获取查询条件中前三个键之后的那些键。
     * 
     * @return 查询条件中前三个键之后的那些键。
     */
    Object[] getOtherKeys();
}
