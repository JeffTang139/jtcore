package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class BooleanFieldAccessor extends FieldAccessor {
	private static final BooleanFieldAccessor java = new BooleanFieldAccessor();
	private static final BooleanFieldAccessor dyn = new BooleanFieldAccessor() {
		@Override
		boolean getBooleanD(int offset, DynObj obj) {
			return unsafe.getBoolean(obj.bin, (long) offset);
		}

		@Override
		void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
			unsafe.putBoolean(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putBoolean(dest.bin, offsetL, unsafe.getBoolean(src.bin,
					offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			unsafe.putBoolean(entity.bin, (long) offset, value != 0);
		}
	};

	private static final BooleanFieldAccessor dynForIBM = new BooleanFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		};

		@Override
		boolean getBooleanD(int offset, DynObj obj) {
			return obj.bin[offset] != 0;
		}

		@Override
		void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
			obj.bin[offset] = value ? (byte) 1 : (byte) 0;
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.bin[offset] = src.bin[offset];
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			entity.bin[offset] = value != 0 ? (byte) 1 : (byte) 0;
		}
	};

	private BooleanFieldAccessor() {
		super(1);
	}

	final static BooleanFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return unsafe.getBoolean(obj, (long) offset);
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return unsafe.getBoolean(obj, (long) offset);
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getBoolean(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getBooleanD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getBoolean(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getBooleanD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getBoolean(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getBooleanD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getBoolean(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getBooleanD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getBoolean(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getBooleanD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getBoolean(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getBooleanD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getBoolean(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getBooleanD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getBoolean(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getBooleanD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getBoolean(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getBooleanD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getBoolean(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getBooleanD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getBoolean(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getBooleanD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getBoolean(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getBooleanD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		unsafe.putBoolean(obj, (long) offset, value);
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		unsafe.putBoolean(obj, (long) offset, value);
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setBoolean(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setBooleanD(type, offset, obj, Convert.toBoolean(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setBoolean(type, offset, obj, value.getBoolean());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setBooleanD(type, offset, obj, value.getBoolean());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setBoolean(this.getBoolean(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setBoolean(this.getBooleanD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putBoolean(dest, offsetL, unsafe.getBoolean(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putBoolean(dest, offsetL, unsafe.getBoolean(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeBoolean(this.getBoolean(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeBoolean(this.getBooleanD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setBoolean(type, offset, obj, deserializer.readBoolean());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setBooleanD(type, offset, dynObj, deserializer.readBoolean());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeBoolean(this.getBoolean(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeBoolean(this.getBooleanD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setBoolean(type, offset, value, unserializer.readBoolean());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setBooleanD(type, offset, value, unserializer.readBoolean());
	}

}
