/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File TreeNodeRoot.java
 * Date 2009-4-9
 */
package org.eclipse.jt.core.impl;

/**
 * ֻ��Ϊ���ĸ����ʹ�ã���ֻ���˸ýڵ����߼����������еľ��Լ�����Ϣ��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
// TODO �ڹ������Ĵ����У��޸ĸ��ڵ���ʹ�õ��ࡣ
public final class TreeNodeRoot<TData> extends TreeNodeImpl<TData> {

    private int absoluteLevel;

    TreeNodeRoot(TData data, int absoluteLevel) {
        super(null, data);
    }

    final int getAbsoluteLevel() {
        return this.absoluteLevel;
    }
}
