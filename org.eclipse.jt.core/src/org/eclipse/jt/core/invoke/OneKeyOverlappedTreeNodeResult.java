/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * ���������ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface OneKeyOverlappedTreeNodeResult<TResult, TKey> extends
        AsyncTreeNodeResult<TResult> {
    /**
     * ��ȡ��ѯ�����еģ���һ��������
     * 
     * @return ��ѯ�����еģ���һ��������
     */
    TKey getKey1();
}
