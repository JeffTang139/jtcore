package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class ByteFieldAccessor extends FieldAccessor {
	private static final ByteFieldAccessor java = new ByteFieldAccessor();
	private static final ByteFieldAccessor dyn = new ByteFieldAccessor() {
		@Override
		byte getByteD(int offset, DynObj obj) {
			return unsafe.getByte(obj.bin, (long) offset);
		}

		@Override
		void setByteD(DataType type, int offset, DynObj obj, byte value) {
			unsafe.putByte(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putByte(dest.bin, offsetL, unsafe.getByte(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final ByteFieldAccessor dynForIBM = new ByteFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		};

		@Override
		byte getByteD(int offset, DynObj obj) {
			return obj.bin[offset];
		}

		@Override
		void setByteD(DataType type, int offset, DynObj obj, byte value) {
			obj.bin[offset] = value;
		}

		@Override
		final void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.bin[offset] = src.bin[offset];
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			long of = offset + Unsf.byte_array_base_offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			long of = offset + Unsf.byte_array_base_offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private ByteFieldAccessor() {
		super(1);
	}

	final static ByteFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getByte(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getByteD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getByte(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getByteD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return unsafe.getByte(obj, (long) offset);
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return unsafe.getByte(obj, (long) offset);
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getByte(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getByteD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getByte(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getByteD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getByte(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getByteD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getByte(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getByteD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getByte(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getByteD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getByte(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getByteD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getByte(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getByteD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getByte(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getByteD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getByte(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getByteD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getByte(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getByteD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		unsafe.putByte(obj, (long) offset, value);
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		unsafe.putByte(obj, (long) offset, value);
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setByte(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setByteD(type, offset, obj, Convert.toByte(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setByte(type, offset, obj, value.getByte());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setByteD(type, offset, obj, value.getByte());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setByte(this.getByte(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setByte(this.getByteD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putByte(dest, offsetL, unsafe.getByte(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putByte(dest, offsetL, unsafe.getByte(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeByte(this.getByte(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeByte(this.getByteD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setByte(type, offset, obj, deserializer.readByte());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setByteD(type, offset, dynObj, deserializer.readByte());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeByte(this.getByte(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeByte(this.getByteD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setByte(type, offset, value, unserializer.readByte());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setByteD(type, offset, value, unserializer.readByte());
	}

}
