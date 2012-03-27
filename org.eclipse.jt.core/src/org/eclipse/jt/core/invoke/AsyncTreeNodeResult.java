/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AsyncTreeNodeResult.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.invoke;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.invoke.AsyncHandle;

/**
 * 异步的树节点查询结果。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface AsyncTreeNodeResult<TFacade> extends AsyncHandle {

    /**
     * 获取执行完后所取得的结果，该结果是一个树节点，且是所得树形结构的根节点。
     * 
     * @return 树节点对象。
     * @throws IllegalStateException
     *             如果结果还未返回，则抛出该异常。
     */
    TreeNode<TFacade> getTreeNode() throws IllegalStateException;

    /**
     * 获取树结点中相应数据的外观类型。
     * 
     * @return 外观类型实例。
     */
    Class<TFacade> getFacadeClass();
}
