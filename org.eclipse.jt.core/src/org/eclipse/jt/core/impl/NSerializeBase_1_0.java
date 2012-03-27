package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

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
public interface NSerializeBase_1_0 extends NSerializeBase {
	// 1.0
	public final static short SERIALIZE_VERSION = 0x0100;

	static final Object NONE_OBJECT = new Object();

	// ===========================================================================
	// 数据长度定义

	static final byte SIZE_BOOLEAN = 1;
	static final byte SIZE_BYTE = 1;
	static final byte SIZE_SHORT = 2;
	static final byte SIZE_CHAR = 2;
	static final byte SIZE_INT = 4;
	static final byte SIZE_FLOAT = 4;
	static final byte SIZE_LONG = 8;
	static final byte SIZE_DOUBLE = 8;
	static final byte SIZE_GUID = SIZE_LONG + SIZE_LONG;
	static final byte SIZE_DATE = SIZE_LONG;
	static final byte SIZE_HEAD = SIZE_BYTE;
	static final byte SIZE_POINTER = SIZE_INT;

	// ===========================================================================
	// 数据头部定义

	static final byte HEAD_MASK = (byte) (0x07 << 5);
	static final byte HEAD_DEMASK = ~HEAD_MASK;

	static final byte HEAD_NULL = (0x00 << 5) & HEAD_MASK;
	static final byte HEAD_MARK = (0x01 << 5) & HEAD_MASK;
	static final byte HEAD_EMPTY = (0x02 << 5) & HEAD_MASK;
	static final byte HEAD_DATA = (0x03 << 5) & HEAD_MASK;
	static final byte HEAD_STRUCT = (byte) (0x04 << 5) & HEAD_MASK;
	static final byte HEAD_CUSTOM = (byte) (0x05 << 5) & HEAD_MASK;

	static final byte HEAD_MARK_UNSERIALIZABLE = HEAD_MARK
			| (0x01 & HEAD_DEMASK);
	static final byte HEAD_MARK_POINTER = HEAD_MARK | (0x02 & HEAD_DEMASK);

	static final byte HEAD_DATA_GUID = HEAD_DATA | (0x00 & HEAD_DEMASK);
	static final byte HEAD_DATA_STRING = HEAD_DATA | (0x01 & HEAD_DEMASK);
	static final byte HEAD_DATA_BOOLEANARRAY = HEAD_DATA | (0x02 & HEAD_DEMASK);
	static final byte HEAD_DATA_BYTEARRAY = HEAD_DATA | (0x03 & HEAD_DEMASK);
	static final byte HEAD_DATA_SHORTARRAY = HEAD_DATA | (0x04 & HEAD_DEMASK);
	static final byte HEAD_DATA_CHARARRAY = HEAD_DATA | (0x05 & HEAD_DEMASK);
	static final byte HEAD_DATA_INTARRAY = HEAD_DATA | (0x06 & HEAD_DEMASK);
	static final byte HEAD_DATA_FLOATARRAY = HEAD_DATA | (0x07 & HEAD_DEMASK);
	static final byte HEAD_DATA_LONGARRAY = HEAD_DATA | (0x08 & HEAD_DEMASK);
	static final byte HEAD_DATA_DOUBLEARRAY = HEAD_DATA | (0x09 & HEAD_DEMASK);
	static final byte HEAD_DATA_OBJECTARRAY0 = HEAD_DATA | (0x0A & HEAD_DEMASK);
	static final byte HEAD_DATA_OBJECTARRAY1 = HEAD_DATA | (0x0B & HEAD_DEMASK);
	static final byte HEAD_DATA_CLASS = HEAD_DATA | (0x0C & HEAD_DEMASK);
	static final byte HEAD_DATA_TYPE0 = HEAD_DATA | (0x0D & HEAD_DEMASK);
	static final byte HEAD_DATA_TYPE1 = HEAD_DATA | (0x0E & HEAD_DEMASK);
	static final byte HEAD_DATA_SMALLENUM0 = HEAD_DATA | (0x0F & HEAD_DEMASK);
	static final byte HEAD_DATA_SMALLENUM1 = HEAD_DATA | (0x10 & HEAD_DEMASK);
	static final byte HEAD_DATA_SMALLENUM2 = HEAD_DATA | (0x11 & HEAD_DEMASK);
	static final byte HEAD_DATA_LARGEENUM0 = HEAD_DATA | (0x12 & HEAD_DEMASK);
	static final byte HEAD_DATA_LARGEENUM1 = HEAD_DATA | (0x13 & HEAD_DEMASK);
	static final byte HEAD_DATA_LARGEENUM2 = HEAD_DATA | (0x14 & HEAD_DEMASK);
	static final byte HEAD_DATA_BOOLEANOBJECT = HEAD_DATA
			| (0x15 & HEAD_DEMASK);
	static final byte HEAD_DATA_BYTEOBJECT = HEAD_DATA | (0x16 & HEAD_DEMASK);
	static final byte HEAD_DATA_CHAROBJECT = HEAD_DATA | (0x17 & HEAD_DEMASK);
	static final byte HEAD_DATA_SHORTOBJECT = HEAD_DATA | (0x18 & HEAD_DEMASK);
	static final byte HEAD_DATA_INTOBJECT = HEAD_DATA | (0x19 & HEAD_DEMASK);
	static final byte HEAD_DATA_LONGOBJECT = HEAD_DATA | (0x1A & HEAD_DEMASK);
	static final byte HEAD_DATA_FLOATOBJECT = HEAD_DATA | (0x1B & HEAD_DEMASK);
	static final byte HEAD_DATA_DOUBLEOBJECT = HEAD_DATA | (0x1C & HEAD_DEMASK);

