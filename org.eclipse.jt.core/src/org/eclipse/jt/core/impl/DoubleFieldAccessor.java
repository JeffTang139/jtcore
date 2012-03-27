package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class DoubleFieldAccessor extends FieldAccessor {
	private static final DoubleFieldAccessor java = new DoubleFieldAccessor();
	private static final DoubleFieldAccessor dyn = new DoubleFieldAccessor() {
		@Override
		double getDoubleD(int offset, DynObj obj) {
			return unsafe.getDouble(obj.bin, (long) offset);
		}

		@Override
		void setDoubleD(DataType type, int offset, DynObj obj, double value) {
			unsafe.putDouble(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putDouble(dest.bin, offsetL, unsafe.getDouble(src.bin,
					offsetL));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			final long of = offset;
			final Object obj = entity.bin;
			long ov;
			long nv;
			do {
				ov = unsafe.getLongVolatile(obj, of);
				nv = Double.doubleToRawLongBits(Double.longBitsToDouble(ov)
						+ value);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			final long of = offset;
			final Object obj = entity.bin;
			long ov;
			long nv;
			do {
				ov = unsafe.getLongVolatile(obj, of);
				nv = Double.doubleToRawLongBits(Double.longBitsToDouble(ov)
						+ value);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private DoubleFieldAccessor() {
		super(8);
	}

	final static DoubleFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getDouble(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getDoubleD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getDouble(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getDoubleD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getDouble(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getDoubleD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getDouble(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getDoubleD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getDouble(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getDoubleD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getDouble(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getDoubleD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getDouble(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getDoubleD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getDouble(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getDoubleD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return unsafe.getDouble(obj, (long) offset);
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return unsafe.getDouble(obj, (long) offset);
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getDouble(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getDoubleD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getDouble(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getDoubleD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getDouble(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getDoubleD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getDouble(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getDoubleD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		unsafe.putDouble(obj, (long) offset, value);
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		unsafe.putDouble(obj, (long) offset, value);
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setDouble(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setDoubleD(type, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setDouble(type, offset, obj, value.getDouble());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setDoubleD(type, offset, obj, value.getDouble());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setDouble(this.getDouble(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setDouble(this.getDoubleD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putDouble(dest, offsetL, unsafe.getDouble(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putDouble(dest, offsetL, unsafe.getDouble(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeDouble(this.getDouble(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeDouble(this.getDoubleD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDouble(type, offset, obj, deserializer.readDouble());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDoubleD(type, offset, dynObj, deserializer.readDouble());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeDouble(this.getDouble(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeDouble(this.getDoubleD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setDouble(type, offset, value, unserializer.readDouble());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setDoubleD(type, offset, value, unserializer.readDouble());
	}

}
