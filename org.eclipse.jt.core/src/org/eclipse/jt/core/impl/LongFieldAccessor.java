package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class LongFieldAccessor extends FieldAccessor {
	private static final LongFieldAccessor java = new LongFieldAccessor();
	private static final LongFieldAccessor dyn = new LongFieldAccessor() {
		@Override
		long getLongD(int offset, DynObj obj) {
			return unsafe.getLong(obj.bin, (long) offset);
		}

		@Override
		void setLongD(DataType type, int offset, DynObj obj, long value) {
			unsafe.putLong(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putLong(dest.bin, offsetL, unsafe.getLong(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			long of = offset;
			Object obj = entity.bin;
			long ov;
			do {
				ov = unsafe.getLongVolatile(obj, of);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, ov + value));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			long of = offset;
			Object obj = entity.bin;
			long ov;
			do {
				ov = unsafe.getLongVolatile(obj, of);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, ov + (long) value));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private LongFieldAccessor() {
		super(8);
	}

	final static LongFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getLong(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getLongD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getLong(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getLongD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getLong(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getLongD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getLong(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getLongD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getLong(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getLongD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getLong(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getLongD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getLong(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getLongD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getLong(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getLongD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getLong(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getLongD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getLong(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getLongD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getLong(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getLongD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getLong(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getLongD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setLong(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setLongD(type, offset, obj, Convert.toLong(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setLong(type, offset, obj, value.getLong());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setLongD(type, offset, obj, value.getLong());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setLong(this.getLong(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setLong(this.getLongD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putLong(dest, offsetL, unsafe.getLong(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putLong(dest, offsetL, unsafe.getLong(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getLong(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getLongD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setLong(type, offset, obj, deserializer.readLong());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setLongD(type, offset, dynObj, deserializer.readLong());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeLong(this.getLong(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeLong(this.getLongD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setLong(type, offset, value, unserializer.readLong());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setLongD(type, offset, value, unserializer.readLong());
	}

}
