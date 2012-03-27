package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.ValueConvertException;
import org.eclipse.jt.core.type.WritableValue;


class EnumFieldAccessor extends FieldAccessor {
	private static final EnumFieldAccessor java = new EnumFieldAccessor();
	private static final EnumFieldAccessor dyn = new EnumFieldAccessor() {
		@Override
		Object getObjectD(int offset, DynObj obj) {
			return obj.objs[offset];
		}

		@Override
		void setEnumNoCheckD(int offset, DynObj obj, Enum<?> value) {
			obj.objs[offset] = value;
		}

	};

	private EnumFieldAccessor() {
		super(0);
	}

	final static EnumFieldAccessor select(boolean isJavaField) {
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

	private final int getOrdinal(int offset, Object obj) {
		return ((Enum<?>) unsafe.getObject(obj, (long) offset)).ordinal();
	}

	private final int getOrdinalD(int offset, DynObj obj) {
		return ((Enum<?>) this.getObjectD(offset, obj)).ordinal();
	}

	private final void setOrdinal(DataType type, int offset, Object obj,
			int ordinal) {
		this.setEnumNoCheck(offset, obj, ((EnumTypeImpl<?>) type)
				.getEnum(ordinal));
	}

	private final void setOrdinalD(DataType type, int offset, DynObj obj,
			int ordinal) {
		this.setEnumNoCheckD(offset, obj, ((EnumTypeImpl<?>) type)
				.getEnum(ordinal));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getOrdinal(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getOrdinalD(offset, obj));
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
		return Convert.toShort(this.getOrdinal(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getOrdinalD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	long getLong(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	float getFloat(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	double getDouble(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
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
		Object e = this.getObject(offset, obj);
		return e != null ? e.toString() : null;
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		Object e = this.getObjectD(offset, obj);
		return e != null ? e.toString() : null;
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
		this.setOrdinal(type, offset, obj, value);
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setOrdinalD(type, offset, obj, value);
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
		this.setOrdinal(type, offset, obj, value);
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setOrdinalD(type, offset, obj, value);
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setOrdinal(type, offset, obj, value);
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setOrdinalD(type, offset, obj, value);
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setOrdinal(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setOrdinalD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setOrdinal(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setOrdinalD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setOrdinal(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setOrdinalD(type, offset, obj, Convert.toInt(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		Enum<?> e;
		if (value == null) {
			e = null;
		} else {
			e = ((EnumTypeImpl<?>) type).getEnum(value);
		}
		this.setEnumNoCheck(offset, obj, e);
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		Enum<?> e;
		if (value == null) {
			e = null;
		} else {
			e = ((EnumTypeImpl<?>) type).getEnum(value);
		}
		this.setEnumNoCheckD(offset, obj, e);
	}

	static private final Enum<?> toEnum(DataType type, Object value) {
		if (value != null && !((EnumTypeImpl<?>) type).isInstance(value)) {
			throw new ValueConvertException();
		}
		return (Enum<?>) value;
	}

	@Override
	final void setObject(DataType type, int offset, Object obj, Object value) {
		this.setEnumNoCheck(offset, obj, EnumFieldAccessor.toEnum(type, value));
	}

	@Override
	final void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this
				.setEnumNoCheckD(offset, obj, EnumFieldAccessor.toEnum(type,
						value));
	}

	final void setEnumNoCheck(int offset, Object obj, Enum<?> value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	void setEnumNoCheckD(int offset, DynObj obj, Enum<?> value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		Type targetType = value.getType().getRootType();
		if (targetType == StringType.TYPE) {
			this.setString(type, offset, obj, value.getString());
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			this.setOrdinal(type, offset, obj, value.getInt());
		} else {
			this.setEnumNoCheck(offset, obj, toEnum(type, value.getObject()));
		}
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		Type targetType = value.getType().getRootType();
		if (targetType == StringType.TYPE) {
			this.setStringD(type, offset, obj, value.getString());
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			this.setOrdinalD(type, offset, obj, value.getInt());
		} else {
			this.setEnumNoCheckD(offset, obj, toEnum(type, value.getObject()));
		}
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		Type targetType = target.getType().getRootType();
		if (targetType == StringType.TYPE) {
			target.setString(this.getString(offset, obj));
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			target.setInt(this.getOrdinal(offset, obj));
		} else {
			target.setObject(this.getObject(offset, obj));
		}
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		Type targetType = target.getType().getRootType();
		if (targetType == StringType.TYPE) {
			target.setString(this.getStringD(offset, dynObj));
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			target.setInt(this.getOrdinalD(offset, dynObj));
		} else {
			target.setObject(this.getObjectD(offset, dynObj));
		}
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
		this.setObjectD(null, offset, dest, this.getObjectD(offset, src));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeEnum((Enum<?>) this.getObject(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeEnum((Enum<?>) this.getObjectD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObject(type, offset, obj, deserializer.readEnum());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObjectD(type, offset, dynObj, deserializer.readEnum());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeEnumField((Enum<?>) this
				.getObject(offset, value), (EnumTypeImpl<?>) type);
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeEnumField((Enum<?>) this.getObjectD(offset,
				value), (EnumTypeImpl<?>) type);
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setObject(type, offset, value, unserializer.readEnumField(type));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setObjectD(type, offset, value, unserializer.readEnumField(type));
	}

}
