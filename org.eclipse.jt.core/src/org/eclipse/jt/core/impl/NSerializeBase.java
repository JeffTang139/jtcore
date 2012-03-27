package org.eclipse.jt.core.impl;

import sun.misc.Unsafe;

/**
 * ���л������������ӿ�
 * 
 * <pre>
 *     �ýӿ���ֻ���������л����ͷ����л����Ĺ������Ժ�Э���Գ�����
 * �������ڳ����л��������л���֮��ĵط�ʹ�øýӿ��ж���ĳ�����
 * �ӿ��ж���ĳ��������л��������л�����ʵ����ϵ���ܣ��Ķ��󣬿�
 * ��������л��������л����޷����У����ԸĶ��������
 * </pre>
 * 
 * @author Jeff Tang
 */
// !!!!!!!! ���³�����ֵ�����л������л�����ʵ���йأ���Ҫ�����޸Ĳݰ� !!!!!!!!!!!
public interface NSerializeBase {

	static final Unsafe UNSAFE = Unsf.unsafe;

	// ===========================================================================

	static final long START_OFFSET_BOOLEANARRAY_ = UNSAFE
			.arrayBaseOffset(boolean[].class);
	static final long START_OFFSET_BYTEARRAY = UNSAFE
			.arrayBaseOffset(byte[].class);
	static final long START_OFFSET_SHORTARRAY = UNSAFE
			.arrayBaseOffset(short[].class);
	static final long START_OFFSET_CHARARRAY = UNSAFE
			.arrayBaseOffset(char[].class);
	static final long START_OFFSET_INTARRAY = UNSAFE
			.arrayBaseOffset(int[].class);
	static final long START_OFFSET_FLOATARRAY = UNSAFE
			.arrayBaseOffset(float[].class);
	static final long START_OFFSET_LONGARRAY = UNSAFE
			.arrayBaseOffset(long[].class);
	static final long START_OFFSET_DOUBLEARRAY = UNSAFE
			.arrayBaseOffset(double[].class);
	static final long START_OFFSET_OBJECTARRAY = UNSAFE
			.arrayBaseOffset(Object[].class);

	// ===========================================================================

	// ===========================================================================

}
