package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class DateFieldAccessor extends FieldAccessor {
	private static final DateFieldAccessor java = new DateFieldAccessor();
	private static final DateFieldAccessor dyn = new DateFieldAccessor() {
		@Override
		long getDateD(int offset, DynObj obj) {
			return unsafe.getLong(obj.bin, (long) offset);
		}

		@Override
		void setDateD(DataType type, int offset, DynObj obj, long value) {
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
			unsafe.putLong(entity.bin, (long) offset, value);
		}
	};

	private DateFieldAccessor() {
		super(8);
	}

	final static DateFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getDate(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getDateD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getDate(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getDateD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getDate(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getDateD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getDate(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getDateD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getDate(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getDateD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getDate(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getDateD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getDate(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getDateD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getDate(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getDateD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getDate(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getDateD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getDate(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getDateD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getDate(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getDateD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getDate(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getDateD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setDate(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setDateD(type, offset, obj, Convert.toDate(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setDate(type, offset, obj, value.getDate());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setDateD(type, offset, obj, value.getDate());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setDate(this.getDate(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setDate(this.getDateD(offset, dynObj));
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
		serializer.writeLong(this.getDate(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getDateD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDate(type, offset, obj, deserializer.readLong());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDateD(type, offset, dynObj, deserializer.readLong());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeDateField(this.getDate(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeDateField(this.getDateD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setDate(type, offset, value, unserializer.readDateField());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setDateD(type, offset, value, unserializer.readChar());
	}

}
