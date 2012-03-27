package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class FloatFieldAccessor extends FieldAccessor {
	private static final FloatFieldAccessor java = new FloatFieldAccessor();
	private static final FloatFieldAccessor dyn = new FloatFieldAccessor() {
		@Override
		float getFloatD(int offset, DynObj obj) {
			return unsafe.getFloat(obj.bin, (long) offset);
		}

		@Override
		void setFloatD(DataType type, int offset, DynObj obj, float value) {
			unsafe.putFloat(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putFloat(dest.bin, offsetL, unsafe
					.getFloat(src.bin, offsetL));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			final long of = offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = Float
						.floatToRawIntBits((float) (Float.intBitsToFloat(ov) + value));
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			final long of = offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = Float.floatToRawIntBits(Float.intBitsToFloat(ov) + value);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private FloatFieldAccessor() {
		super(4);
	}

	final static FloatFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getFloat(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getFloatD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getFloat(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getFloatD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getFloat(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getFloatD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getFloat(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getFloatD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getFloat(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getFloatD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getFloat(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getFloatD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getFloat(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getFloatD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return unsafe.getFloat(obj, (long) offset);
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return unsafe.getFloat(obj, (long) offset);
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getFloat(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getFloatD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getFloat(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getFloatD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getFloat(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getFloatD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getFloat(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getFloatD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getFloat(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getFloatD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		unsafe.putFloat(obj, (long) offset, value);
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		unsafe.putFloat(obj, (long) offset, value);
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setFloat(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setFloatD(type, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setFloat(type, offset, obj, value.getFloat());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setFloatD(type, offset, obj, value.getFloat());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setFloat(this.getFloat(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setFloat(this.getFloatD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putFloat(dest, offsetL, unsafe.getFloat(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putFloat(dest, offsetL, unsafe.getFloat(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeFloat(this.getFloat(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeFloat(this.getFloatD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setFloat(type, offset, obj, deserializer.readFloat());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setFloatD(type, offset, dynObj, deserializer.readFloat());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeFloat(this.getFloat(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeFloat(this.getFloatD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setFloat(type, offset, value, unserializer.readFloat());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setFloatD(type, offset, value, unserializer.readFloat());
	}

}
