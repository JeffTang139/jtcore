package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeDetectorBase;
import org.eclipse.jt.core.type.ValueConvertException;
import org.eclipse.jt.core.type.WritableValue;

import sun.misc.Unsafe;


/**
 * 字段访问器
 * 
 * @author Jeff Tang
 * 
 */
abstract class FieldAccessor {
	public static final Unsafe unsafe = Unsf.unsafe;
	final int memBytes;

	int getBinDynFieldOffset(int binSize) {
		return binSize + Unsf.byte_array_base_offset;
	}

	FieldAccessor(int memBytes) {
		this.memBytes = memBytes;
	}

	boolean getBoolean(int offset, Object obj) {
		throw new ValueConvertException();
	}

	boolean getBooleanD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		throw new ValueConvertException();
	}

	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		throw new ValueConvertException();
	}

	byte getByte(int offset, Object obj) {
		throw new ValueConvertException();
	}

	byte getByteD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setByte(DataType type, int offset, Object obj, byte value) {
		throw new ValueConvertException();
	}

	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		throw new ValueConvertException();
	}

	byte[] getBytes(int offset, Object obj) {
		throw new ValueConvertException();
	}

	byte[] getBytesD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		throw new ValueConvertException();
	}

	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		throw new ValueConvertException();
	}

	char getChar(int offset, Object obj) {
		throw new ValueConvertException();
	}

	char getCharD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setChar(DataType type, int offset, Object obj, char value) {
		throw new ValueConvertException();
	}

	void setCharD(DataType type, int offset, DynObj obj, char value) {
		throw new ValueConvertException();
	}

	short getShort(int offset, Object obj) {
		throw new ValueConvertException();
	}

	short getShortD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setShort(DataType type, int offset, Object obj, short value) {
		throw new ValueConvertException();
	}

	void setShortD(DataType type, int offset, DynObj obj, short value) {
		throw new ValueConvertException();
	}

	int getInt(int offset, Object obj) {
		throw new ValueConvertException();
	}

	int getIntD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setInt(DataType type, int offset, Object obj, int value) {
		throw new ValueConvertException();
	}

	void setIntD(DataType type, int offset, DynObj obj, int value) {
		throw new ValueConvertException();
	}

	long getLong(int offset, Object obj) {
		throw new ValueConvertException();
	}

	long getLongD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setLong(DataType type, int offset, Object obj, long value) {
		throw new ValueConvertException();
	}

	void setLongD(DataType type, int offset, DynObj obj, long value) {
		throw new ValueConvertException();
	}

	long getDate(int offset, Object obj) {
		throw new ValueConvertException();
	}

	long getDateD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setDate(DataType type, int offset, Object obj, long value) {
		throw new ValueConvertException();
	}

	void setDateD(DataType type, int offset, DynObj obj, long value) {
		throw new ValueConvertException();
	}

	float getFloat(int offset, Object obj) {
		throw new ValueConvertException();
	}

	float getFloatD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setFloat(DataType type, int offset, Object obj, float value) {
		throw new ValueConvertException();
	}

	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		throw new ValueConvertException();
	}

	double getDouble(int offset, Object obj) {
		throw new ValueConvertException();
	}

	double getDoubleD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setDouble(DataType type, int offset, Object obj, double value) {
		throw new ValueConvertException();
	}

	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		throw new ValueConvertException();
	}

	String getString(int offset, Object obj) {
		throw new ValueConvertException();
	}

	String getStringD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setString(DataType type, int offset, Object obj, String value) {
		throw new ValueConvertException();
	}

	void setStringD(DataType type, int offset, DynObj obj, String value) {
		throw new ValueConvertException();
	}

	Object getObject(int offset, Object obj) {
		throw new ValueConvertException();
	}

	Object getObjectD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setObject(DataType type, int offset, Object obj, Object value) {
		throw new ValueConvertException();
	}

	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		throw new ValueConvertException();
	}

	GUID getGUID(int offset, Object obj) {
		throw new ValueConvertException();
	}

	GUID getGUIDD(int offset, DynObj obj) {
		throw new ValueConvertException();
	}

	void setGUID(DataType type, int offset, Object obj, GUID value) {
		throw new ValueConvertException();
	}

	void setGUIDD(DataType type, int offset, DynObj dynObj, GUID value) {
		throw new ValueConvertException();
	}

	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		throw new ValueConvertException();
	}

	void setValueD(DataType type, int offset, DynObj dynObj, ReadableValue value) {
		throw new ValueConvertException();
	}

	void assignTo(int offset, Object obj, WritableValue target) {
		throw new ValueConvertException();
	}

	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		throw new ValueConvertException();
	}

	/**
	 * 拷贝字段的值
	 * 
	 * @param objaContext
	 *            TODO
	 */
	void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 拷贝字段的值
	 */
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		throw new UnsupportedOperationException();
	}

	final static FieldAccessor getAccessor(DataType type, boolean isJavaField) {
		return type.detect(FieldAccessor.accessorSelector, isJavaField);
	}

	// //////////////////////////////
	// // SETL
	// //////////////////////////////
	void SETLMergeLongValueNoCheck(int offset, DynObj entity, long value) {
		throw new UnsupportedOperationException();
	}

	void SETLMergeDoubleValueNoCheck(int offset, DynObj entity, double value) {
		throw new UnsupportedOperationException();
	}

	boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
		this.setValueD(null, offset, entity, ReadableValue.NULL);
		return true;
	}

	/**
	 * 访问器选择器
	 */
	private final static TypeDetector<FieldAccessor, Boolean> accessorSelector = new TypeDetectorBase<FieldAccessor, Boolean>() {
		@Override
		public FieldAccessor inBoolean(Boolean isJavaField) {
			return BooleanFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inByte(Boolean isJavaField) {
			return ByteFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inDate(Boolean isJavaField) {
			return DateFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inDouble(Boolean isJavaField) {
			return DoubleFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inShort(Boolean isJavaField) {
			return ShortFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inInt(Boolean isJavaField) {
			return IntFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inFloat(Boolean isJavaField) {
			return FloatFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inLong(Boolean isJavaField) {
			return LongFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inGUID(Boolean isJavaField) {
			return GUIDFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inBytes(Boolean isJavaField, SequenceDataType type) {
			return BytesFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inString(Boolean isJavaField, SequenceDataType type) {
			return StringFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inEnum(Boolean isJavaField, EnumType<?> type) {
			return EnumFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inResource(Boolean isJavaField,
				Class<?> facadeClass, Object category) throws Throwable {
			return ObjectFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inObject(Boolean isJavaField, ObjectDataType type)
				throws Throwable {
			return ObjectFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inUnknown(Boolean isJavaField) throws Throwable {
			return StringFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inCharacter(Boolean isJavaField) throws Throwable {
			return CharFieldAccessor.select(isJavaField);
		}

		@Override
		public FieldAccessor inNull(Boolean isJavaField) throws Throwable {
			return StringFieldAccessor.select(isJavaField);
		}
	};

	/* -------------------------------------------------------------------- */
	// Serialization
	/* -------------------------------------------------------------------- */
	abstract void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException;

	abstract void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException;

	abstract void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException;

	abstract void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException;

	// ////////////////////////////////////
	// / new io Serialization

	/**
	 * 序列化静态对象中的域
	 */
	abstract boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, final int offset);

	/**
	 * 序列化动态对象中的域
	 */
	abstract boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset);

	/**
	 * 反序列化静态对象中的域
	 */
	abstract void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset);

	/**
	 * 反序列化动态对象中的域
	 */
	abstract void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset);

}
