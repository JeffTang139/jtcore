package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.Undigester;


/**
 * 引用赋值器
 * 
 * @author Jeff Tang
 * 
 */
class RefDataType extends ObjectDataTypeBase {

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(this.javaClass.getName());
	}

	public static final RefDataType objectRefType = new RefDataType(
			Object.class) {
	};

	public static final RefDataType dateRefType = new RefDataType(
			java.util.Date.class) {
		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == dateRefType || another == sqlDateRefType
					|| another == DateType.TYPE) {
				return AssignCapability.SAME;
			} else if (another == LongType.TYPE) {
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType sqlDateRefType = new RefDataType(
			java.sql.Date.class) {
		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == dateRefType || another == sqlDateRefType
					|| another == DateType.TYPE) {
				return AssignCapability.SAME;
			} else if (another == LongType.TYPE) {
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType enumBaseDataType = new RefDataType(
			java.lang.Enum.class) {
	};

	public static final RefDataType booleanObjType = new RefDataType(
			Boolean.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeBooleanObject((Boolean) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeBoolean((Boolean) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readBoolean();
		}
	};

	public static final RefDataType byteObjType = new RefDataType(Byte.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object object) {
			return serializer.writeByteObject((Byte) object);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeByte((Byte) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readByte();
		}
	};

	public static final RefDataType shortObjType = new RefDataType(Short.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeShortObject((Short) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeShort((Short) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readShort();
		}
	};

	public static final RefDataType charObjType = new RefDataType(
			Character.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeCharObject((Character) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeChar((Character) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readChar();
		}
	};

	public static final RefDataType intObjType = new RefDataType(Integer.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeIntObject((Integer) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeInt((Integer) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readInt();
		}

	};

	public static final RefDataType longObjType = new RefDataType(Long.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeLongObject((Long) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeLong((Long) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readLong();
		}
	};

	public static final RefDataType floatObjType = new RefDataType(Float.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeFloatObject((Float) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeFloat((Float) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readFloat();
		}
	};

	public static final RefDataType doubleObjType = new RefDataType(
			Double.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeDoubleObject((Double) obj);
		}

		@Override
		public void writeObjectData(InternalSerializer serializer, Object obj)
				throws IOException, StructDefineNotFoundException {
			serializer.writeDouble((Double) obj);
		}

		@Override
		public Object readObjectData(InternalDeserializer deserializer)
				throws IOException, StructDefineNotFoundException {
			return deserializer.readDouble();
		}
	};

	/**
	 * 拒绝结构对象
	 */
	boolean rejectStruct;

	static {
		// 资源项
		new RefDataType(ResourceItem.class);
		new RefDataType(ZEROReadableValue.INSTANCE);
		new RefDataType(NULLReadableValue.INSTANCE);
		new RefDataType(UNKNOWNReadableValue.INSTANCE);
	}

	/**
	 * 确保该类静态数据被JVM初始
	 */
	static void ensureStaticInited() {
	}

	RefDataType(Class<?> javaClass, boolean rejectStruct) {
		super(javaClass);
		this.rejectStruct = rejectStruct;
	}

	RefDataType(Class<?> javaClass) {
		this(javaClass, false);
	}

	<TClass> RefDataType(TClass singleton) {
		this(singleton.getClass(), true);
		this.registerDataObjectTranslator(new DOT_SingletonTranslator<TClass>(
				singleton));
	}

	@Override
	public AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		if (another instanceof ObjectDataType) {
			Class<?> anotherJavaClass = ((ObjectDataType) another)
					.getJavaClass();
			if (anotherJavaClass == this.javaClass) {
				return AssignCapability.SAME;
			}
			if (this.javaClass.isAssignableFrom(anotherJavaClass)) {
				return AssignCapability.IMPLICIT;
			}
			if (anotherJavaClass.isAssignableFrom(this.javaClass)) {
				return AssignCapability.EXPLICIT;
			}
		}
		return AssignCapability.NO;
	}

	public final DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.OBJECT);
		digester.update(this.javaClass);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.OBJECT) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return DataTypeBase.dataTypeOfJavaClass(undigester
						.extractClass());
			}
		});
	}

	// // /////////////////////////////////////////
	// // Serialization

	@Override
	public boolean supportSerialization() {
		return false;
	}

	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException(obj.getClass().getName()
				+ "类型的对象不支持D＆A-Core框架定义的序列化");
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException();

	}
}
