/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File StructObject.java
 * Date 2008-6-30
 */
package org.eclipse.jt.core.impl;

import java.util.Arrays;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class DynObj {
	/**
	 * �ṹ����
	 */
	StructDefineImpl define;
	/**
	 * �����ƴ洢buf
	 */
	byte[] bin;
	/**
	 * ���ö���洢
	 */
	Object[] objs;
	/**
	 * ���,ǰ16λΪ��ֵ���
	 */
	int masks;
	/**
	 * null���ʹ�õ�λ��
	 */
	static final int null_mask_bits = 16;
	static final int MULL_MASK_MASK = 0xFFFF;

	/**
	 * ��̬����̬�����Ƿ����
	 */
	final boolean dynDataEq(DynObj dyn, boolean deepEq) {
		if (this.define == dyn.define
				&& (this.masks & MULL_MASK_MASK) == (dyn.masks & MULL_MASK_MASK)) {
			byte[] bin = this.bin;
			byte[] bin2 = dyn.bin;
			Object[] objs = this.objs;
			Object[] objs2 = this.objs;
			return (bin == bin2 || Arrays.equals(bin, bin2))
					&& (objs == objs2 || deepEq ? Arrays
							.deepEquals(objs, objs2) : Arrays.equals(objs,
							objs2));
		}
		return false;
	}

	/**
	 * ��̬����̬����hashCode
	 */
	final int dynDataHashCode(boolean deepHash) {
		return (((this.define.hashCode() * 31 + this.masks & MULL_MASK_MASK) * 31) + (deepHash ? Arrays
				.deepHashCode(this.objs)
				: Arrays.hashCode(this.objs)))
				* 31 + Arrays.hashCode(this.bin);
	}

	// ��¼���
	static final int r_new = 0;
	static final int r_new_modified = 1 << 29;
	static final int r_db = 2 << 29;
	static final int r_db_deleting = 3 << 29;
	static final int r_db_modifing = 4 << 29;
	static final int r_setl_reverse = 5 << 29;
	static final int r_mask = 7 << 29;

	final int getRecordState() {
		return this.masks & r_mask;
	}

	final void setRecordState(int state) {
		this.masks = (this.masks & ~r_mask) | (state & r_mask);
	}
}
