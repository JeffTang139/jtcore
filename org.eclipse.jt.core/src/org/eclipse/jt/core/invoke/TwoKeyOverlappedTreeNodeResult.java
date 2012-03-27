/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * ˫�����ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> extends
        OneKeyOverlappedTreeNodeResult<TResult, TKey1> {
    /**
     * ��ȡ��ѯ�����еĵڶ�������
     * 
     * @return ��ѯ�����еĵڶ�������
     */
    TKey2 getKey2();
}
