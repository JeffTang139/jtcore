/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

/**
 * ������ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface MoreKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3>
        extends ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3> {
    /**
     * ��ȡ��ѯ������ǰ������֮�����Щ����
     * 
     * @return ��ѯ������ǰ������֮�����Щ����
     */
    Object[] getOtherKeys();
}
