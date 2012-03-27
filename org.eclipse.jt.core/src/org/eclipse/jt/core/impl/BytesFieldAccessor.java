package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class BytesFieldAccessor extends FieldAccessor {
	private static final BytesFieldAccessor java = new BytesFieldAccessor();
	private static final BytesFieldAccessor dyn = new BytesFieldAccessor() {
		@Override
		byte[] getBytesD(int offset, DynObj obj) {
			return (byte[]) obj.objs[offset];
		}

		@Override
		void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
			obj.objs[offset] = value;
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.objs[offset] = src.objs[offset];
		}

	};

	private BytesFieldAccessor() {
		super(0);
	}

	final static BytesFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getBytes(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getBytesD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getBytes(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getBytesD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getBytes(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getBytesD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return (byte[]) unsafe.getObject(obj, (long) offset);
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return (byte[]) unsafe.getObject(obj, (long) offset);
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getBytes(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getBytesD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getBytes(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getBytesD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getBytes(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getBytesD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getBytes(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getBytesD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getBytes(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getBytesD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getBytes(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getBytesD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getBytes(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getBytesD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getBytes(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getBytesD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return this.getBytes(offset, obj);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return this.getBytesD(offset, obj);
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setBytes(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setBytesD(type, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setBytes(type, offset, obj, value.getBytes());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setBytesD(type, offset, obj, value.getBytes());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setBytes(this.getBytes(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setBytes(this.getBytesD(offset, dynObj));
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
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (type == BytesType.TYPE) {
			serializer.writeSpecialObject(BytesType.TYPE, this.getObject(
					offset, obj));
		} else {
			throw new UnsupportedOperationException("Illegal data type: "
					+ type);
		}
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (type == BytesType.TYPE) {
			serializer.writeSpecialObject(BytesType.TYPE, this.getObjectD(
					offset, dynObj));
		} else {
			throw new UnsupportedOperationException("Illegal data type: "
					+ type);
		}
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		if (type == BytesType.TYPE) {
			this.setObject(BytesType.TYPE, offset, obj, deserializer
					.readObject());
		} else {
			throw new UnsupportedOperationException("Illegal data type: "
					+ type);
		}
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		if (type == BytesType.TYPE) {
			this.setObjectD(BytesType.TYPE, offset, dynObj, deserializer
					.readObject());
		} else {
			throw new UnsupportedOperationException("Illegal data type: "
					+ type);
		}
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeByteArrayField(this.getBytes(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeByteArrayField(this.getBytesD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setBytes(type, offset, value, unserializer.readByteArrayField());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setBytesD(type, offset, value, unserializer.readByteArrayField());
	}

}
