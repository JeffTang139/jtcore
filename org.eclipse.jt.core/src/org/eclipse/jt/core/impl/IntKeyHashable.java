/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IntIdentifiable.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface IntKeyHashable<T extends IntKeyHashable<T>> extends IntIdentifiable,
        Linkable<T> {
}
