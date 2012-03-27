/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AsyncEvent.java
 * Date 2009-4-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.Event;

/**
 * 异步事件的句柄。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface AsyncEvent extends AsyncHandle {
    /**
     * 获取事件对象。
     * 
     * @return 事件对象。
     */
    Event getEvent();

    /**
     * 是否需要等待所有的处理过程都完成。
     */
    boolean needWait();
}
