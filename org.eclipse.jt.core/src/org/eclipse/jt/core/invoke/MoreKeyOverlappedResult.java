/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedResult.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.invoke;


/**
 * �����ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
        ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
    /**
     * ��ȡ��ѯ������ǰ������֮�����Щ����
     * 
     * @return ��ѯ������ǰ������֮�����Щ����
     */
    Object[] getOtherKeys();
}
