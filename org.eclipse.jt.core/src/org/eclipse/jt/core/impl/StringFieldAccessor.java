package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class StringFieldAccessor extends FieldAccessor {
	private static final StringFieldAccessor java = new StringFieldAccessor();
	private static final StringFieldAccessor dyn = new StringFieldAccessor() {
		@Override
		String getStringD(int offset, DynObj obj) {
			return (String) obj.objs[offset];
		}

		@Override
		void setStringD(DataType type, int offset, DynObj obj, String value) {
			obj.objs[offset] = value;
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.objs[offset] = src.objs[offset];
		}

	};

	private StringFieldAccessor() {
		super(0);
	}

	final static StringFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getString(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getStringD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getString(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getStringD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getString(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getStringD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getString(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getStringD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getString(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getStringD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getString(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getStringD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getString(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getStringD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getString(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getStringD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getString(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getStringD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getString(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getStringD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return (String) unsafe.getObject(obj, (long) offset);
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return (String) unsafe.getObject(obj, (long) offset);
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getString(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getStringD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return this.getString(offset, obj);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return this.getStringD(offset, obj);
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setString(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setStringD(type, offset, obj, Convert.toString(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setString(type, offset, obj, value.getString());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setStringD(type, offset, obj, value.getString());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setString(this.getString(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setString(this.getStringD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putObject(dest, offsetL, unsafe.getObject(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putObject(dest, offsetL, unsafe.getObject(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeString(this.getString(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeString(this.getStringD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setString(type, offset, obj, deserializer.readString());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setStringD(type, offset, dynObj, deserializer.readString());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeStringField(this.getString(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeStringField(this.getStringD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setString(type, offset, value, unserializer.readStringField());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setStringD(type, offset, value, unserializer.readStringField());
	}

}
