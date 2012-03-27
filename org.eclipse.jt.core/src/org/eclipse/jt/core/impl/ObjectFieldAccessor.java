package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.ValueConvertException;
import org.eclipse.jt.core.type.WritableValue;


class ObjectFieldAccessor extends FieldAccessor {
	private static final ObjectFieldAccessor java = new ObjectFieldAccessor();
	private static final ObjectFieldAccessor dyn = new ObjectFieldAccessor() {
		@Override
		Object getObjectD(int offset, DynObj obj) {
			return obj.objs[offset];
		}

		@Override
		void setObjectD(DataType type, int offset, DynObj obj, Object value) {
			obj.objs[offset] = check(type, value);
		}

	};

	private ObjectFieldAccessor() {
		super(0);
	}

	final static ObjectFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getObject(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getObjectD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getObject(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getObjectD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getObject(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getObjectD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getObject(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getObjectD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getObject(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getObjectD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getObject(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getObjectD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getObject(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getObjectD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getObject(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getObjectD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getObject(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getObjectD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getObject(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getObjectD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getObject(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getObjectD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getObject(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getObjectD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return unsafe.getObject(obj, (long) offset);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return unsafe.getObject(obj, (long) offset);
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setObject(type, offset, obj, value);
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setObjectD(type, offset, obj, value);
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setObject(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setObjectD(type, offset, obj, Convert.toObject(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setObject(type, offset, obj, value);
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setObjectD(type, offset, obj, value);
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setObject(type, offset, obj, value);
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setObjectD(type, offset, obj, value);
	}

	private static Object check(DataType type, Object value) {
		if (value != null && !((ObjectDataType) type).isInstance(value)) {
			throw new ValueConvertException();
		}
		return value;
	}

	@Override
	final void setObject(DataType type, int offset, Object obj, Object value) {
		unsafe.putObject(obj, (long) offset, check(type, value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		unsafe.putObject(obj, (long) offset, check(type, value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setObject(type, offset, obj, check(type, value.getObject()));
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setObjectD(type, offset, obj, check(type, value.getObject()));
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setObject(this.getObject(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setObject(this.getObjectD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putObject(dest, offsetL, objaContext.assign(unsafe.getObject(
				src, offsetL), unsafe.getObject(dest, offsetL), type));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		this.setObjectD(null, offset, dest, objaContext.assign(this.getObjectD(
				offset, src), this.getObjectD(offset, dest), type));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (type instanceof ObjectDataTypeInternal) {
			ObjectDataTypeInternal odt = (ObjectDataTypeInternal) type;
			if (odt.supportSerialization()) {
				serializer.writeSpecialObject(odt, this.getObject(offset, obj));
				return;
			}
		}
		serializer.writeObject(this.getObject(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (type instanceof ObjectDataTypeInternal) {
			ObjectDataTypeInternal odt = (ObjectDataTypeInternal) type;
			if (odt.supportSerialization()) {
				serializer.writeSpecialObject(odt, this.getObjectD(offset,
						dynObj));
				return;
			}
		}
		serializer.writeObject(this.getObjectD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObject(type, offset, obj, deserializer.readObject());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObjectD(type, offset, dynObj, deserializer.readObject());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeObject(this.getObject(offset, value), type);
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeObject(this.getObjectD(offset, value), type);
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		final Object object = unserializer.readObject(type);
		if (object != NUnserializer.UNSERIALIZABLE_OBJECT) {
			this.setObject(type, offset, value, object);
		}
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		final Object object = unserializer.readObject(type);
		if (object != NUnserializer.UNSERIALIZABLE_OBJECT) {
			this.setObjectD(type, offset, value, object);
		}
	}

}
