package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


public abstract class NUnserializer {

	public NUnserializer(ObjectTypeQuerier objectTypeQuerier) {
		if (objectTypeQuerier == null) {
			throw new NullArgumentException("TypeQuerier");
		}
		this.objectTypeQuerier = objectTypeQuerier;
	}

	/**
	 * �����л�������
	 */
	public static abstract class NUnserializerFactory {
		/**
		 * ��ð汾��
		 */
		public final short version;

		/**
		 * ���������л���
		 */
		public abstract NUnserializer newNUnserializer(
				ObjectTypeQuerier objectTypeQuerier);

		NUnserializerFactory(short version) {
			this.version = version;
		}
	}

	/**
	 * ���밴�հ汾�ŴӴ�С��˳���ŷ�
	 */
	private static NUnserializerFactory[] factorys = new NUnserializerFactory[] { NUnserializer_1_0.factory };

	/**
	 * ��Ҫ��ķ������л�������
	 */
	public static NUnserializerFactory findUnserializerFactory(
			short requiredVersion) {
		for (NUnserializerFactory factory : factorys) {
			if (factory.version == requiredVersion) {
				return factory;
			}
		}
		return null;
	}

	public static NUnserializer newUnserializer(short requiredVersion,
			ObjectTypeQuerier objectTypeQuerier) {
		for (NUnserializerFactory factory : factorys) {
			if (factory.version == requiredVersion) {
				return factory.newNUnserializer(objectTypeQuerier);
			}
		}
		throw new UnsupportedOperationException("��֧�ְ汾Ϊ"
				+ Integer.toHexString(requiredVersion) + "�ķ����л�����");
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
	 * �жϵ�ǰ�����л������Ƿ��Ѿ����
	 * 
	 * @return �����ǰ�����л������Ѿ���ɣ�����true�����򷵻�false
	 */
	public abstract boolean isUnserialized();

	/**
	 * ���÷����л�������״̬
	 */
	public abstract void reset();

	/**
	 * ��ʼ��ָ����fragment�з����л�һ������
	 * 
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment���ṩ���ֽ������������л�һ��������Ҫ������ֽ���ɱ��η����л�ʱ������false�������µ��ֽ��Ժ�
	 *         ������unserializeRest(fragment)����������ɷ����л�������˵�������л��Ѿ���� ������true
	 */
	public abstract boolean unserializeStart(final DataInputFragment fragment,
			Object destHint);

	/**
	 * �Ѵ�ָ����fragment�з����л���ǰ�����л�����δ�����л�����
	 * 
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment���ṩ���ֽ������������л�һ��������Ҫ������ֽ���ɱ��η����л�ʱ������false�������µ��ֽ��Ժ�
	 *         ���ٵ��ø÷���������ɷ����л�������˵�������л��Ѿ���� ������true
	 */
	public abstract boolean unserializeRest(final DataInputFragment fragment);

	/**
	 * ��ȡ��ǰ���л���õ��Ķ��󣬵��ø÷�����ǰ���Ǳ�֤�����л�������˱��η����л�����
	 */
	public abstract Object getUnserialzedObject();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean readBoolean();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract byte readByte();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract short readShort();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract char readChar();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract int readInt();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract float readFloat();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract long readLong();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract double readDouble();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract GUID readGUIDField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract String readStringField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract long readDateField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Enum<?> readEnumField(final DataType declaredType);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract byte[] readByteArrayField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readObject(final DataType declaredType);

	static final Object UNSERIALIZABLE_OBJECT = new Object();

	/**
	 * �������ͻ�ȡ��
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface ObjectTypeQuerier {
		/**
		 * ����null��ʾ��Ҫ��������
		 * 
		 * @param typeID
		 * @return ����null��ʾ��Ҫ��������
		 */
		public DataType findElseAsync(GUID typeID);

		public static final ObjectTypeQuerier staticObjectTypeQuerier = new ObjectTypeQuerier() {

			public final DataType findElseAsync(GUID typeID) {
				final DataType dt = DataTypeBase.findDataType(typeID);
				if (dt == null) {
					throw new MissingObjectException("�Ҳ���IDΪ[" + typeID
							+ "]�ľ�̬����");
				}
				return dt;
			}
		};
	}

	private final ObjectTypeQuerier objectTypeQuerier;

	protected DataType tryGetDataType(GUID typeID) {
		return this.objectTypeQuerier.findElseAsync(typeID);
	}

}
