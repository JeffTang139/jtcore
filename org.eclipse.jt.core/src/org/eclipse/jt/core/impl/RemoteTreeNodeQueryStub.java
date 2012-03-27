/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTreeNodeQueryStub.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.TreeNode;

/**
 * 远程树节点查询存根。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface RemoteTreeNodeQueryStub extends RemoteRequestStub {
    /**
     * 获取远程查询结束后返回的树节点。
     * 
     * @return 远程查询结束后返回的树节点。
     */
    @SuppressWarnings("unchecked")
    TreeNode getTreeNode();
}
