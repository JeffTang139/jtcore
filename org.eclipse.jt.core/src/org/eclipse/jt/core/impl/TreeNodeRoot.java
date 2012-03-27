/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TreeNodeRoot.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

/**
 * 只作为树的根结点使用，且只多了该节点在逻辑上整棵树中的绝对级次信息。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO 在构建树的代码中，修改根节点所使用的类。
public final class TreeNodeRoot<TData> extends TreeNodeImpl<TData> {

    private int absoluteLevel;

    TreeNodeRoot(TData data, int absoluteLevel) {
        super(null, data);
    }

    final int getAbsoluteLevel() {
        return this.absoluteLevel;
    }
}
