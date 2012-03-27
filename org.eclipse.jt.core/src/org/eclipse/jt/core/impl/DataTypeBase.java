package org.eclipse.jt.core.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.StaticStructDefineImpl.ETryLoadJavaFieldsAndPrepareAccessInfo;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * 非结构数据类型的实现基类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class DataTypeBase implements DataTypeInternal {
	final Class<?> javaClass;

	public final Class<?> getJavaClass() {
		return this.javaClass;
	}

	public Class<?> getRegClass() {
		return this.javaClass;
	}

	@Override
	public String toString() {
		return this.javaClass != null ? this.javaClass.getName() : null;

	};

	public DataTypeInternal getRootType() {
		return this;
	}

	private ArrayDataTypeBase arrayOf;

	public final ArrayDataTypeBase arrayOf() {
		final ArrayDataTypeBase a = this.arrayOf;
		if (a != null) {
			return a;
		}
		if (this.javaClass == null) {
			throw new UnsupportedOperationException(this.toString()
					+ "类型不支持其数组类型");
		}
		return this.arrayOf = arrayOf(this);
	}

	public final void setArrayOf(ArrayDataTypeBase type) {
		if (type == null) {
			throw new NullArgumentException("type");
		}
		if (this.arrayOf != null) {
			throw new UnsupportedOperationException("\"" + this + "\"的数组类型\""
					+ this.arrayOf + "\"已经存在，不可以重复设置:\"" + type + "\"");
		}
		this.arrayOf = type;
	}

	void regThisDataTypeInConstructor() {
		regDataType(this);
	}

	DataTypeBase(Class<?> javaClass) {
		this.javaClass = javaClass;
		this.regThisDataTypeInConstructor();
	}

	private static final ReentrantReadWriteLock.ReadLock dataTypeMapRL;
	private static final ReentrantReadWriteLock.WriteLock dataTypeMapWL;
	private static volatile int modifiedVersion;
	private static final HashMap<GUID, DataTypeInternal> dataTypesByGUID = new HashMap<GUID, DataTypeInternal>();
	private static final HashMap<Class<?>, DataTypeInternal> dataTypesByClass = new HashMap<Class<?>, DataTypeInternal>();
	private static final HashMap<String, StaticStructDefineImpl> staticStructDefineByName = new HashMap<String, StaticStructDefineImpl>();

	static {
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		dataTypeMapRL = rwl.readLock();
		dataTypeMapWL = rwl.writeLock();
		TypeFactory.ensureStaticInited();
		// 各引用类型
		RefDataType.ensureStaticInited();
		// 各列表类型
		ListDataType.ensureStaticInited();
	}

	final static ArrayDataTypeBase arrayOf(DataTypeInternal type) {
		type = type.getRootType();
		final Class<?> javaClass = type.getJavaClass();
		if (javaClass == null) {
			throw new IllegalArgumentException("type");
		}
		final Class<?> arrayClass;
		DataTypeInternal arrayTypeOf;
		final int mv;
		dataTypeMapRL.lock();
		try {
			arrayClass = Array.newInstance(type.getJavaClass(), 0).getClass();
			arrayTypeOf = dataTypesByClass.get(arrayClass);
			if (arrayTypeOf != null) {
				return (ArrayDataTypeBase) arrayTypeOf;
			}
			mv = modifiedVersion;
		} finally {
			dataTypeMapRL.unlock();
		}
		dataTypeMapWL.lock();
		try {
			if (mv != modifiedVersion) {
				arrayTypeOf = dataTypesByClass.get(arrayClass);
				if (arrayTypeOf != null) {
					return (ArrayDataTypeBase) arrayTypeOf;
				}
			}
			return new ObjectArrayDataType(arrayClass, type);
		} finally {
			dataTypeMapWL.unlock();
		}
	}

	public static final DataType findDataType(final GUID typeID) {
		if (typeID == null) {
			throw new NullArgumentException("typeID");
		}
		DataType dt;
		dataTypeMapRL.lock();
		try {
			dt = dataTypesByGUID.get(typeID);
		} finally {
			dataTypeMapRL.unlock();
		}
		if (dt != null) {
			return dt;
		}
		return tryFindAlternateType(typeID);
	}

	static final boolean regDataType(DataTypeInternal dataType) {
		if (dataType == null) {
			throw new NullArgumentException("dataType");
		}
		dataTypeMapWL.lock();
		try {
			final GUID id = dataType.getID();
			DataTypeInternal old = dataTypesByGUID.put(id, dataType);
			if (old != null) {
				if (old != dataType) {
					dataTypesByGUID.put(id, old);
					System.err.println("类型注册失败：ID冲突\"" + old + "\" - \""
							+ dataType + "\"，ID[" + id + "]");
				} else {
					System.err.println("类型注册警告：类型\"" + dataType + "\"被多次注册");
				}
				return false;
			} else {
				final Class<?> javaClass = dataType.getRegClass();
				if (javaClass != null) {
					old = dataTypesByClass.put(javaClass, dataType);
					if (old != null) {
						if (old != dataType) {
							dataTypesByClass.put(javaClass, old);
							dataTypesByGUID.remove(id);
							System.err.println("类型注册失败：Java类冲突\"" + old
									+ "\" - \"" + dataType + "\"，class["
									+ javaClass + "]");
						} else {
							System.err.println("类型注册警告：类型\"" + dataType
									+ "\"被多次注册");
						}
						return false;
					}
				}
			}
			modifiedVersion++;
		} finally {
			dataTypeMapWL.unlock();
		}
		return true;
	}

	// static final void unRegDataType(DataType dataType) {
	// if (dataType == null) {
	// throw new NullArgumentException("dataType");
	// }
	// dataTypeMapWL.lock();
	// try {
	// GUID id = dataType.getID();
	// final DataType old = dataTypesByGUID.remove(id);
	// if (old != dataType) {
	// if (old != null) {
	// dataTypesByGUID.put(id, old);
	// System.err.println("类型注消失败：ID冲突\"" + old + "\" - \""
	// + dataType + "\"，ID[" + id + "]");
	// }
	// } else if (dataType.getRootType() == dataType) {
	// final Class<?> javaClass = dataType.getJavaClass();
	// final DataType oldByClass = dataTypesByClass.remove(javaClass);
	// if (oldByClass != null && oldByClass != dataType) {
	// dataTypesByClass.put(javaClass, oldByClass);
	// System.err.println("类型注消失败：Java类冲突\"" + oldByClass
	// + "\" - \"" + dataType + "\"，class[" + javaClass
	// + "]");
	// }
	// }
	// } finally {
	// dataTypeMapWL.unlock();
	// }
	// }

	static final int class_modifier_ENUM = 0x00004000;

	/**
	 * 根据Java类型返回对应D&A类型
	 */
	public final static DataTypeInternal dataTypeOfJavaClass(Class<?> javaClass) {
		return dataTypeOfJavaClass(javaClass, false);
	}

	@SuppressWarnings("unchecked")
	final static DataTypeInternal dataTypeOfJavaClass(Class<?> javaClass,
			boolean rejectStruct) {
		if (javaClass == null) {
			throw new NullArgumentException("javaClass");
		}
		final int mv;
		dataTypeMapRL.lock();
		try {
			final DataTypeInternal type = dataTypesByClass.get(javaClass);
			if (type != null) {
				return type;
			}
			mv = modifiedVersion;
		} finally {
			dataTypeMapRL.unlock();
		}
		final int jcm = javaClass.getModifiers();
		if ((jcm & class_modifier_ENUM) != 0) {
			return EnumTypeImpl.ENUM((Class<Enum>) javaClass);
		}
		dataTypeMapWL.lock();
		try {
			if (mv != modifiedVersion) {
				final DataTypeInternal type = dataTypesByClass.get(javaClass);
				if (type != null) {
					return type;
				}
			}
			if (javaClass.isArray()) {
				return new ObjectArrayDataType(javaClass, null);
			} else if (rejectStruct
					|| (jcm & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0) {
				return new RefDataType(javaClass);
			} else {
				try {
					return new StaticStructDefineImpl(javaClass,
							dataTypesByClass, staticStructDefineByName, false);
				} catch (Throwable e) {
					return new RefDataType(javaClass, false);
				}
			}
		} finally {
			dataTypeMapWL.unlock();
		}
	}

	/**
	 * 获取对象的结构定义
	 */
	public final static DataTypeInternal dataTypeOfJavaObj(Object obj) {
		if (obj == null) {
			throw new NullArgumentException("obj");
		}
		if (obj instanceof DynObj) {
			final DynObj dynObj = (DynObj) obj;
			if (dynObj.define != null) {
				return dynObj.define;
			}
		}
		return dataTypeOfJavaClass(obj.getClass());
	}

	public final static StaticStructDefineImpl findStaticStructDefine(
			String structName) {
		dataTypeMapRL.lock();
		try {
			return staticStructDefineByName.get(structName);
		} finally {
			dataTypeMapRL.unlock();
		}
	}

	private final static UnsupportedOperationException unsupportedStaticStructDefine(
			Class<?> javaClass, Throwable cause) {
		if (cause != null) {
			return new UnsupportedOperationException("Java类型\"" + javaClass
					+ "\"不支持DNA结构类型", cause);
		} else {
			return new UnsupportedOperationException("Java类型\"" + javaClass
					+ "\"不支持DNA结构类型");
		}
	}

	private final static StaticStructDefineImpl findOrGetStaticStructDefine(
			Class<?> javaClass, boolean findOrGet, boolean force) {

		if (javaClass == null) {
			throw new NullArgumentException("javaClass");
		}
		final int mv;
		DataType objDataType;
		dataTypeMapRL.lock();
		try {
			objDataType = dataTypesByClass.get(javaClass);
			mv = modifiedVersion;
		} finally {
			dataTypeMapRL.unlock();
		}
		RefDataType refDataType;
		if (objDataType instanceof StaticStructDefineImpl) {
			return (StaticStructDefineImpl) objDataType;
		} else if (objDataType instanceof RefDataType) {
			refDataType = (RefDataType) objDataType;
			if (refDataType.rejectStruct) {
				if (findOrGet) {
					return null;
				} else {
					throw unsupportedStaticStructDefine(javaClass, null);
				}
			}
		} else if (objDataType != null) {
			if (findOrGet) {
				return null;
			} else {
				throw unsupportedStaticStructDefine(javaClass, null);
			}
		} else {
			refDataType = null;
		}
		dataTypeMapWL.lock();
		try {
			if (mv != modifiedVersion) {
				objDataType = dataTypesByClass.get(javaClass);
				if (objDataType instanceof StaticStructDefineImpl) {
					return (StaticStructDefineImpl) objDataType;
				} else if (objDataType instanceof RefDataType) {
					refDataType = (RefDataType) objDataType;
					if (refDataType.rejectStruct) {
						if (findOrGet) {
							return null;
						} else {
							throw unsupportedStaticStructDefine(javaClass, null);
						}
					}
				} else if (objDataType != null) {
					if (findOrGet) {
						return null;
					} else {
						throw unsupportedStaticStructDefine(javaClass, null);
					}
				} else {
					refDataType = null;
				}
			}
			Throwable ex = null;
			try {
				return new StaticStructDefineImpl(javaClass, dataTypesByClass,
						staticStructDefineByName, force);
			} catch (ETryLoadJavaFieldsAndPrepareAccessInfo e) {
				ex = e.getCause();
			} catch (Throwable e) {
				ex = e;
			}
			if (refDataType != null) {
				refDataType.rejectStruct = true;
			} else {
				new RefDataType(javaClass);
			}
			if (findOrGet) {
				return null;
			} else {
				throw unsupportedStaticStructDefine(javaClass, ex);
			}
		} finally {
			dataTypeMapWL.unlock();
		}
	}

	public final static StaticStructDefineImpl getStaticStructDefine(
			Class<?> javaClass) {
		return findOrGetStaticStructDefine(javaClass, false, true);
	}

	public final static StaticStructDefineImpl getStaticStructDefine(
			Class<?> javaClass, boolean force) {
		return findOrGetStaticStructDefine(javaClass, false, force);
	}

	/**
	 * 获取类型的结构定义，该类型的各子段都有效（忽略标记）
	 */
	public final static StaticStructDefineImpl findStaticStructDefine(
			Class<?> javaClass) {
		return findOrGetStaticStructDefine(javaClass, true, true);
	}

	public boolean isArray() {
		return false;
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	public boolean canDBTypeConvertTo(DataType target) {
		return false;
	}

	public static DataType typeOf(Class<?> type,
			java.lang.reflect.Type genericType, Class<?> ownerClass) {
		// XXX
		return dataTypeOfJavaClass(type);
	}

	final static DataTypeBase[] emptyArray = {};

	public boolean isLOB() {
		return false;
	}

	public boolean isBytes() {
		return false;
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public boolean isDBType() {
		return false;
	}

	public abstract <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData);

	static final int tryParseLength(String str, String name) {
		if (str.startsWith(name)) {
			for (int i = name.length(), c = str.length(); i < c; i++) {
				char chr = str.charAt(i);
				if (chr == ' ' || chr == '\t') {
					continue;
				}
				if (chr == '(') {
					for (int l = 0, j = i + 1; j < c; j++) {
						chr = str.charAt(j);
						if (chr == ' ' || chr == '\t') {
							continue;
						}
						if ('0' <= chr && chr <= '9') {
							l = l * 10 + chr - '0';
							continue;
						}
						if (chr == ')') {
							return l;
						}
						return -1;
					}
				}
				return -1;
			}
			return 1;
		}
		return -1;
	}

	final String getFactoryRefName() {
		return this.toString().toUpperCase();
	}

	public int nioSerialWrite(Object obj, NSerializer serializer,
			boolean declared) {
		throw new UnsupportedOperationException();
	}

	static final long ENTRY_TYPE_UNKNOWN = 0x0000000000000000L;
	static final long ENTRY_TYPE_NULL = 0x0001000000000000L;
	static final long PRIMITIVE_TYPE_BOOLEAN = 0x0002000000000000L;
	static final long PRIMITIVE_TYPE_BYTE = 0x0003000000000000L;
	static final long PRIMITIVE_TYPE_SHORT = 0x0004000000000000L;
	static final long PRIMITIVE_TYPE_CHAR = 0x0005000000000000L;
	static final long PRIMITIVE_TYPE_INT = 0x0006000000000000L;
	static final long PRIMITIVE_TYPE_LONG = 0x0007000000000000L;
	static final long PRIMITIVE_TYPE_DATE = 0x0008000000000000L;
	static final long PRIMITIVE_TYPE_FLOAT = 0x0009000000000000L;
	static final long PRIMITIVE_TYPE_DOUBLE = 0x000A000000000000L;
	static final long ENTRY_TYPE_STRING = 0x000B000000000000L;
	static final long ENTRY_TYPE_GUID = 0x000C000000000000L;
	static final long ENTRY_TYPE_CLASS = 0x000D000000000000L;
	static final long ARRAY_DEM_1 = 0x0100000000000000L;
	static final long ARRAY_DEM_MASK = 0xFF00000000000000L;
	static final long ARRAY_TYPE_MASK = ~ARRAY_DEM_MASK;
	static final long ENTRY_TYPE_BYTES = PRIMITIVE_TYPE_BYTE + ARRAY_DEM_1;
	static final int ALTER_LEN_MASK = 0x30000000;
	static final int ALTER_LEN_LOB = 0x20000000;
	static final int ALTER_LEN_FIXED = 0x10000000;
	static final int ALTER_LEN_VAR = 0x00000000;
	static final int ALTER_ISN = 0x01000000;

	final static GUID calcNativeTypeID(long typeMId) {
		return GUID.valueOf(typeMId, 0L);
	}

	final static GUID calcNativeTypeID(CharSequence digest) {
		final byte[] bytes = GUID.MD5BytesOf(digest);
		bytes[0] = 0;
		bytes[1] |= 0x80;// 对象标记
		return GUID.valueOf(bytes);
	}

	final static GUID calcArryTypeID(DataType componentType) {
		final GUID compid = componentType.getRootType().getID();
		final long msb = compid.getMostSigBits();
		final int deepth = (((int) (msb >>> 56)) + 1);
		if (255 < deepth) {
			throw new UnsupportedOperationException("数组的维度超过255:" + deepth);
		}
		return GUID.valueOf(msb + ARRAY_DEM_1, compid.getLeastSigBits());
	}

	final static GUID calcNumTypeID(byte precision, byte scale) {
		return GUID.valueOf((PRIMITIVE_TYPE_DOUBLE & 0xFFFFL) << 48,
				(precision & 0xFFL) << 8 | scale & 0xFFL);
	}

	final static GUID calcStrTypeID(boolean isFixed, boolean isLOB,
			boolean isN, int length) {
		long l;
		if (isLOB) {
			l = ALTER_LEN_LOB;
		} else if (isFixed) {
			l = ALTER_LEN_FIXED;
		} else {
			l = ALTER_LEN_VAR;
		}
		if (isN) {
			l |= ALTER_ISN;
		}
		return GUID.valueOf(ENTRY_TYPE_STRING, (l << 32)
				| (length & 0xFFFFFFFFL));
	}

	final static GUID calcBytesTypeID(boolean isFixed, boolean isLOB, int length) {
		long l;
		if (isLOB) {
			l = ALTER_LEN_LOB;
		} else if (isFixed) {
			l = ALTER_LEN_FIXED;
		} else {
			l = ALTER_LEN_VAR;
		}
		return GUID.valueOf(ENTRY_TYPE_BYTES, (l << 32)
				| (length & 0xFFFFFFFFL));
	}

	private static DataType tryFindAlternateType(GUID typeID) {
		final long msb = typeID.getMostSigBits();
		final long lsb = typeID.getLeastSigBits();
		if (msb == ENTRY_TYPE_STRING) {
			final int lsbh = (int) (lsb >>> 32);
			final int lsbl = (int) lsb;
			switch (lsbh & ALTER_LEN_MASK) {
			case ALTER_LEN_VAR:
				if ((lsbh & ALTER_ISN) != 0) {
					return NVarCharDBType.map.get(lsbl, 0, 0);
				} else {
					return VarCharDBType.map.get(lsbl, 0, 0);
				}
			case ALTER_LEN_FIXED:
				if ((lsbh & ALTER_ISN) != 0) {
					return NCharDBType.map.get(lsbl, 0, 0);
				} else {
					return CharDBType.map.get(lsbl, 0, 0);
				}
			}
		} else if (msb == ENTRY_TYPE_BYTES) {
			final int lsbh = (int) (lsb >>> 32);
			final int lsbl = (int) lsb;
			switch (lsbh & ALTER_LEN_MASK) {
			case ALTER_LEN_VAR:
				return VarBinDBType.map.get(lsbl, 0, 0);
			case ALTER_LEN_FIXED:
				return FixBinDBType.map.get(lsbl, 0, 0);
			}
		} else if ((msb & ARRAY_DEM_MASK) != 0) {
			final GUID orgTypeID = GUID.valueOf(ARRAY_TYPE_MASK & msb, lsb);
			DataTypeInternal dt;
			int mv;
			dataTypeMapRL.lock();
			try {
				dt = dataTypesByGUID.get(orgTypeID);
				mv = modifiedVersion;
			} finally {
				dataTypeMapRL.unlock();
			}
			if (dt != null) {
				dataTypeMapWL.lock();
				try {
					if (mv != modifiedVersion) {
						dt = dataTypesByGUID.get(orgTypeID);
						if (dt == null) {
							return null;
						}
					}
					int deepth = (int) (msb >>> 56);
					ArrayDataTypeBase adt = dt.arrayOf();
					while (--deepth > 0) {
						adt = adt.arrayOf();
					}
					return adt;
				} finally {
					dataTypeMapRL.lock();
				}
			}
		}
		return null;
	}

	private GUID id;

	public final GUID getID() {
		GUID id = this.id;
		if (id == null) {
			id = this.calcTypeID();
			this.id = id;
		}
		return id;
	}

	/**
	 * 计算类型ID
	 */
	protected abstract GUID calcTypeID();

	static abstract class AssignbilityBase implements
			TypeDetector<AssignCapability, DataType> {

		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inCharacter(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inDate(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inNumeric(DataType to, int precision, int scale)
				throws Throwable {
			return this.inDouble(to);
		}

		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inObject(DataType to, ObjectDataType type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inVarChar(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inString(to, type);
		}

		public AssignCapability inNVarChar(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inString(to, type);
		}

		public AssignCapability inChar(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inString(to, type);
		}

		public AssignCapability inNChar(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inString(to, type);
		}

		public AssignCapability inText(DataType to) throws Throwable {
			return this.inString(to, TextDBType.TYPE);
		}

		public AssignCapability inNText(DataType to) throws Throwable {
			return this.inString(to, NTextDBType.TYPE);
		}

		public AssignCapability inBytes(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inBinary(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inBytes(to, type);
		}

		public AssignCapability inVarBinary(DataType to, SequenceDataType type)
				throws Throwable {
			return this.inBytes(to, type);
		}

		public AssignCapability inBlob(DataType to) throws Throwable {
			return this.inBytes(to, BlobDBType.TYPE);
		}

		public AssignCapability inGUID(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inEnum(DataType to, EnumType<?> type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inResource(DataType to, Class<?> facadeClass,
				Object category) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inUnknown(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inStruct(DataType to, StructDefine type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inModel(DataType to, ModelDefine type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inQuery(DataType to, QueryStatementDefine type)
				throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inTable(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inRecordSet(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.NO;
		}

	}

	/**
	 * 确保该类静态数据被JVM初始
	 */
	static void ensureStaticInited() {
	}
}
