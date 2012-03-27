package org.eclipse.jt.core.impl;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

import sun.misc.Unsafe;

public final class Unsf {
	public static final Unsafe unsafe;
	public static final boolean jvm_ibm;
	/**
	 * 数组起始数据的偏移量
	 */
	public final static int byte_array_base_offset;

	private Unsf() {
	}

	static {
		Unsafe us;
		int babo;
		try {
			final Field f = Unsafe.class.getDeclaredField("theUnsafe");
			java.security.AccessController
					.doPrivileged(new PrivilegedAction<Object>() {
						public Object run() {
							f.setAccessible(true);
							return null;
						}
					});
			us = (Unsafe) f.get(null);
			babo = us.arrayBaseOffset(byte[].class);
		} catch (Throwable e) {
			us = null;
			babo = 0;
		}
		unsafe = us;
		byte_array_base_offset = babo;
		final String vmVendor = System.getProperty("java.vm.vendor");
		jvm_ibm = vmVendor != null && vmVendor.startsWith("IBM");
	}
}
