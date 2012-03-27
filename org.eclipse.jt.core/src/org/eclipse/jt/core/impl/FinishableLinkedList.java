/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CloseableLinkedList.java
 * Date 2009-3-12
 */
package org.eclipse.jt.core.impl;

import java.util.LinkedList;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class FinishableLinkedList<E> extends LinkedList<E> {
    private static final long serialVersionUID = 4113017375578510620L;

    private volatile boolean finished = false;

    final void finish() {
        this.finished = true;
    }

    final boolean finished() {
        return this.finished;
    }
}
