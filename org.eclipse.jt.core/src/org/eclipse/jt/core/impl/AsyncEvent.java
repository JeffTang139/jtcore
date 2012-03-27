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
 * �첽�¼��ľ����
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface AsyncEvent extends AsyncHandle {
    /**
     * ��ȡ�¼�����
     * 
     * @return �¼�����
     */
    Event getEvent();

    /**
     * �Ƿ���Ҫ�ȴ����еĴ�����̶���ɡ�
     */
    boolean needWait();
}
