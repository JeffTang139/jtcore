/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File UtilHelper.java
 * Date 2009-5-31
 */
package org.eclipse.jt.core.impl;


/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public final class UtilHelper {
	public static final int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	public static final int hash(long l) {
		return hash((int) (l ^ (l >>> 32)));
	}

	public static final int hash(final Object o) {
		if (o == null) {
			return 0;
		}
		return hash(o.hashCode());
	}

	public static final int indexForHash(final int hash, final int length) {
		return hash & (length - 1);
	}

	public static final int indexForIntKey(final int key, final int length) {
		return hash(key) & (length - 1);
	}

	public static final int indexForLongKey(final long key, final int length) {
		return hash(key) & (length - 1);
	}

	public static final int indexForObjectKey(final Object key, final int length) {
		return hash(key) & (length - 1);
	}

}
