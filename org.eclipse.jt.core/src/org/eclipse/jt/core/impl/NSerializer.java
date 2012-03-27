package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


/**
 * 序列化器抽象类
 */
public abstract class NSerializer {

	/**
	 * 序列化器工厂
	 */
	public static abstract class NSerializerFactory {
		/**
		 * 获得版本号
		 */
		public final short version;

		public final boolean remoteVersionCompatible(short remoteVersion) {
			return this.version <= remoteVersion;
		}

		/**
		 * 创建序列化器
		 */
		public abstract NSerializer newNSerializer();

		NSerializerFactory(short version) {
			this.version = version;
		}
	}

	/**
	 * 必须按照版本号从大到小的顺序排放
	 */
	private static NSerializerFactory[] factorys = new NSerializerFactory[] { NSerializer_1_0.factory };

	/**
	 * 按要求的版本号查找最接近版本的序列化器工厂
	 */
	public static NSerializerFactory getRemoteCompatibleFactory(
			short remoteVersion) {
		for (NSerializerFactory factory : factorys) {
			if (factory.remoteVersionCompatible(remoteVersion)) {
				return factory;
			}
		}
		throw new IllegalArgumentException(
				"无法取得与远程节点兼容的序列化器，远程序列化协议版本过低: version(" + remoteVersion + ")");
	}

	/**
	 * 获得序列化器工厂最大版本号
	 */
	public static short getHighestSerializeVersion() {
		return factorys[0].version;
	}

	/**
	 * 获得序列化器版本号
	 */
	public abstract short getVersion();

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
	public abstract boolean serializeStart(final Object object,
			final DataOutputFragment fragment);

	/**
	 * 把当前序列化对象未序列化部分序列化到指定的fragment中
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment不够装下object序列化后产生的字节流，需要更多的fragment完成序列化时，返回false，
	 *         申请新fragment以后 ，再调用该方法继续完成序列化；否则说明序列化已经完成 ，返回true
	 */
	public abstract boolean serializeRest(final DataOutputFragment fragment);

	/**
	 * 重置序列化器所有状态
	 */
	public abstract void reset();

	/**
	 * 判断当前对象序列化是否完成
	 * 
	 * @return 如果当前对象序列化已经完成，则返回true，否则返回false
	 */
	public abstract boolean isSerialized();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeBoolean(final boolean value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeByte(final byte value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeShort(final short value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeChar(final char value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeInt(final int value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeFloat(final float value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeLong(final long value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeDouble(final double value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeGUIDField(final GUID value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeStringField(final String value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeDateField(final long value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeEnumField(final Enum<?> value,
			EnumTypeImpl<?> enumType);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeByteArrayField(final byte[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeObject(final Object value, final DataType declaredType);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeUnserializable();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeGUIDData(final GUID value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeEnumData(final Enum<?> value,
			EnumTypeImpl<?> enumType, boolean declared);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeClassData(final Class<?> value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeBooleanArrayData(final boolean[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeByteArrayData(final byte[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeShortArrayData(final short[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeCharArrayData(final char[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeIntArrayData(final int[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeFloatArrayData(final float[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeLongArrayData(final long[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeDoubleArrayData(final double[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeObjectArrayData(final ObjectArrayDataType arrayType,
			final Object[] value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeStringData(final String value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeStructData(final Object value,
			final StructDefineImpl structDefine, boolean declared);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeCustomSerializeDataObject(final Object value,
			ObjectDataType type, DataObjectTranslator<?, ?> serializer);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeBooleanObject(final Boolean value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeByteObject(final Byte value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeCharObject(final Character value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeShortObject(final Short value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeIntObject(final Integer value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeLongObject(final Long value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeFloatObject(final Float value);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean writeDoubleObject(final Double value);

}