	static final byte HEAD_EMPTY_GUID = HEAD_EMPTY | (0x00 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_STRING = HEAD_EMPTY | (0x01 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_BOOLEANARRAY = HEAD_EMPTY
			| (0x02 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_BYTEARRAY = HEAD_EMPTY | (0x03 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_SHORTARRAY = HEAD_EMPTY | (0x04 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_CHARARRAY = HEAD_EMPTY | (0x05 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_INTARRAY = HEAD_EMPTY | (0x06 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_FLOATARRAY = HEAD_EMPTY | (0x07 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_LONGARRAY = HEAD_EMPTY | (0x08 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_DOUBLEARRAY = HEAD_EMPTY
			| (0x09 & HEAD_DEMASK);
	static final byte HEAD_EMPTY_OBJECTARRAY0 = HEAD_EMPTY
			| (0x0A & HEAD_DEMASK);
	static final byte HEAD_EMPTY_OBJECTARRAY1 = HEAD_EMPTY
			| (0x0B & HEAD_DEMASK);

	static final byte HEAD_STRUCT0 = HEAD_STRUCT | (0x00 & HEAD_DEMASK);
	static final byte HEAD_STRUCT1 = HEAD_STRUCT | (0x01 & HEAD_DEMASK);
	static final byte HEAD_STRUCT2 = HEAD_STRUCT | (0x02 & HEAD_DEMASK);

	static final byte HEAD_CUSTOM0 = HEAD_CUSTOM | (0x00 & HEAD_DEMASK);
	static final byte HEAD_CUSTOM1 = HEAD_CUSTOM | (0x01 & HEAD_DEMASK);

	static final String EMPTY_STRING = "";
	static final GUID EMPTY_GUID = GUID.emptyID;
	static final byte[] EMPTY_BYTEARRAY = new byte[0];

	static final Object[] EMPTY_OBJECTS = new Object[] { EMPTY_GUID,
			EMPTY_STRING, new boolean[0], EMPTY_BYTEARRAY, new short[0],
			new char[0], new int[0], new float[0], new long[0], new double[0],
			new Object[0], new Object[0] };

	// ===========================================================================

	static final int SMALLENUM_MAX_ORDINAL = Byte.MAX_VALUE;
	static final int LARGEENUM_MAX_ORDINAL = Integer.MAX_VALUE;
	static final int CONTINUOUS_BOOLEAN_MAX_COUNT = 8;
	static final int CONTINUOUS_NULL_MAX_COUNT = 0xFF & (~HEAD_MASK);

	// ===========================================================================
	// ===========================================================================

	enum ContinuousState {

		NONE,

		NULL,

		BOOLEAN

	}

	enum StackQuitState {

		STACK_EMPTY,

		BUFFER_OVERFLOW,

		NEW_ELEMENT_PUSHED

	}

}
