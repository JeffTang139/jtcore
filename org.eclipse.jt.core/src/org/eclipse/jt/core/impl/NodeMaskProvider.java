/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File NodeMaskProvider.java
 * Date 2009-6-25
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface NodeMaskProvider {

    int getGlobalMask();

    int getLocalMask();
}
