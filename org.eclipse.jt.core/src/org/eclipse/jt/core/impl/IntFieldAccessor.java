package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class IntFieldAccessor extends FieldAccessor {
	private static final IntFieldAccessor java = new IntFieldAccessor();
	private static final IntFieldAccessor dyn = new IntFieldAccessor() {
		@Override
		int getIntD(int offset, DynObj obj) {
			return unsafe.getInt(obj.bin, (long) offset);
		}

		@Override
		void setIntD(DataType type, int offset, DynObj obj, int value) {
			unsafe.putInt(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putInt(dest.bin, offsetL, unsafe.getInt(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			do {
				ov = unsafe.getIntVolatile(obj, of);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, ov + (int) value));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			do {
				ov = unsafe.getIntVolatile(obj, of);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, ov + (int) value));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private IntFieldAccessor() {
		super(4);
	}

	final static IntFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getInt(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getIntD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getInt(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getIntD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getInt(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getIntD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getInt(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getIntD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getInt(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getIntD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return unsafe.getInt(obj, (long) offset);
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return unsafe.getInt(obj, (long) offset);
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getInt(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getIntD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getInt(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getIntD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getInt(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getIntD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getInt(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getIntD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getInt(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getIntD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getInt(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getIntD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getInt(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getIntD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		unsafe.putInt(obj, (long) offset, value);
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		unsafe.putInt(obj, (long) offset, value);
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setInt(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setIntD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setInt(type, offset, obj, value.getInt());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setIntD(type, offset, obj, value.getInt());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setInt(this.getInt(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setInt(this.getIntD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putInt(dest, offsetL, unsafe.getInt(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putInt(dest, offsetL, unsafe.getInt(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeInt(this.getInt(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeInt(this.getIntD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setInt(type, offset, obj, deserializer.readInt());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setInt(type, offset, dynObj, deserializer.readInt());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeInt(this.getInt(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeInt(this.getIntD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setInt(type, offset, value, unserializer.readInt());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setIntD(type, offset, value, unserializer.readInt());
	}

}
