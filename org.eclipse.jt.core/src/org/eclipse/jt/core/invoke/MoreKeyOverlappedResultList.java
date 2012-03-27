/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedResultList.java
 * Date 2009-4-8
 */
package org.eclipse.jt.core.invoke;


/**
 * �����ѯ����б�
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface MoreKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3>
        extends ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {
    /**
     * ��ȡ��ѯ������ǰ������֮�����Щ����
     * 
     * @return ��ѯ������ǰ������֮�����Щ����
     */
    Object[] getOtherKeys();
}
