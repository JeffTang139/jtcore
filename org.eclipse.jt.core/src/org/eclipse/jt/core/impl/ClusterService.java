/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ClusterService.java
 * Date May 14, 2009
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ClusterService {
    final ApplicationImpl application;

    ClusterService(ApplicationImpl application) {
        if (application == null) {
            throw new NullArgumentException("application");
        }
        this.application = application;
    }

    // TODO ��Ҫ�л�ȡ��Դ���������
    //

    void putAndCommitResource() {

    }

    void putResource() {

    }

    void updateResource() {

    }

    void removeResource() {

    }
}
