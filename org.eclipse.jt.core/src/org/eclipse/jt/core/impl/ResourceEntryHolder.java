/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File ResourceEntryHolder.java
 * Date 2008-9-1
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface ResourceEntryHolder {
    /**
     * ɾ��ָ����Entry���ύ
     */
    void removeEntryCommitly(ResourceEntry<?, ?, ?> entry);
}
