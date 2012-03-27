/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTreeNodeQueryStub.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.TreeNode;

/**
 * Զ�����ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteTreeNodeQueryStub extends RemoteRequestStub {
    /**
     * ��ȡԶ�̲�ѯ�����󷵻ص����ڵ㡣
     * 
     * @return Զ�̲�ѯ�����󷵻ص����ڵ㡣
     */
    @SuppressWarnings("unchecked")
    TreeNode getTreeNode();
}
