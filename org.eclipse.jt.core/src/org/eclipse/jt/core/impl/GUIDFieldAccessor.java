package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class GUIDFieldAccessor extends FieldAccessor {
	private static final GUIDFieldAccessor java = new GUIDFieldAccessor();
	private static final GUIDFieldAccessor dyn = new GUIDFieldAccessor() {
		@Override
		GUID getGUIDD(int offset, DynObj obj) {
			return (GUID) obj.objs[offset];
		}

		@Override
		void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
			obj.objs[offset] = value;
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.objs[offset] = src.objs[offset];
		}

	};

	private GUIDFieldAccessor() {
		super(0);
	}

	final static GUIDFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getGUID(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getGUIDD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getGUID(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getGUIDD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getGUID(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getGUIDD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getGUID(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getGUIDD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getGUID(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getGUIDD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getGUID(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getGUIDD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getGUID(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getGUIDD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getGUID(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getGUIDD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getGUID(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getGUIDD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getGUID(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getGUIDD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getGUID(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getGUIDD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return (GUID) unsafe.getObject(obj, (long) offset);
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return (GUID) unsafe.getObject(obj, (long) offset);
	}

	@Override
	Object getObject(int offset, Object obj) {
		return this.getGUID(offset, obj);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return this.getGUIDD(offset, obj);
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setGUID(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setGUIDD(type, offset, obj, Convert.toGUID(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setGUID(type, offset, obj, value.getGUID());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setGUIDD(type, offset, obj, value.getGUID());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setGUID(this.getGUID(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setGUID(this.getGUIDD(offset, dynObj));
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
		serializer.writeGUID(this.getGUID(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeGUID(this.getGUIDD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setGUID(type, offset, obj, deserializer.readGUID());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setGUIDD(type, offset, dynObj, deserializer.readGUID());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeGUIDField(this.getGUID(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeGUIDField(this.getGUIDD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setGUID(type, offset, value, unserializer.readGUIDField());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setGUIDD(type, offset, value, unserializer.readGUIDField());
	}

}
