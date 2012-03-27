/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * �������ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3>
        extends TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
    /**
     * ��ȡ��ѯ�����еĵ���������
     * 
     * @return ��ѯ�����еĵ���������
     */
    TKey3 getKey3();
}
