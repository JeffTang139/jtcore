package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


class ShortFieldAccessor extends FieldAccessor {
	private static final ShortFieldAccessor java = new ShortFieldAccessor();
	private static final ShortFieldAccessor dyn = new ShortFieldAccessor() {
		@Override
		short getShortD(int offset, DynObj obj) {
			return unsafe.getShort(obj.bin, (long) offset);
		}

		@Override
		void setShortD(DataType type, int offset, DynObj obj, short value) {
			unsafe.putShort(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			long offsetL = offset;
			unsafe.putShort(dest.bin, offsetL, unsafe
					.getShort(src.bin, offsetL));
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
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
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
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final ShortFieldAccessor dynForIBM = new ShortFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		};

		@Override
		short getShortD(int offset, DynObj obj) {
			return (short) (obj.bin[offset] & 0xFF | (obj.bin[offset + 1] << 8));
		}

		@Override
		void setShortD(DataType type, int offset, DynObj obj, short value) {
			obj.bin[offset] = (byte) value;
			obj.bin[offset + 1] = (byte) (value >>> 8);
		}

		@Override
		void assignD(DataType type, int offset, DynObj src, DynObj dest,
				OBJAContext objaContext) {
			dest.bin[offset] = src.bin[offset];
			dest.bin[offset + 1] = src.bin[offset + 1];
		}

		@Override
		final void SETLMergeLongValueNoCheck(int offset, DynObj entity,
				long value) {
			final long of = offset + Unsf.byte_array_base_offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(int offset, DynObj entity,
				double value) {
			final long of = offset + Unsf.byte_array_base_offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private ShortFieldAccessor() {
		super(2);
	}

	final static ShortFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getShort(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getShortD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getShort(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getShortD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getShort(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getShortD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getShort(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getShortD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return unsafe.getShort(obj, (long) offset);
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return unsafe.getShort(obj, (long) offset);
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getShort(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getShortD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getShort(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getShortD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getShort(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getShortD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getShort(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getShortD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getShort(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getShortD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getShort(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getShortD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getShort(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getShortD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getShort(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getShortD(offset, obj));
	}

	@Override
	void setBoolean(DataType type, int offset, Object obj, boolean value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBooleanD(DataType type, int offset, DynObj obj, boolean value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setChar(DataType type, int offset, Object obj, char value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setCharD(DataType type, int offset, DynObj obj, char value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setByte(DataType type, int offset, Object obj, byte value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setByteD(DataType type, int offset, DynObj obj, byte value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBytes(DataType type, int offset, Object obj, byte[] value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBytesD(DataType type, int offset, DynObj obj, byte[] value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setShort(DataType type, int offset, Object obj, short value) {
		unsafe.putShort(obj, (long) offset, value);
	}

	@Override
	void setShortD(DataType type, int offset, DynObj obj, short value) {
		unsafe.putShort(obj, (long) offset, value);
	}

	@Override
	void setInt(DataType type, int offset, Object obj, int value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setIntD(DataType type, int offset, DynObj obj, int value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setLong(DataType type, int offset, Object obj, long value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setLongD(DataType type, int offset, DynObj obj, long value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setFloat(DataType type, int offset, Object obj, float value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setFloatD(DataType type, int offset, DynObj obj, float value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDouble(DataType type, int offset, Object obj, double value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDoubleD(DataType type, int offset, DynObj obj, double value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDate(DataType type, int offset, Object obj, long value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDateD(DataType type, int offset, DynObj obj, long value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setString(DataType type, int offset, Object obj, String value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setStringD(DataType type, int offset, DynObj obj, String value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setGUID(DataType type, int offset, Object obj, GUID value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setGUIDD(DataType type, int offset, DynObj obj, GUID value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setObject(DataType type, int offset, Object obj, Object value) {
		this.setShort(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setObjectD(DataType type, int offset, DynObj obj, Object value) {
		this.setShortD(type, offset, obj, Convert.toShort(value));
	}

	@Override
	void setValue(DataType type, int offset, Object obj, ReadableValue value) {
		this.setShort(type, offset, obj, value.getShort());
	}

	@Override
	void setValueD(DataType type, int offset, DynObj obj, ReadableValue value) {
		this.setShortD(type, offset, obj, value.getShort());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setShort(this.getShort(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setShort(this.getShortD(offset, dynObj));
	}

	@Override
	final void assign(DataType type, int offset, Object src, Object dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putShort(dest, offsetL, unsafe.getShort(src, offsetL));
	}

	@Override
	void assignD(DataType type, int offset, DynObj src, DynObj dest,
			OBJAContext objaContext) {
		long offsetL = offset;
		unsafe.putShort(dest, offsetL, unsafe.getShort(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(DataType type, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeShort(this.getShort(offset, obj));
	}

	@Override
	void writeOutD(DataType type, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeShort(this.getShortD(offset, dynObj));
	}

	@Override
	void readIn(DataType type, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setShort(type, offset, obj, deserializer.readShort());
	}

	@Override
	void readInD(DataType type, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setShortD(type, offset, dynObj, deserializer.readShort());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final Object value, int offset) {
		return serializer.writeShort(this.getShort(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final DataType type, final DynObj value, final int offset) {
		return serializer.writeShort(this.getShortD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final Object value, final int offset) {
		this.setShort(type, offset, value, unserializer.readShort());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final DataType type, final DynObj value, final int offset) {
		this.setShortD(type, offset, value, unserializer.readShort());
	}

}
