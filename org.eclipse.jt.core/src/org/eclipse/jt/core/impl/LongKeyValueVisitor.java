/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ValueVisitor.java
 * Date 2009-5-25
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
interface LongKeyValueVisitor<TValue> {
	void visit(long key, TValue value);
}
