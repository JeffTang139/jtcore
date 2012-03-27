package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * 反序列化器
 * 
 * <pre>
 *     反序列化器与序列化器以序列化协议为依据形成对应关系。
 *     用法示例：
 *         if (!unserializer.unserializeStart(fragment) {
 *             do {
 *             } while (unserializer.unserializeRest(newFragment);
 *         }
 *         Object object = unserializer.getUnserialzedObject();
 * </pre>
 * 
 * @author Jeff Tang
 */
// !!!!!!! 以下代码与序列化协议联系紧密，修改须谨慎 !!!!!!!!
public class NUnserializer_1_0 extends NUnserializer implements
		NSerializeBase_1_0 {

	@Override
	public final short getVersion() {
		return SERIALIZE_VERSION;
	}

	/**
	 * 工厂
	 */
	public final static NUnserializerFactory factory = new NUnserializerFactory(
			SERIALIZE_VERSION) {
		@Override
		public final NUnserializer newNUnserializer(
				ObjectTypeQuerier objectTypeQuerier) {
			return new NUnserializer_1_0(objectTypeQuerier);
		}

	};

	/**
	 * 构造反序列化器
	 */
	public NUnserializer_1_0(ObjectTypeQuerier objectTypeQuerier) {
		super(objectTypeQuerier);
		this.unserialized = true;
		this.rootObject = NONE_OBJECT;
	}

	/**
	 * 开始从指定的fragment中反序列化一个对象
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment中提供的字节流不够反序列化一个对象，需要更多的字节完成本次反序列化时，返回false，输入新的字节以后
	 *         ，调用unserializeRest(fragment)方法继续完成反序列化；否则说明反序列化已经完成 ，返回true
	 */
	@Override
	public final boolean unserializeStart(final DataInputFragment fragment,
			final Object destHint) {
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		if (!this.unserialized) {
			throw new IllegalStateException("当前反序列化还未完成");
		}
		this.dataInputFragment = fragment;
		this.reset();
		this.destHint = destHint;
		return this.unserialized = this.internalUnserializeStart(fragment);
	}

	/**
	 * 把从指定的fragment中反序列化当前反序列化对象未反序列化部分
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment中提供的字节流不够反序列化一个对象，需要更多的字节完成本次反序列化时，返回false，输入新的字节以后
	 *         ，再调用该方法继续完成反序列化；否则说明反序列化已经完成 ，返回true
	 */
	@Override
	public final boolean unserializeRest(final DataInputFragment fragment) {
		if (this.unserialized) {
			throw new IllegalStateException("当前反序列化已经完成");
		}
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		this.dataInputFragment = fragment;
		if (this.rootObject == NONE_OBJECT) {
			return this.unserialized = this.internalUnserializeStart(fragment);
		} else {
			if (this.structStack.isEmpty()) {
				this.rootObject = this.readObject(null);
				if (this.unserializeSuccess) {
					if (this.rootObject == UNSERIALIZABLE_OBJECT) {
						this.rootObject = this.destHint;
					}
					return this.unserialized = true;
				} else {
					return this.unserialized = false;
				}
			} else {
				while (true) {
					switch (this.processStructStack()) {
					case NEW_ELEMENT_PUSHED:
						continue;
					case BUFFER_OVERFLOW:
						return this.unserialized = false;
					case STACK_EMPTY:
						return this.unserialized = true;
					}
				}
			}
		}
	}

	/**
	 * 判断当前反序列化过程是否已经完成
	 * 
	 * @return 如果当前反序列化过程已经完成，返回true，否则返回false
	 */
	@Override
	public final boolean isUnserialized() {
		return this.rootObject != NONE_OBJECT && this.unserialized;
	}

	/**
	 * 获取当前序列化后得到的对象，调用该方法的前提是保证反序列化已完成了本次反序列化过程
	 * 
	 * @return
	 */
	@Override
	public final Object getUnserialzedObject() {
		if (this.rootObject == NONE_OBJECT || !this.unserialized) {
			throw new IllegalStateException("当前反序列化尚未完成");
		}
		return this.rootObject;
	}

	/**
	 * 重置反序列化器所有状态
	 */
	@Override
	public final void reset() {
		this.unserialized = true;
		this.rootObject = NONE_OBJECT;
		this.unserializeSuccess = false;
		this.objectReader = null;
		this.resetVLDRead();
		this.resetContinuousRead();
		this.structStack.reset();
		this.objectIndex.clear();
		this.destHint = null;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final boolean readBoolean() {
		final boolean result;
		if (this.continuousState == ContinuousState.BOOLEAN) {
			result = (byte) (this.continuousValue & 0x01) == 1 ? true : false;
			this.continuousValue >>>= 1;
			if ((this.continuousBooleanRest -= 1) == 0) {
				this.resetContinuousRead();
			}
		} else {
			if (this.continuousState != ContinuousState.NONE) {
				throw unserializeException();
			}
			if (this.dataInputFragment.remain() < SIZE_BYTE) {
				this.unserializeSuccess = false;
				return false;
			}
			final byte continuousBooleanInfo = this.dataInputFragment
					.readByte();
			result = (byte) (continuousBooleanInfo & 0x01) == 1 ? true : false;
			this.setContinuousRead(ContinuousState.BOOLEAN,
					(byte) (continuousBooleanInfo >>> 1),
					CONTINUOUS_BOOLEAN_MAX_COUNT - 1);
			this.unserializeSuccess = true;
		}
		return result;
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final byte readByte() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readByte();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final short readShort() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readShort();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final char readChar() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readChar();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final int readInt() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readInt();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final float readFloat() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readFloat();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final long readLong() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readLong();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final double readDouble() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readDouble();
		}
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final GUID readGUIDField() {
		// this.tryResetContinuousRead();
		final Object result = this.readSpecialObjectField(
				SpecialObjectFieldCallBacker.GUID, EMPTY_GUID, null);
		if (result == null) {
			return null;
		}
		if (result instanceof GUID) {
			return (GUID) result;
		}
		throw unserializeException();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final String readStringField() {
		// this.tryResetContinuousRead();
		if (this.VLDReader != null) {
			if (this.VLDReader != VLDataReader.STRING) {
				throw unserializeException();
			}
			if (this.tryReadVLD()) {
				return null;
			}
			final String result = new String((char[]) this.VLDObject);
			this.resetVLDRead();
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
		final Object result = this.readSpecialObjectField(
				SpecialObjectFieldCallBacker.STRING, EMPTY_STRING, null);
		if (result == null) {
			return null;
		}
		if (result instanceof String) {
			return (String) result;
		}
		throw unserializeException();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final long readDateField() {
		// this.tryResetContinuousRead();
		return this.readLong();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final Enum<?> readEnumField(final DataType declaredType) {
		// this.tryResetContinuousRead();
		final Object result = this.readSpecialObjectField(
				SpecialObjectFieldCallBacker.ENUM, null, declaredType);
		if (result == null) {
			return null;
		}
		if (result instanceof Enum<?>) {
			return (Enum<?>) result;
		}
		throw unserializeException();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final byte[] readByteArrayField() {
		// this.tryResetContinuousRead();
		if (this.VLDReader != null) {
			if (this.VLDReader != VLDataReader.BYTEARRAY) {
				throw unserializeException();
			}
			if (this.tryReadVLD()) {
				return null;
			}
			final byte[] result = (byte[]) this.VLDObject;
			this.resetVLDRead();
			this.unserializeSuccess = true;
			return result;
		}
		final Object result = this.readSpecialObjectField(
				SpecialObjectFieldCallBacker.BYTEARRAY, EMPTY_BYTEARRAY, null);
		if (result == null) {
			return null;
		}
		if (result instanceof byte[]) {
			return (byte[]) result;
		}
		throw unserializeException();
	}

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	@Override
	final Object readObject(final DataType declaredType) {
		if (this.customData != null) {
			Object result = this.customData;
			this.customData = null;
			return result;
		}
		if (this.continuousState == ContinuousState.NULL) {
			return this.readNull();
		}
		if (this.objectReader != null) {
			Object result = this.objectReader.read(this, declaredType);
			if (this.unserializeSuccess) {
				this.objectReader = null;
			}
			return result;
		}
		this.tryResetContinuousRead();
		byte head = this.readByte();
		if (this.unserializeSuccess) {
			switch ((byte) (head & HEAD_MASK)) {
			case HEAD_NULL:
				this.readNullHead(head);
				return null;
			case HEAD_MARK:
				switch (head) {
				case HEAD_MARK_UNSERIALIZABLE:
					return UNSERIALIZABLE_OBJECT;
				case HEAD_MARK_POINTER:
					return this.readPointer();
				default:
					throw unserializeException();
				}
			case HEAD_EMPTY:
				switch (head) {
				case HEAD_EMPTY_OBJECTARRAY0:
					return this.readObjectArray(true, true);
				case HEAD_EMPTY_OBJECTARRAY1:
					return this.readObjectArray(true, false);
				default:
					return EMPTY_OBJECTS[head & HEAD_DEMASK];
				}
			case HEAD_DATA:
				final ObjectReader objectReader = OBJECT_READERS[(byte) (head & HEAD_DEMASK)];
				Object result = objectReader.read(this, declaredType);
				if (head == HEAD_DATA_OBJECTARRAY0
						|| head == HEAD_DATA_OBJECTARRAY1) {
					this.structStack
							.beginProcessStructObject(
									result,
									StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD,
									null, (short) 0);
				} else {
					if (!this.unserializeSuccess) {
						// 只可能是变长对象的Reader
						this.objectReader = objectReader;
					}
				}
				return result;
			case HEAD_CUSTOM:
				switch (head) {
				case HEAD_CUSTOM0:
					result = this.objectIndex.get(this.readInt());
					if (result instanceof GUID) {
						final short version = this.readShort();
						final DataType dataType = DataTypeBase
								.findDataType((GUID) result);
						if (dataType == null) {
							((DataFragmentImpl) this.dataInputFragment)
									.skip(-(SIZE_HEAD + SIZE_INT + SIZE_SHORT));
							this.unserializeSuccess = false;
						} else {
							this.structStack
									.beginProcessStructObject(
											null,
											StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD,
											((ObjectDataTypeInternal) dataType)
													.getDataObjectTranslator(),
											version);
						}
						return null;
					}
					throw unserializeException();
				case HEAD_CUSTOM1:
					final GUID key = this.readGUID();
					final short version = this.readShort();
					final DataType dataType = DataTypeBase.findDataType(key);
					if (dataType == null) {
						((DataFragmentImpl) this.dataInputFragment)
								.skip(-(SIZE_HEAD + SIZE_GUID + SIZE_SHORT));
						this.objectIndex.remove(this.objectIndex.size() - 1);
						this.unserializeSuccess = false;
					} else {
						this.structStack
								.beginProcessStructObject(
										null,
										StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD,
										((ObjectDataTypeInternal) dataType)
												.getDataObjectTranslator(),
										version);
					}
					return null;
				default:
					throw unserializeException();
				}
			case HEAD_STRUCT:
				final StructDefineImpl structDefine;
				switch (head) {
				case HEAD_STRUCT0:
					if (declaredType instanceof StructDefineImpl) {
						structDefine = (StructDefineImpl) declaredType;
						this.pushToStructStack(structDefine);
						return null;
					}
					throw unserializeException();
				case HEAD_STRUCT1:
					this.pushToStructStack(this.readStructByPointer());
					return null;
				case HEAD_STRUCT2:
					structDefine = this.readStructByGUID();
					if (structDefine != null) {
						this.pushToStructStack(structDefine);
					}
					return null;
				default:
					throw unserializeException();
				}
			default:
				throw unserializeException();
			}
		}
		return null;
	}

	private final boolean internalUnserializeStart(
			final DataInputFragment fragment) {
		final Object object = this.readObject(null);
		if (this.unserializeSuccess) {
			this.rootObject = object;
			if (this.structStack.isEmpty()) {
				if (this.rootObject == UNSERIALIZABLE_OBJECT) {
					this.rootObject = this.destHint;
				}
				return this.unserialized = true;
			} else {
				while (true) {
					switch (this.processStructStack()) {
					case NEW_ELEMENT_PUSHED:
						continue;
					case BUFFER_OVERFLOW:
						return this.unserialized = false;
					case STACK_EMPTY:
						return this.unserialized = true;
					}
				}
			}
		}
		return this.unserialized = false;
	}

	private final Object readSpecialObjectField(
			final SpecialObjectFieldCallBacker callBacker,
			final Object emptyObject, final DataType declaredType) {
		if (this.continuousState == ContinuousState.NULL) {
			this.readNull();
			return null;
		} else {
			final byte head = this.readByte();
			if (this.unserializeSuccess) {
				switch ((byte) (head & HEAD_MASK)) {
				case HEAD_NULL:
					this.readNullHead(head);
					return null;
				case HEAD_MARK:
					switch (head) {
					case HEAD_MARK_POINTER:
						return this.readPointer();
					default:
						throw unserializeException();
					}
				case HEAD_EMPTY:
					return emptyObject;
				default:
					return callBacker.read(this, head, declaredType);
				}
			}
			return null;
		}
	}

	private final Object readNull() {
		if ((this.continuousValue -= 1) == 0) {
			this.resetContinuousRead();
		}
		return null;
	}

	private final Object readPointer() {
		final Object object = this.objectIndex.get(this.dataInputFragment
				.readInt());
		if (object != null) {
			this.unserializeSuccess = true;
			return object;
		}
		throw unserializeException();
	}

	private final GUID readGUID() {
		final GUID result = GUID.valueOf(this.dataInputFragment.readLong(),
				this.dataInputFragment.readLong());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final String readString() {
		final Object result = this.readVLData(VLDataReader.STRING);
		if (result == null) {
			return null;
		}
		final String string = new String((char[]) result);
		this.objectIndex.add(string);
		return string;
	}

	/**
	 * @return 如果反序列化成功，返回的是一个所有元素为null的Object数组
	 */
	private final Object[] readObjectArray(final boolean isEmptyArray,
			final boolean IDByPointer) {
		final GUID typeID;
		if (IDByPointer) {
			final Object object = this.objectIndex.get(this.dataInputFragment
					.readInt());
			if (object instanceof GUID) {
				typeID = (GUID) object;
			} else {
				throw unserializeException();
			}
		} else {
			typeID = this.readGUID();
		}
		final int arrayLength = isEmptyArray ? 0 : this.dataInputFragment
				.readInt();
		if (this.destHint != null) {
			if (!(this.destHint instanceof Object[])) {
				throw unserializeException();
			}
			final Object[] objectArray = (Object[]) this.destHint;
			this.destHint = null;
			this.unserializeSuccess = true;
			return objectArray;
		} else {
			final ArrayDataTypeBase arrayDataType = this
					.getArrayDataType(typeID);
			if (arrayDataType == null) {
				if (IDByPointer) {
					throw unserializeException();
				} else {
					((DataFragmentImpl) this.dataInputFragment)
							.skip(-(isEmptyArray ? (SIZE_HEAD + SIZE_GUID)
									: (SIZE_HEAD + SIZE_GUID + SIZE_INT)));
					this.objectIndex.remove(this.objectIndex.size() - 1);
					this.unserializeSuccess = false;
					return null;
				}
			} else {
				this.unserializeSuccess = true;
				return (Object[]) (arrayDataType.newArray(arrayLength));
			}
		}
	}

	private final Class<?> readClass() {
		final Object result = this.readVLData(VLDataReader.STRING);
		if (result == null) {
			return null;
		}
		final String className = new String((char[]) result);
		try {
			final Class<?> clazz = Class.forName(className);
			this.objectIndex.add(clazz);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw unserializeException();
		}
	}

	private final DataType readDataType(final boolean byPointer) {
		final GUID typeID;
		if (byPointer) {
			final Object result = this.objectIndex.get(this.dataInputFragment
					.readInt());
			if (result instanceof GUID) {
				typeID = (GUID) result;
				this.unserializeSuccess = true;
			}
			throw unserializeException();
		} else {
			typeID = this.readGUID();
		}
		return DataTypeBase.findDataType(typeID);
	}

	private final Enum<?> readSmallEnum(final DataType declaredType) {
		if (declaredType instanceof EnumTypeImpl<?>) {
			final Enum<?> result = ((EnumTypeImpl<?>) declaredType).enums[this.dataInputFragment
					.readByte()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
		throw unserializeException();
	}

	private final Enum<?> readSmallEnumByPointer() {
		final Object ID = this.objectIndex
				.get(this.dataInputFragment.readInt());
		if (ID instanceof GUID) {
			final Enum<?> result = this.getEnum((GUID) ID, this.readByte());
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
		throw unserializeException();
	}

	private final Enum<?> readSmallEnumByGUID() {
		final Enum<?> result = this.getEnum(this.readGUID(),
				this.dataInputFragment.readByte());
		if (result == null) {
			((DataFragmentImpl) this.dataInputFragment)
					.skip(-(SIZE_HEAD + SIZE_GUID));
			this.objectIndex.remove(this.objectIndex.size() - 1);
			this.unserializeSuccess = false;
		} else {
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
		}
		return result;
	}

	private final Enum<?> readLargeEnum(final DataType declaredType) {
		if (declaredType instanceof EnumTypeImpl<?>) {
			final Enum<?> result = ((EnumTypeImpl<?>) declaredType).enums[this.dataInputFragment
					.readInt()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
		throw unserializeException();
	}

	private final Enum<?> readLargeEnumByPointer() {
		final Object ID = this.objectIndex.get(this.readInt());
		if (ID instanceof GUID) {
			final Enum<?> result = this.getEnum((GUID) ID,
					this.dataInputFragment.readInt());
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
		throw unserializeException();
	}

	private final Enum<?> readLargeEnumByGUID() {
		final Enum<?> result = this.getEnum(this.readGUID(),
				this.dataInputFragment.readInt());
		if (result == null) {
			((DataFragmentImpl) this.dataInputFragment)
					.skip(-(SIZE_HEAD + SIZE_GUID));
			this.objectIndex.remove(this.objectIndex.size() - 1);
			this.unserializeSuccess = false;
		} else {
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
		}
		return result;
	}

	private final StructDefineImpl readStructByPointer() {
		final Object object = this.readPointer();
		if (this.unserializeSuccess) {
			if (object instanceof GUID) {
				return this.getStructDefine((GUID) object);
			}
			throw unserializeException();
		}
		return null;
	}

	private final StructDefineImpl readStructByGUID() {
		final GUID structID = this.readGUID();
		if (this.unserializeSuccess) {
			final StructDefineImpl structDefine = this
					.getStructDefine(structID);
			if (structDefine == null) {
				((DataFragmentImpl) this.dataInputFragment)
						.skip(-(SIZE_HEAD + SIZE_GUID));
				this.objectIndex.remove(this.objectIndex.size() - 1);
				this.unserializeSuccess = false;
			}
			return structDefine;
		}
		return null;
	}

	private final Boolean readBooleanObject() {
		final Boolean result = new Boolean(
				this.dataInputFragment.readByte() == 1);
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Byte readByteObject() {
		final Byte result = new Byte(this.dataInputFragment.readByte());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Character readCharObject() {
		final Character result = new Character(this.dataInputFragment
				.readChar());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Short readShortObject() {
		final Short result = new Short(this.dataInputFragment.readShort());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Integer readIntObject() {
		final Integer result = new Integer(this.dataInputFragment.readInt());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Long readLongObject() {
		final Long result = new Long(this.dataInputFragment.readLong());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Float readFloatObject() {
		final Float result = new Float(this.dataInputFragment.readFloat());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final Double readDoubleObject() {
		final Double result = new Double(this.dataInputFragment.readDouble());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	private final void readNullHead(final byte nullHead) {
		final byte continuousNullCount = (byte) (nullHead & HEAD_DEMASK);
		switch (continuousNullCount) {
		case 0:
			throw unserializeException();
		case 1:
			break;
		default:
			this.setContinuousRead(ContinuousState.NULL,
					(byte) (continuousNullCount - 1), 0);
		}
	}

	private final Object readVLData(final VLDataReader VLDReader) {
		if (this.VLDReader != null) {
			if (this.VLDReader != VLDReader) {
				throw unserializeException();
			}
		} else {
			final int VLDLength = this.dataInputFragment.readInt();
			this.setVLDRead(VLDReader, VLDReader.newVLDObject(VLDLength),
					VLDLength);
		}
		if (this.tryReadVLD()) {
			this.unserializeSuccess = false;
			return null;
		}
		final Object result = this.VLDObject;
		this.resetVLDRead();
		this.unserializeSuccess = true;
		return result;
	}

	private final void pushToStructStack(final StructDefineImpl structDefine) {
		final Object object;
		if (this.destHint != null) {
			object = this.destHint;
			this.destHint = null;
			if (!structDefine.soClass.isInstance(object)) {
				throw unserializeException();
			}
		} else {
			object = structDefine.newEmptySO();
		}
		this.structStack.beginProcessStructObject(object, structDefine
				.getFirstNIOSerializableField(), null, (short) 0);
	}

	private final StackQuitState processStructStack() {
		Object object = null;
		int structStackDeepness = this.structStack.deepness();
		for (;;) {
			final StructFieldDefineImpl field = this.structStack
					.beginProcessField();
			if (field == null) {
				if (this.structStack.endProcessStructObject(this, object)) {
					return StackQuitState.STACK_EMPTY;
				} else {
					object = null;
					structStackDeepness = this.structStack.deepness();
					continue;
				}
			} else if (field == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD
					|| field == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				if ((object = this.readObject(null)) == UNSERIALIZABLE_OBJECT) {
					object = null;
				}
			} else {
				if (object == null) {
					object = this.structStack.getCurrentObject();
				}
				field.unserialize(this, object);
			}
			if (structStackDeepness != this.structStack.deepness()) {
				return StackQuitState.NEW_ELEMENT_PUSHED;
			}
			if (this.unserializeSuccess) {
				this.structStack.endProcessField(object);
			} else {
				return StackQuitState.BUFFER_OVERFLOW;
			}
		}
	}

	private final void resetVLDRead() {
		this.VLDLength = 0;
		this.VLDReadCount = 0;
		this.VLDReader = null;
	}

	private final void resetContinuousRead() {
		this.continuousState = ContinuousState.NONE;
		this.continuousValue = 0;
		this.continuousBooleanRest = 0;
	}

	private final void tryResetContinuousRead() {
		if (this.continuousState != ContinuousState.NONE) {
			this.continuousState = ContinuousState.NONE;
			this.continuousValue = 0;
			this.continuousBooleanRest = 0;
		}
	}

	private final void setVLDRead(final VLDataReader VLDReader,
			final Object VLDObject, final int VLDLength) {
		this.VLDReader = VLDReader;
		this.VLDObject = VLDObject;
		this.VLDLength = VLDLength;
	}

	private final void setContinuousRead(final ContinuousState continuousState,
			final byte continuousValue, final int continuousBooleanRest) {
		this.continuousState = continuousState;
		this.continuousValue = continuousValue;
		this.continuousBooleanRest = continuousBooleanRest;
	}

	/**
	 * @return 返回true表示尚未完成数据的反序列化
	 */
	private final boolean tryReadVLD() {
		if (this.VLDReader != null) {
			if (this.VLDReadCount != this.VLDLength) {
				this.VLDReadCount = this.VLDReader.readElement(this,
						this.dataInputFragment, this.VLDObject,
						this.VLDReadCount, this.VLDLength);
				if (this.VLDReadCount != this.VLDLength) {
					return true;
				}
			}
			if (this.VLDReader != VLDataReader.STRING) {
				if (this.VLDReader == VLDataReader.BOOLEANARRAY) {
					this.resetContinuousRead();
				}
				this.objectIndex.add(this.VLDObject);
			}
			return false;
		}
		throw unserializeException();
	}

	private final StructDefineImpl getStructDefine(final GUID structTypeID) {
		final DataType dataType = this.tryGetDataType(structTypeID);
		if (dataType instanceof StructDefineImpl) {
			return (StructDefineImpl) dataType;
		}
		if (dataType == null) {
			// 本机没找到对应数据类型的情况
			return null;
		}
		throw unserializeException();
	}

	private final Enum<?> getEnum(final GUID enumTypeID, final int enumOrdinal) {
		final DataType dataType = this.tryGetDataType(enumTypeID);
		if (dataType instanceof EnumTypeImpl<?>) {
			final EnumTypeImpl<?> enumType = (EnumTypeImpl<?>) dataType;
			return enumType.enums[enumOrdinal];
		}
		if (dataType == null) {
			return null;
		}
		throw unserializeException();
	}

	private final ArrayDataTypeBase getArrayDataType(final GUID typeID) {
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType instanceof ArrayDataTypeBase) {
			return (ArrayDataTypeBase) dataType;
		}
		if (dataType == null) {
			return null;
		}
		throw unserializeException();
	}

	private static final RuntimeException unserializeException() {
		return new RuntimeException("反序列化异常");
	}

	private final ArrayList<Object> objectIndex = new ArrayList<Object>();

	private final StructStack structStack = new StructStack();

	private boolean unserialized;

	private boolean unserializeSuccess;

	private DataInputFragment dataInputFragment;

	private Object rootObject;

	private Object destHint;

	private ObjectReader objectReader;

	private ContinuousState continuousState;

	private byte continuousValue;

	private int continuousBooleanRest;

	private VLDataReader VLDReader;

	private Object VLDObject;

	private int VLDLength;

	private int VLDReadCount;

	private Object customData;

	private static final ObjectReader[] OBJECT_READERS = ObjectReader.values();

	/**
	 * 对于变长的数据调完回调方法之后都要判断，是否完全结束
	 */
	private enum ObjectReader {

		GUID {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readGUID();
			}
		},

		STRING {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readString();
			}
		},

		BOOLEANARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.BOOLEANARRAY);
			}
		},

		BYTEARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.BYTEARRAY);
			}
		},

		SHORTARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.SHORTARRAY);
			}
		},

		CHARARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.CHARARRAY);
			}
		},

		INTARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.INTARRAY);
			}
		},

		FLOATARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.FLOATARRAY);
			}
		},

		LONGARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.LONGARRAY);
			}
		},

		DOUBLEARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readVLData(VLDataReader.DOUBLEARRAY);
			}
		},

		OBJECTARRAY0 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readObjectArray(false, true);
			}
		},

		OBJECTARRAY1 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readObjectArray(false, false);
			}
		},

		CLASS {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readClass();
			}
		},

		TYPE0 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readDataType(true);
			}
		},

		TYPE1 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readDataType(false);
			}
		},

		SMALLENUM0 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				throw unserializeException();
			}
		},

		SMALLENUM1 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readSmallEnumByPointer();
			}
		},

		SMALLENUM2 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readSmallEnumByGUID();
			}
		},

		LARGEENUM0 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				throw unserializeException();
			}
		},

		LARGEENUM1 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readLargeEnumByPointer();
			}
		},

		LARGEENUM2 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readLargeEnumByGUID();
			}
		},

		BOOLEANOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readBooleanObject();
			}
		},

		BYTEOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readByteObject();
			}
		},

		CHAROBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readCharObject();
			}
		},

		SHORTOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readShortObject();
			}
		},

		INTOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readIntObject();
			}
		},

		LONGOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readLongObject();
			}
		},

		FLOATOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readFloatObject();
			}
		},

		DOUBLEOBJECT {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readDoubleObject();
			}
		},

		POINTER {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readPointer();
			}
		},

		STRUCT1 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readStructByPointer();
			}
		},

		STRUCT2 {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final DataType declaredType) {
				return unserializer.readStructByGUID();
			}
		};

		abstract Object read(final NUnserializer_1_0 unserializer,
				final DataType declaredType);

	}

	private enum VLDataReader {

		BOOLEANARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new boolean[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final boolean[] array = (boolean[]) data;
				final int bufferRemain = dataInputFragment.remain() * 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = unserializer.readBoolean();
				}
				return canWriteToIndex;
			}
		},

		BYTEARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new byte[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final byte[] array = (byte[]) data;
				final int bufferRemain = dataInputFragment.remain();
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readByte();
				}
				return canWriteToIndex;
			}
		},

		SHORTARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new short[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final short[] array = (short[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readShort();
				}
				return canWriteToIndex;
			}
		},

		CHARARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new char[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readChar();
				}
				return canWriteToIndex;
			}
		},

		INTARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new int[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final int[] array = (int[]) data;
				final int bufferRemain = dataInputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readInt();
				}
				return canWriteToIndex;
			}
		},

		FLOATARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new float[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final float[] array = (float[]) data;
				final int bufferRemain = dataInputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readFloat();
				}
				return canWriteToIndex;
			}
		},

		LONGARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new long[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final long[] array = (long[]) data;
				final int bufferRemain = dataInputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readLong();
				}
				return canWriteToIndex;
			}
		},

		DOUBLEARRAY {
			@Override
			Object newVLDObject(int VLDLength) {
				return new double[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final double[] array = (double[]) data;
				final int bufferRemain = dataInputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readDouble();
				}
				return canWriteToIndex;
			}
		},

		STRING {
			@Override
			Object newVLDObject(int VLDLength) {
				return new char[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer_1_0 unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex
						+ bufferRemain
						: length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readChar();
				}
				return canWriteToIndex;
			}
		};

		abstract Object newVLDObject(final int VLDLength);

		/**
		 * @return 返回读取了多少个元素
		 */
		abstract int readElement(final NUnserializer_1_0 unserializer,
				final DataInputFragment dataInputFragment, Object data,
				final int startIndex, final int length);

	}

	private enum SpecialObjectFieldCallBacker {

		GUID {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final byte head, final DataType declaredType) {
				if (head == HEAD_DATA_GUID) {
					return unserializer.readGUID();
				} else {
					throw unserializeException();
				}
			}
		},

		STRING {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final byte head, final DataType declaredType) {
				if (head == HEAD_DATA_STRING) {
					return unserializer.readString();
				} else {
					throw unserializeException();
				}
			}
		},

		ENUM {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final byte head, final DataType declaredType) {
				switch (head) {
				case HEAD_DATA_SMALLENUM0:
					return unserializer.readSmallEnum(declaredType);
				case HEAD_DATA_SMALLENUM1:
					return unserializer.readSmallEnumByPointer();
				case HEAD_DATA_SMALLENUM2:
					return unserializer.readSmallEnumByGUID();
				case HEAD_DATA_LARGEENUM0:
					return unserializer.readLargeEnum(declaredType);
				case HEAD_DATA_LARGEENUM1:
					return unserializer.readLargeEnumByPointer();
				case HEAD_DATA_LARGEENUM2:
					return unserializer.readLargeEnumByGUID();
				default:
					throw unserializeException();
				}
			}
		},

		BYTEARRAY {
			@Override
			final Object read(final NUnserializer_1_0 unserializer,
					final byte head, final DataType declaredType) {
				if (head == HEAD_DATA_BYTEARRAY) {
					return unserializer.readVLData(VLDataReader.BYTEARRAY);
				} else {
					throw unserializeException();
				}
			}
		};

		abstract Object read(final NUnserializer_1_0 unserializer,
				final byte head, final DataType declaredType);

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
			if (result instanceof InternalObject) {
				throw new UnsupportedOperationException();
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		final void beginProcessStructObject(Object object,
				final StructFieldDefineImpl firstField,
				final DataObjectTranslator customSerializer, final short version) {
			if (firstField == null) {
				throw new NullArgumentException("firstField");
			}
			if (firstField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				object = new ObjectArray((Object[]) object);
			} else if (firstField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
				object = new CustomData(customSerializer, version);
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
		@SuppressWarnings("unchecked")
		final boolean endProcessStructObject(
				final NUnserializer_1_0 unserializer, Object value) {
			if (this.stackSize == 0) {
				throw new IllegalStateException();
			}
			StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			int topIndex;
			for (;;) {
				topIndex = this.stackTopIndex;
				if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
					do {
						CustomData customData = (CustomData) this.objectStack[topIndex];
						// TODO 通过上下文传入一个对象查询器
						this.objectStack[topIndex] = null;
						this.fieldStack[topIndex] = null;
						this.stackTopIndex--;
						if ((--this.stackSize) == 0) {
							value = customData.customSerializer.recoverObject(
									unserializer.destHint, value, null,
									customData.version);
							unserializer.destHint = null;
							unserializer.objectIndex
									.add(unserializer.rootObject = value);
							return true;
						} else {
							value = customData.customSerializer.recoverObject(
									null, value, null, customData.version);
						}
						topIndex = this.stackTopIndex;
					} while ((stackTopField = this.fieldStack[this.stackTopIndex]) == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD);
					if (unserializer.customData != null) {
						throw new IllegalStateException();
					}
					unserializer.objectIndex
							.add(unserializer.customData = value);
					return false;
				}
				if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
					value = ((ObjectArray) this.objectStack[topIndex]).array;
				}
				this.objectStack[topIndex] = null;
				this.fieldStack[topIndex] = null;
				this.stackTopIndex--;
				if ((--this.stackSize) == 0) {
					unserializer.objectIndex
							.add(unserializer.rootObject = value);
					return true;
				}
				stackTopField = this.fieldStack[this.stackTopIndex];
				if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
					continue;
				}
				if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
					ObjectArray objectArray = (ObjectArray) this.objectStack[this.stackTopIndex];
					objectArray.array[objectArray.offset++] = value;
				} else {
					if (unserializer.customData != null) {
						throw new IllegalStateException();
					}
					unserializer.customData = value;
				}
				unserializer.objectIndex.add(value);
				return false;
			}
		}

		final StructFieldDefineImpl beginProcessField() {
			final StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD
					&& ((CustomData) this.objectStack[this.stackTopIndex]).serialized == true) {
				return null;
			}
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				final ObjectArray objectArray = (ObjectArray) this.objectStack[this.stackTopIndex];
				if (objectArray.offset == objectArray.array.length) {
					return null;
				}
			}
			return stackTopField;
		}

		final void endProcessField(final Object value) {
			final StructFieldDefineImpl stackTopField = this.fieldStack[this.stackTopIndex];
			if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_CUSTOMDATA_FIELD) {
				((CustomData) this.objectStack[this.stackTopIndex]).serialized = true;
			} else if (stackTopField == StructFieldDefineImpl.NIOSERIALIZE_OBJECTARRAY_FIELD) {
				ObjectArray objectArray = (ObjectArray) this.objectStack[this.stackTopIndex];
				objectArray.array[objectArray.offset++] = value;
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

		private static abstract class InternalObject {

		}

		private static class ObjectArray extends InternalObject {

			ObjectArray(final Object[] array) {
				if (array == null) {
					throw new NullPointerException("array");
				}
				this.array = array;
				this.offset = 0;
			}

			final Object[] array;

			int offset;

		}

		@SuppressWarnings("unchecked")
		private static class CustomData extends InternalObject {

			CustomData(final DataObjectTranslator customSerializer,
					final short version) {
				if (customSerializer == null) {
					throw new NullPointerException("customSerializer");
				}
				this.customSerializer = customSerializer;
				this.version = version;
			}

			boolean serialized = false;

			final DataObjectTranslator customSerializer;

			final short version;

		}

	}

}
