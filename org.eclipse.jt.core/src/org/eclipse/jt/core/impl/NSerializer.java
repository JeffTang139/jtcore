package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.serial.DataObjectTranslator;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


/**
 * ���л���������
 */
public abstract class NSerializer {

	/**
	 * ���л�������
	 */
	public static abstract class NSerializerFactory {
		/**
		 * ��ð汾��
		 */
		public final short version;

		public final boolean remoteVersionCompatible(short remoteVersion) {
			return this.version <= remoteVersion;
		}

		/**
		 * �������л���
		 */
		public abstract NSerializer newNSerializer();

		NSerializerFactory(short version) {
			this.version = version;
		}
	}

	/**
	 * ���밴�հ汾�ŴӴ�С��˳���ŷ�
	 */
	private static NSerializerFactory[] factorys = new NSerializerFactory[] { NSerializer_1_0.factory };

	/**
	 * ��Ҫ��İ汾�Ų�����ӽ��汾�����л�������
	 */
	public static NSerializerFactory getRemoteCompatibleFactory(
			short remoteVersion) {
		for (NSerializerFactory factory : factorys) {
			if (factory.remoteVersionCompatible(remoteVersion)) {
				return factory;
			}
		}
		throw new IllegalArgumentException(
				"�޷�ȡ����Զ�̽ڵ���ݵ����л�����Զ�����л�Э��汾����: version(" + remoteVersion + ")");
	}

	/**
	 * ������л����������汾��
	 */
	public static short getHighestSerializeVersion() {
		return factorys[0].version;
	}

	/**
	 * ������л����汾��
	 */
	public abstract short getVersion();

	/**
	 * ��ʼ���л�һ������ָ����fragment��
	 * 
	 * @param object
	 *            ����
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment����װ��object���л���������ֽ�������Ҫ�����fragment������л�ʱ������false��
	 *         ������fragment�Ժ� ������serializeRest(fragment)��������������л�������˵�����л��Ѿ����
	 *         ������true
	 */
	public abstract boolean serializeStart(final Object object,
			final DataOutputFragment fragment);

	/**
	 * �ѵ�ǰ���л�����δ���л��������л���ָ����fragment��
	 * 
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment����װ��object���л���������ֽ�������Ҫ�����fragment������л�ʱ������false��
	 *         ������fragment�Ժ� ���ٵ��ø÷�������������л�������˵�����л��Ѿ���� ������true
	 */
	public abstract boolean serializeRest(final DataOutputFragment fragment);

	/**
	 * �������л�������״̬
	 */
	public abstract void reset();

	/**
	 * �жϵ�ǰ�������л��Ƿ����
	 * 
	 * @return �����ǰ�������л��Ѿ���ɣ��򷵻�true�����򷵻�false
	 */
	public abstract boolean isSerialized();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeBoolean(final boolean value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeByte(final byte value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeShort(final short value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeChar(final char value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeInt(final int value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeFloat(final float value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeLong(final long value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeDouble(final double value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeGUIDField(final GUID value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeStringField(final String value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeDateField(final long value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeEnumField(final Enum<?> value,
			EnumTypeImpl<?> enumType);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeByteArrayField(final byte[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeObject(final Object value, final DataType declaredType);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeUnserializable();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeGUIDData(final GUID value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeEnumData(final Enum<?> value,
			EnumTypeImpl<?> enumType, boolean declared);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeClassData(final Class<?> value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeBooleanArrayData(final boolean[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeByteArrayData(final byte[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeShortArrayData(final short[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeCharArrayData(final char[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeIntArrayData(final int[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeFloatArrayData(final float[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeLongArrayData(final long[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeDoubleArrayData(final double[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeObjectArrayData(final ObjectArrayDataType arrayType,
			final Object[] value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeStringData(final String value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeStructData(final Object value,
			final StructDefineImpl structDefine, boolean declared);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeCustomSerializeDataObject(final Object value,
			ObjectDataType type, DataObjectTranslator<?, ?> serializer);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeBooleanObject(final Boolean value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeByteObject(final Byte value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeCharObject(final Character value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeShortObject(final Short value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeIntObject(final Integer value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeLongObject(final Long value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeFloatObject(final Float value);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean writeDoubleObject(final Double value);

}
