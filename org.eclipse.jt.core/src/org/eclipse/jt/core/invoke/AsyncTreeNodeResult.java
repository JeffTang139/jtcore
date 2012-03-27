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
 * �첽�����ڵ��ѯ�����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface AsyncTreeNodeResult<TFacade> extends AsyncHandle {

    /**
     * ��ȡִ�������ȡ�õĽ�����ý����һ�����ڵ㣬�����������νṹ�ĸ��ڵ㡣
     * 
     * @return ���ڵ����
     * @throws IllegalStateException
     *             ��������δ���أ����׳����쳣��
     */
    TreeNode<TFacade> getTreeNode() throws IllegalStateException;

    /**
     * ��ȡ���������Ӧ���ݵ�������͡�
     * 
     * @return �������ʵ����
     */
    Class<TFacade> getFacadeClass();
}
