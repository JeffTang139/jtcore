package org.eclipse.jt.core.impl;

import java.util.Arrays;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


/**
 * 序列化器
 * 
 * <pre>
 *     序列化器与反序列化器以序列化协议为依据形成对应关系。
 *     用法示例：
 *         if (!serializer.serializeStart(object, fragment) {
 *             do {
 *             } while (serializer.serializeRest(newFragment);
 *         }
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
// !!!!!!! 以下代码与序列化协议联系紧密，修改须谨慎 !!!!!!!!
public class NSerializer_1_0 extends NSerializer implements NSerializeBase_1_0 {

	@Override
	public final short getVersion() {
		return SERIALIZE_VERSION;
	}

	/**
	 * 工厂
	 */
	public final static NSerializerFactory factory = new NSerializerFactory(
			SERIALIZE_VERSION) {
		@Override
		public final NSerializer newNSerializer() {
			return new NSerializer_1_0();
		}

	};

	/**
	 * 开始序列化一个对象到指定的fragment中
	 * 
	 * @param object
	 *            对象
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment不够装下object序列化后产生的字节流，需要更多的fragment完成序列化时，返回false，
	 *         申请新fragment以后 ，调用serializeRest(fragment)方法继续完成序列化；否则说明序列化已经完成
	 *         ，返回true
	 */
	@Override
	public final boolean serializeStart(final Object object,
			final DataOutputFragment fragment) {
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		if (!this.serialized) {
			throw new IllegalStateException("当前序列化任务还未完成");
		}
		this.dataOutputFragment = fragment;
		this.reset();
		return this.serialized = this.internalSerializeStart(object);
	}

	/**
	 * 把当前序列化对象未序列化部分序列化到指定的fragment中
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment不够装下object序列化后产生的字节流，需要更多的fragment完成序列化时，返回false，
	 *         申请新fragment以后 ，再调用该方法继续完成序列化；否则说明序列化已经完成 ，返回true
	 */
	@Override
	public final boolean serializeRest(final DataOutputFragment fragment) {
		if (this.serialized) {
			throw new IllegalStateException("当前序列化任务已经完成");
		}
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		this.dataOutputFragment = fragment;
		if (this.rootObject != NONE_OBJECT) {
			return this.serialized = this
					.internalSerializeStart(this.rootObject);
		}
		if (!this.structStack.isEmpty()) {
			cyc: {
				while (true) {
					switch (this.processStructStack()) {
					case NEW_ELEMENT_PUSHED:
						continue;
					case BUFFER_OVERFLOW:
						return this.serialized = false;
					case STACK_EMPTY:
						break cyc;
					}
				}
			}
		}
		return this.serialized = this.tryWriteContinuousValue();
	}

	/**
	 * 判断当前对象序列化是否完成
	 * 
	 * @return 如果当前对象序列化已经完成，则返回true，否则返回false
	 */
	@Override
	public final boolean isSerialized() {
		return this.serialized;
	}

	/**
	 * 重置序列化器所有状态
	 */
	@Override
	public final void reset() {
		this.serialized = true;
		this.rootObject = NONE_OBJECT;
		this.continuousState = ContinuousState.NONE;
		this.continuousValue = 0;
		this.continuousBoolCount = 0;
		this.objectIndexMap.reset();
		this.structStack.reset();
		this.resetVLDWrite();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeBoolean(final boolean value) {
		switch (this.continuousState) {
		case BOOLEAN:
			if (this.continuousValue < CONTINUOUS_BOOLEAN_MAX_COUNT) {
				if (value) {
					this.continuousValue |= (1 << this.continuousBoolCount);
				}
				this.continuousBoolCount++;
				return SERIALIZE_SUCCESS;
			}
		case NULL:
			if (!this.tryWriteContinuousValue()) {
				return SERIALIZE_FAIL;
			}
		case NONE:
			break;
		default:
			throw serializeException();
		}
		this.continuousState = ContinuousState.BOOLEAN;
		this.continuousValue = value ? (byte) 0x01 : 0;
		this.continuousBoolCount = 1;
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeByte(final byte value) {
		if (this.dataOutputFragment.remain() < SIZE_BYTE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeShort(final short value) {
		if (this.dataOutputFragment.remain() < SIZE_SHORT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeShort(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeChar(final char value) {
		if (this.dataOutputFragment.remain() < SIZE_CHAR) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeChar(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeInt(final int value) {
		if (this.dataOutputFragment.remain() < SIZE_INT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeInt(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeFloat(final float value) {
		if (this.dataOutputFragment.remain() < SIZE_FLOAT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeFloat(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeLong(final long value) {
		if (this.dataOutputFragment.remain() < SIZE_LONG) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeLong(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeDouble(final double value) {
		if (this.dataOutputFragment.remain() < SIZE_DOUBLE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeDouble(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeGUIDField(final GUID value) {
		if (value == null) {
			return this.writeNull();
		}
		this.tryWriteContinuousValue();
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex >= 0) {
			return this.writePointer(objectIndex);
		}
		return this.writeGUIDData(value);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeStringField(final String value) {
		if (value == null) {
			return this.writeNull();
		}
		this.tryWriteContinuousValue();
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex >= 0) {
			return this.writePointer(objectIndex);
		}
		return this.writeStringData(value);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeDateField(final long value) {
		this.tryWriteContinuousValue();
		return this.writeDate(value);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeEnumField(final Enum<?> value, EnumTypeImpl<?> enumType) {
		if (value == null) {
			return this.writeNull();
		}
		this.tryWriteContinuousValue();
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex >= 0) {
			return this.writePointer(objectIndex);
		}
		return this.writeEnumData(value, enumType,
				value.getClass() == enumType.javaClass);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeByteArrayField(final byte[] value) {
		if (value == null) {
			return this.writeNull();
		}
		this.tryWriteContinuousValue();
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex >= 0) {
			return this.writePointer(objectIndex);
		}
		return this.writeByteArrayData(value);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeObject(final Object value, final DataType declaredType) {
		if (value == null) {
			return this.writeNull();
		}
		this.tryWriteContinuousValue();
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex >= 0) {
			return this.writePointer(objectIndex);
		}
		if (value instanceof DataType) {
			return this.writeDataType((DataType) value);
		}
		return ((ObjectDataTypeInternal) DataTypeBase.dataTypeOfJavaObj(value))
				.nioSerializeData(this, value);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeUnserializable() {
		return this.writeByte(HEAD_MARK_UNSERIALIZABLE);
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeGUIDData(final GUID value) {
		if (value.equals(EMPTY_GUID)) {
			return this.writeByte(HEAD_EMPTY_GUID);
		}
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_GUID);
		this.dataOutputFragment.writeLong(value.getMostSigBits());
		this.dataOutputFragment.writeLong(value.getLeastSigBits());
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeEnumData(final Enum<?> value, EnumTypeImpl<?> enumType,
			boolean declared) {
		assert value != null;
		assert enumType != null;
		final int enumOrdinal = value.ordinal();
		if (enumOrdinal > LARGEENUM_MAX_ORDINAL) {
			throw serializeException();
		}
		final DataOutputFragment dataOutputFragment = this.dataOutputFragment;
		final boolean isSmallEnum = enumOrdinal < SMALLENUM_MAX_ORDINAL + 1;
		if (declared) {
			if (isSmallEnum) {
				if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_BYTE) {
					return SERIALIZE_FAIL;
				}
				dataOutputFragment.writeByte(HEAD_DATA_SMALLENUM0);
			} else {
				if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				dataOutputFragment.writeByte(HEAD_DATA_LARGEENUM0);
			}
		} else {
			final GUID enumTypeID = enumType.getID();
			final int enumTypeIDIndex = this.objectIndexMap
					.tryGetIndex(enumTypeID);
			if (enumTypeIDIndex < 0) {
				if (isSmallEnum) {
					if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID
							+ SIZE_BYTE) {
						return SERIALIZE_FAIL;
					}
					dataOutputFragment.writeByte(HEAD_DATA_SMALLENUM2);
				} else {
					if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID
							+ SIZE_INT) {
						return SERIALIZE_FAIL;
					}
					dataOutputFragment.writeByte(HEAD_DATA_LARGEENUM2);
				}
				dataOutputFragment.writeLong(enumTypeID.getMostSigBits());
				dataOutputFragment.writeLong(enumTypeID.getLeastSigBits());
				this.objectIndexMap.tryPutObject(enumTypeID);
			} else {
				if (isSmallEnum) {
					if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER
							+ SIZE_BYTE) {
						return SERIALIZE_FAIL;
					}
					dataOutputFragment.writeByte(HEAD_DATA_SMALLENUM1);
				} else {
					if (dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER
							+ SIZE_INT) {
						return SERIALIZE_FAIL;
					}
					dataOutputFragment.writeByte(HEAD_DATA_LARGEENUM1);
				}
				dataOutputFragment.writeInt(enumTypeIDIndex);
			}
		}
		if (isSmallEnum) {
			dataOutputFragment.writeByte((byte) enumOrdinal);
		} else {
			dataOutputFragment.writeInt(enumOrdinal);
		}
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeClassData(final Class<?> value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.STRING) {
				throw serializeException();
			}
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
				return SERIALIZE_FAIL;
			}
			final String className = value.getName();
			final int VLDLength = className.length();
			this.dataOutputFragment.writeByte(HEAD_DATA_CLASS);
			this.dataOutputFragment.writeInt(VLDLength);
			this.setVLDWrite(VLDataWriter.STRING, className, VLDLength);
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeBooleanArrayData(final boolean[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.BOOLEANARRAY) {
				throw serializeException();
			}
		} else {
			final int booleanArrayLength = value.length;
			if (booleanArrayLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_BOOLEANARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				final int VLDLength = (booleanArrayLength / 8) + 1;
				final byte[] VLDObject = new byte[VLDLength];
				int byteArrayIndex = 0;
				int index = 0;
				byte booleans;
				for (; (index + 8) < booleanArrayLength;) {
					booleans = 0;
					for (int offset = 0; offset < 8; offset++) {
						if (value[index++]) {
							booleans |= (1 << offset);
						}
					}
					VLDObject[byteArrayIndex++] = booleans;
				}
				booleans = 0;
				for (int offset = 0; index < booleanArrayLength;) {
					if (value[index++]) {
						booleans |= (1 << offset);
					}
					offset++;
				}
				VLDObject[byteArrayIndex] = booleans;
				this.dataOutputFragment.writeByte(HEAD_DATA_BOOLEANARRAY);
				this.dataOutputFragment.writeInt(booleanArrayLength);
				this.setVLDWrite(VLDataWriter.BOOLEANARRAY, VLDObject,
						VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeByteArrayData(final byte[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.BYTEARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_BYTEARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_BYTEARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.BYTEARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeShortArrayData(final short[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.SHORTARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_SHORTARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_SHORTARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.SHORTARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeCharArrayData(final char[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.CHARARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_CHARARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_CHARARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.CHARARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeIntArrayData(final int[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.INTARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_INTARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_INTARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.INTARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeFloatArrayData(final float[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.FLOATARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_FLOATARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_FLOATARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.FLOATARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeLongArrayData(final long[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.LONGARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_LONGARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_LONGARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.LONGARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeDoubleArrayData(final double[] value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.DOUBLEARRAY) {
				throw serializeException();
			}
		} else {
			final int VLDLength = value.length;
			if (VLDLength == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_DOUBLEARRAY);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_DOUBLEARRAY);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.DOUBLEARRAY, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeObjectArrayData(final ObjectArrayDataType arrayType,
			final Object[] value) {
		final int arrayLength = value.length;
		final GUID typeID = arrayType.getID();
		final int typeIDIndex = this.objectIndexMap.tryGetIndex(typeID);
		if (arrayLength == 0) {
			if (typeIDIndex < 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_OBJECTARRAY1);
				this.dataOutputFragment.writeLong(typeID.getMostSigBits());
				this.dataOutputFragment.writeLong(typeID.getLeastSigBits());
				this.objectIndexMap.tryPutObject(typeID);
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_OBJECTARRAY0);
				this.dataOutputFragment.writeInt(typeIDIndex);
			}
			return SERIALIZE_SUCCESS;
		} else {
			if (typeIDIndex < 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID
						+ SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_OBJECTARRAY1);
				this.dataOutputFragment.writeLong(typeID.getMostSigBits());
				this.dataOutputFragment.writeLong(typeID.getLeastSigBits());
				this.dataOutputFragment.writeInt(arrayLength);
				this.objectIndexMap.tryPutObject(typeID);
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER
						+ SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_DATA_OBJECTARRAY0);
				this.dataOutputFragment.writeInt(typeIDIndex);
				this.dataOutputFragment.writeInt(arrayLength);
			}
			this.structStack.beginProcessStructObject(value,
					StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD, null);
			return SERIALIZE_SUCCESS;
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeStringData(final String value) {
		if (this.VLDWriter != null) {
			if (this.VLDWriter != VLDataWriter.STRING) {
				throw serializeException();
			}
		} else {
			if (value.equals(EMPTY_STRING)) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_EMPTY_STRING);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				}
				final int VLDLength = value.length();
				this.dataOutputFragment.writeByte(HEAD_DATA_STRING);
				this.dataOutputFragment.writeInt(VLDLength);
				this.setVLDWrite(VLDataWriter.STRING, value, VLDLength);
			}
		}
		return this.tryWriteVLD();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean writeStructData(final Object value,
			final StructDefineImpl structDefine, boolean declared) {
		assert value != null;
		final boolean serializeSuccess = this.writeStructHead(structDefine,
				declared);
		if (serializeSuccess) {
			this.structStack.beginProcessStructObject(value, structDefine
					.getFirstNIOSerializableField(), null);
		}
		return serializeSuccess;
	}

	@Override
	@SuppressWarnings("unchecked")
	final boolean writeCustomSerializeDataObject(final Object value,
			ObjectDataType type, DataObjectTranslator serializer) {
		if (this.writeCustomSerializeDataHead(type.getID(), serializer
				.getVersion())) {
			this.structStack.beginProcessStructObject(value,
					StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD,
					serializer);
			return SERIALIZE_SUCCESS;
		}
		return SERIALIZE_FAIL;
	}

	@Override
	final boolean writeBooleanObject(final Boolean value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_BYTE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_BOOLEANOBJECT);
		this.dataOutputFragment.writeByte(value ? (byte) 1 : (byte) 0);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeByteObject(final Byte value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_BYTE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_BYTEOBJECT);
		this.dataOutputFragment.writeByte(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeCharObject(final Character value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_CHAR) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_CHAROBJECT);
		this.dataOutputFragment.writeChar(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeShortObject(final Short value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_SHORT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_SHORTOBJECT);
		this.dataOutputFragment.writeShort(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeIntObject(final Integer value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_INTOBJECT);
		this.dataOutputFragment.writeInt(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeLongObject(final Long value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_LONG) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_LONGOBJECT);
		this.dataOutputFragment.writeLong(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeFloatObject(final Float value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_FLOAT) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_FLOATOBJECT);
		this.dataOutputFragment.writeFloat(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeDoubleObject(final Double value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_DOUBLE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_DATA_DOUBLEOBJECT);
		this.dataOutputFragment.writeDouble(value);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	private final boolean internalSerializeStart(final Object object) {
		boolean result = this.writeObject(object, null);
		if (result) {
			if (this.structStack.isEmpty()) {
				result = this.tryWriteContinuousValue();
			} else {

				cyc: {
					while (true) {
						switch (this.processStructStack()) {
						case NEW_ELEMENT_PUSHED:
							continue;
						case BUFFER_OVERFLOW:
							return false;
						case STACK_EMPTY:
							this.tryWriteContinuousValue();
							break cyc;
						}
					}
				}
			}
		} else {
			this.rootObject = object;
		}
		return result;
	}

	private final boolean writeNull() {
		switch (this.continuousState) {
		case NULL:
			if (this.continuousValue < CONTINUOUS_NULL_MAX_COUNT) {
				this.continuousValue++;
				return SERIALIZE_SUCCESS;
			}
		case BOOLEAN:
			if (!this.tryWriteContinuousValue()) {
				return SERIALIZE_FAIL;
			}
		case NONE:
			break;
		default:
			throw serializeException();
		}
		this.continuousState = ContinuousState.NULL;
		this.continuousValue = 1;
		return SERIALIZE_SUCCESS;
	}

	private final boolean writeDate(final long value) {
		if (this.dataOutputFragment.remain() < SIZE_DATE) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeLong(value);
		return SERIALIZE_SUCCESS;
	}

	private final boolean writePointer(final int objectIndex) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
			return SERIALIZE_FAIL;
		}
		this.dataOutputFragment.writeByte(HEAD_MARK_POINTER);
		this.dataOutputFragment.writeInt(objectIndex);
		return SERIALIZE_SUCCESS;
	}

	private final boolean writeStructHead(final StructDefineImpl structDefine,
			final boolean declared) {
		if (declared) {
			return this.writeByte(HEAD_STRUCT0);
		} else {
			final GUID structID = structDefine.getID();
			final int structIDIndex = this.objectIndexMap.tryGetIndex(structID);
			if (structIDIndex < 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_STRUCT2);
				this.dataOutputFragment.writeLong(structID.getMostSigBits());
				this.dataOutputFragment.writeLong(structID.getLeastSigBits());
				this.objectIndexMap.tryPutObject(structID);
				return SERIALIZE_SUCCESS;
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
					return SERIALIZE_FAIL;
				}
				this.dataOutputFragment.writeByte(HEAD_STRUCT1);
				this.dataOutputFragment.writeInt(structIDIndex);
				return SERIALIZE_SUCCESS;
			}
		}
	}

	private final boolean writeCustomSerializeDataHead(final GUID value,
			final short version) {
		final int valueIndex = this.objectIndexMap.tryGetIndex(value);
		if (valueIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID
					+ SIZE_SHORT) {
				return SERIALIZE_FAIL;
			}
			this.dataOutputFragment.writeByte(HEAD_CUSTOM1);
			this.dataOutputFragment.writeLong(value.getMostSigBits());
			this.dataOutputFragment.writeLong(value.getLeastSigBits());
			this.objectIndexMap.tryPutObject(value);
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER
					+ SIZE_SHORT) {
				return SERIALIZE_FAIL;
			}
			this.dataOutputFragment.writeByte(HEAD_CUSTOM0);
			this.dataOutputFragment.writeInt(valueIndex);
		}
		this.dataOutputFragment.writeShort(version);
		return SERIALIZE_SUCCESS;
	}

	private final boolean writeDataType(final DataType type) {
		final GUID typeID = type.getID();
		final int typeIDIndex = this.objectIndexMap.tryGetIndex(typeID);
		if (typeIDIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
				return SERIALIZE_FAIL;
			}
			this.dataOutputFragment.writeByte(HEAD_DATA_TYPE1);
			this.dataOutputFragment.writeLong(typeID.getMostSigBits());
			this.dataOutputFragment.writeLong(typeID.getLeastSigBits());
			this.objectIndexMap.tryPutObject(typeID);
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
				return SERIALIZE_FAIL;
			}
			this.dataOutputFragment.writeByte(HEAD_DATA_TYPE0);
			this.dataOutputFragment.writeInt(typeIDIndex);
		}
		return SERIALIZE_SUCCESS;
	}

	private final boolean tryWriteVLD() {
		if (this.VLDWriter != null) {
			if (this.VLDWroteCount != this.VLDLength) {
				this.VLDWroteCount = this.VLDWriter.writeElement(this,
						this.dataOutputFragment, this.VLDObject,
						this.VLDWroteCount, this.VLDLength);
				if (this.VLDWroteCount != this.VLDLength) {
					return SERIALIZE_FAIL;
				}
			}
			this.objectIndexMap.tryPutObject(this.VLDObject);
			this.resetVLDWrite();
		}
		return SERIALIZE_SUCCESS;
	}

	private final boolean tryWriteContinuousValue() {
		if (this.continuousState != ContinuousState.NONE) {
			if (this.dataOutputFragment.remain() < SIZE_BYTE) {
				return false;
			}
			switch (this.continuousState) {
			case BOOLEAN:
				this.dataOutputFragment.writeByte(this.continuousValue);
				break;
			case NULL:
				this.dataOutputFragment
						.writeByte((byte) (HEAD_NULL | this.continuousValue));
				break;
			default:
				throw serializeException();
			}
			this.continuousState = ContinuousState.NONE;
			this.continuousValue = 0;
			this.continuousBoolCount = 0;
		}
		return true;
	}

	private final StackQuitState processStructStack() {
		Object object = null;
		int structStackDeepness = this.structStack.deepness();
		for (;;) {
			final StructFieldDefineImpl field = this.structStack
					.beginProcessField();
			final boolean serializeSuccess;
			if (field == null) {
				if (this.structStack.endProcessStructObject(this)) {
					return StackQuitState.STACK_EMPTY;
				} else {
					object = null;
					structStackDeepness = this.structStack.deepness();
					continue;
				}
			} else if (field == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD
					|| field == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				serializeSuccess = this.writeObject(this.structStack
						.getCurrentObject(), null);
				object = null;
			} else {
				if (object == null) {
					object = this.structStack.getCurrentObject();
				}
				serializeSuccess = field.serialize(this, object);
			}
			if (serializeSuccess) {
				if (structStackDeepness != this.structStack.deepness()) {
					this.structStack.endProcessField(field);
					return StackQuitState.NEW_ELEMENT_PUSHED;
				}
			} else {
				return StackQuitState.BUFFER_OVERFLOW;
			}
			this.structStack.endProcessField();
		}
	}

	private final void resetVLDWrite() {
		this.VLDWriter = null;
		this.VLDObject = null;
		this.VLDLength = 0;
		this.VLDWroteCount = 0;
	}

	private final void setVLDWrite(final VLDataWriter VLDWriter,
			final Object VLDObject, final int VLDLength) {
		this.VLDWriter = VLDWriter;
		this.VLDObject = VLDObject;
		this.VLDLength = VLDLength;
	}

	private static final RuntimeException serializeException() {
		return new RuntimeException("序列化异常");
	}

	private static final boolean SERIALIZE_SUCCESS = true;

	private static final boolean SERIALIZE_FAIL = !SERIALIZE_SUCCESS;

	private final ObjectIndexMap objectIndexMap = new ObjectIndexMap();

	private final StructStack structStack = new StructStack();

	private boolean serialized = true;

	private Object rootObject;

	private DataOutputFragment dataOutputFragment;

	private ContinuousState continuousState;

	private byte continuousValue;

	private int continuousBoolCount;

	private VLDataWriter VLDWriter;

	private Object VLDObject;

	private int VLDLength;

	private int VLDWroteCount;

	private enum VLDataWriter {

		BOOLEANARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final byte[] array = (byte[]) data;
				final int bufferRemain = dataOutputFragment.remain();
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeByte(array[index]);
				}
				return canWriteToIndex;
			}
		},

		BYTEARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final byte[] array = (byte[]) data;
				final int bufferRemain = dataOutputFragment.remain();
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeByte(array[index]);
				}
				return canWriteToIndex;
			}
		},

		SHORTARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final short[] array = (short[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeShort(array[index]);
				}
				return canWriteToIndex;
			}
		},

		CHARARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeChar(array[index]);
				}
				return canWriteToIndex;
			}
		},

		INTARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final int[] array = (int[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeInt(array[index]);
				}
				return canWriteToIndex;
			}
		},

		FLOATARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final float[] array = (float[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeFloat(array[index]);
				}
				return canWriteToIndex;
			}
		},

		LONGARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final long[] array = (long[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeLong(array[index]);
				}
				return canWriteToIndex;
			}
		},

		DOUBLEARRAY {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final double[] array = (double[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeDouble(array[index]);
				}
				return canWriteToIndex;
			}
		},

		STRING {
			@Override
			final int writeElement(final NSerializer_1_0 serializer,
					final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final String string = (String) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeChar(string.charAt(index));
				}
				return canWriteToIndex;
			}
		};

		/**
		 * @return 返回写了多少个元素
		 */
		abstract int writeElement(final NSerializer_1_0 serializer,
				final DataOutputFragment dataOutputFragment, final Object data,
				final int startIndex, final int length);

	}

	private static final class ObjectIndexMap {

		ObjectIndexMap() {
			this.map = new MapEntry[16];
		}

		final void tryPutObject(final Object object) {
			if (object == null) {
				throw new NullArgumentException("object");
			}
			final int hashCode = System.identityHashCode(object);
			final int oldLength = this.map.length;
			int hashIndex = hashCode & (oldLength - 1);
			if (++this.size > oldLength * 0.75) {
				final int newLength = oldLength * 2;
				final MapEntry[] newMap = new MapEntry[newLength];
				for (int index = 0; index < oldLength; index++) {
					for (MapEntry entry = this.map[index], next; entry != null; entry = next) {
						hashIndex = System.identityHashCode(entry.object)
								& (newLength - 1);
						next = entry.next;
						entry.next = newMap[hashIndex];
						newMap[hashIndex] = entry;
					}
				}
				this.map = newMap;
				hashIndex = hashCode & (newLength - 1);
			}
			final int objIndex = this.objectIndex++;
			this.map[hashIndex] = new MapEntry(object, objIndex,
					this.map[hashIndex]);
		}

		final int tryGetIndex(final Object object) {
			if (object == null) {
				throw new NullArgumentException("object");
			}
			final int hashCode = System.identityHashCode(object);
			final int oldLength = this.map.length;
			int hashIndex = hashCode & (oldLength - 1);
			for (MapEntry entry = this.map[hashIndex]; entry != null; entry = entry.next) {
				if (entry.object == object) {
					return entry.index;
				}
			}
			return -1;
		}

		final void reset() {
			Arrays.fill(this.map, null);
			this.size = 0;
			this.objectIndex = 0;
		}

		@Override
		public String toString() {
			final Object[] objects = new Object[this.size];
			for (MapEntry entry : this.map) {
				while (entry != null) {
					objects[entry.index] = entry.object;
					entry = entry.next;
				}
			}
			String string = "";
			int index = 0;
			for (Object object : objects) {
				string += (index++) + "\t" + object + "\n";
			}
			return string;
		}

		private MapEntry[] map;

		private int size;

		private int objectIndex;

		private static class MapEntry {

			public final Object object;

			public final int index;

			public MapEntry next;

			MapEntry(final Object object, final int index, final MapEntry next) {
				this.object = object;
				this.index = index;
				this.next = next;
			}

		}

	}

	private static final class StructStack {

		StructStack() {
			this.objectStack = new Object[4];
			this.fieldStack = new StructFieldDefineImpl[4];
		}

		final boolean isEmpty() {
			return this.stackSize == 0;
		}

		final Object getCurrentObject() {
			final Object result = this.objectStack[this.stackTopIndex];
			if (result instanceof CustomData) {
				return ((CustomData) result).value;
			}
			if (result instanceof ObjectArray) {
				return ((ObjectArray) result).getCurrentObject();
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		final void beginProcessStructObject(Object object,
				final StructFieldDefineImpl firstField,
				final DataObjectTranslator customSerializer) {
			if (firstField == null) {
				throw new NullArgumentException("firstField");
			}
			if (firstField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				object = new ObjectArray((Object[]) object);
			} else if (firstField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
				object = new CustomData(object, customSerializer);
			}
			int oldCapacity = this.objectStack.length;
			if (oldCapacity == this.stackSize) {
				final int newCapacity = oldCapacity * 2;
				final Object[] newObjectStack = new Object[newCapacity];
				final StructFieldDefineImpl[] newFieldStack = new StructFieldDefineImpl[newCapacity];
				System.arraycopy(this.objectStack, 0, newObjectStack, 0,
						oldCapacity);
				System.arraycopy(this.fieldStack, 0, newFieldStack, 0,
						oldCapacity);
				this.objectStack = newObjectStack;
				this.fieldStack = newFieldStack;
			}
			this.objectStack[this.stackSize] = object;
			this.fieldStack[this.stackSize++] = firstField;
			this.stackTopIndex++;
		}

		/**
		 * 返回执行该方法后，栈是否为空。true表示栈已空，false表示栈未空
		 */
		final boolean endProcessStructObject(final NSerializer_1_0 serializer) {
			if (this.stackSize == 0) {
				throw new IllegalStateException();
			}
			int newStackSize;
			StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			StructFieldDefineImpl oldStackTopField;
			Object oldStackTopObject;
			do {
				newStackSize = --this.stackSize;
				oldStackTopField = stackTopField;
				oldStackTopObject = this.objectStack[newStackSize];
				this.stackTopIndex--;
				this.objectStack[newStackSize] = null;
				this.fieldStack[newStackSize] = null;
				if (newStackSize == 0) {
					return true;
				}
				stackTopField = this.fieldStack[this.stackTopIndex];
				if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
					continue;
				}
				if (oldStackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
					serializer.objectIndexMap
							.tryPutObject(((CustomData) oldStackTopObject).sourceValue);
				} else if (oldStackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
					serializer.objectIndexMap
							.tryPutObject(((ObjectArray) oldStackTopObject).array);
				} else {
					serializer.objectIndexMap.tryPutObject(oldStackTopObject);
				}
				return false;
			} while (true);
		}

		final StructFieldDefineImpl beginProcessField() {
			final StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				final ObjectArray objectArray = (ObjectArray) this.objectStack[this.stackTopIndex];
				if (objectArray.offset == objectArray.array.length) {
					return null;
				}
			}
			return stackTopField;
		}

		final void endProcessField(final StructFieldDefineImpl stackTopField) {
			if (stackTopField == null) {
				return;
			}
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				((ObjectArray) this.objectStack[this.stackTopIndex - 1]).offset++;
			} else {
				this.fieldStack[this.stackTopIndex - 1] = stackTopField.nextNIOSerializableField;
			}
		}

		final void endProcessField() {
			final StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
				this.fieldStack[this.stackTopIndex] = null;
			} else if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				((ObjectArray) this.objectStack[this.stackTopIndex]).offset++;
			} else {
				this.fieldStack[this.stackTopIndex] = stackTopField.nextNIOSerializableField;
			}
		}

		final int deepness() {
			return this.stackSize;
		}

		final void reset() {
			this.stackSize = 0;
			this.stackTopIndex = -1;
		}

		private Object[] objectStack;

		private StructFieldDefineImpl[] fieldStack;

		private int stackSize;

		private int stackTopIndex;

		private static class ObjectArray {

			ObjectArray(final Object[] array) {
				if (array == null) {
					throw new NullPointerException("array");
				}
				this.array = array;
				this.offset = 0;
			}

			final Object getCurrentObject() {
				return this.array[this.offset];
			}

			final Object[] array;

			int offset;

		}

		@SuppressWarnings("unchecked")
		private static class CustomData {

			CustomData(final Object value,
					final DataObjectTranslator customSerializer) {
				if (customSerializer == null) {
					throw new NullPointerException("customSerializer");
				}
				this.value = customSerializer.toDelegateObject(value);
				this.sourceValue = value;
			}

			final Object value;

			final Object sourceValue;
		}

	}

}
