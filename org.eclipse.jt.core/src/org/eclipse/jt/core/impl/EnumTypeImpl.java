package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * 枚举类型
 * 
 * @author Jeff Tang
 * 
 * @param <TEnum>
 */
public final class EnumTypeImpl<TEnum extends Enum<TEnum>> extends
		ObjectDataTypeBase implements EnumType<TEnum> {

	@Override
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inEnum(DataType to, EnumType<?> type)
				throws Throwable {
			if (to == type) {
				return AssignCapability.SAME;
			}
			return AssignCapability.NO;
		}

	};

	public final DataType calcPrecedence(DataType target) {
		throw new UnsupportedOperationException();
	}

	final static boolean ordinalSupport(Type rootType) {
		// FIXME
		return rootType == LongType.TYPE || rootType == IntType.TYPE
				|| rootType == ShortType.TYPE || rootType == ByteType.TYPE;
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.ENUM_H);
		digester.update(this.javaClass);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.ENUM_H) {
			@SuppressWarnings("unchecked")
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return ENUM(undigester.extractClass());
			}
		});
	}

	@Override
	public String toString() {
		return "enum:".concat(this.javaClass.getSimpleName());
	}

	@Override
	final void regThisDataTypeInConstructor() {
	}

	final TEnum[] enums;

	private EnumTypeImpl(Class<TEnum> enumClass) {
		super(enumClass);
		this.enums = enumClass.getEnumConstants();
		regDataType(this);
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inEnum(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final TEnum getEnum(int ordinal) {
		return this.enums[ordinal];
	}

	@SuppressWarnings("unchecked")
	public final TEnum getEnum(String name) {
		return Enum.valueOf((Class<TEnum>) this.javaClass, name);
	}

	@SuppressWarnings("unchecked")
	public final Class<TEnum> getEnumClass() {
		return (Class<TEnum>) this.javaClass;
	}

	public int getCount() {
		return this.enums.length;
	}

	@SuppressWarnings("unchecked")
	public static <TEnum extends Enum<TEnum>> EnumTypeImpl<TEnum> ENUM(
			Class<TEnum> enumClass) {
		if (enumClass == null) {
			throw new NullArgumentException("enumClass");
		}
		final Class s = enumClass.getSuperclass();
		if (s != Enum.class) {
			enumClass = s;
		}
		int mv;
		EnumTypeImpl type;
		enumTypeMapRL.lock();
		try {
			mv = modifyVersion;
			type = enumTypes.get(enumClass);
			if (type != null) {
				return type;
			}
		} finally {
			enumTypeMapRL.unlock();
		}
		enumTypeMapWL.lock();
		try {
			if (modifyVersion != mv) {
				type = enumTypes.get(enumClass);
				if (type != null) {
					return type;
				}
			}
			type = new EnumTypeImpl<TEnum>(enumClass);
			enumTypes.put(enumClass, type);
			modifyVersion++;
			return type;
		} finally {
			enumTypeMapWL.unlock();
		}
	}

	private static final ReentrantReadWriteLock.ReadLock enumTypeMapRL;
	private static final ReentrantReadWriteLock.WriteLock enumTypeMapWL;
	private static volatile int modifyVersion;
	private final static HashMap<Class<?>, EnumTypeImpl<?>> enumTypes = new HashMap<Class<?>, EnumTypeImpl<?>>();

	static {
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		enumTypeMapRL = rwl.readLock();
		enumTypeMapWL = rwl.writeLock();
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	protected final GUID calcTypeID() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.javaClass.getName()).append('{');
		for (TEnum e : this.enums) {
			sb.append(e.name());
			sb.append(',');
		}
		sb.append('}');
		return calcNativeTypeID(sb);
	}

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeEnumData((Enum<?>) object, this, false);
	}

}
