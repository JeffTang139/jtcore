package org.eclipse.jt.core.impl;

import sun.misc.Unsafe;

/**
 * 序列化反序列器基接口
 * 
 * <pre>
 *     该接口中只定义了序列化器和反序列化器的公用属性和协议性常量，
 * 不建议在除序列化器反序列化器之外的地方使用该接口中定义的常量。
 * 接口中定义的常量与序列化器反序列化器的实现联系紧密，改动后，可
 * 能造成序列化器反序列化器无法运行，所以改动须谨慎。
 * </pre>
 * 
 * @author Jeff Tang
 */
// !!!!!!!! 以下常量的值与序列化反序列化器的实现有关，不要轻易修改草案 !!!!!!!!!!!
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
