package org.eclipse.jt.core.type;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.impl.BinDBType;
import org.eclipse.jt.core.impl.BlobDBType;
import org.eclipse.jt.core.impl.BooleanArrayDataType;
import org.eclipse.jt.core.impl.BooleanType;
import org.eclipse.jt.core.impl.ByteType;
import org.eclipse.jt.core.impl.BytesType;
import org.eclipse.jt.core.impl.CharArrayDataType;
import org.eclipse.jt.core.impl.CharDBType;
import org.eclipse.jt.core.impl.CharacterType;
import org.eclipse.jt.core.impl.CharsType;
import org.eclipse.jt.core.impl.ClassType;
import org.eclipse.jt.core.impl.DataTypeBase;
import org.eclipse.jt.core.impl.DateType;
import org.eclipse.jt.core.impl.DoubleArrayDataType;
import org.eclipse.jt.core.impl.DoubleType;
import org.eclipse.jt.core.impl.EnumTypeImpl;
import org.eclipse.jt.core.impl.FixBinDBType;
import org.eclipse.jt.core.impl.FloatArrayDataType;
import org.eclipse.jt.core.impl.FloatType;
import org.eclipse.jt.core.impl.GUIDArrayDataType;
import org.eclipse.jt.core.impl.GUIDType;
import org.eclipse.jt.core.impl.IntArrayDataType;
import org.eclipse.jt.core.impl.IntType;
import org.eclipse.jt.core.impl.LongArrayDataType;
import org.eclipse.jt.core.impl.LongType;
import org.eclipse.jt.core.impl.ModelDefineImpl;
import org.eclipse.jt.core.impl.NCharDBType;
import org.eclipse.jt.core.impl.NTextDBType;
import org.eclipse.jt.core.impl.NVarCharDBType;
import org.eclipse.jt.core.impl.NumericDBType;
import org.eclipse.jt.core.impl.SQLTypesWrapper;
import org.eclipse.jt.core.impl.ShortArrayDataType;
import org.eclipse.jt.core.impl.ShortType;
import org.eclipse.jt.core.impl.StaticStructDefineImpl;
import org.eclipse.jt.core.impl.StringArrayDataType;
import org.eclipse.jt.core.impl.StringType;
import org.eclipse.jt.core.impl.TextDBType;
import org.eclipse.jt.core.impl.UnknownType;
import org.eclipse.jt.core.impl.VarBinDBType;
import org.eclipse.jt.core.impl.VarCharDBType;


/**
 * 类型工厂
 * 
 * <p>
 * 用于构造各种DNA定义类型，包括DNA定义的Java类型，数据库类型，及列表与数组类型。
 * 
 * <p>
 * 数据库类型包括以下：
 * <ul>
 * <li>BOOLEAN
 * <li>SHORT
 * <li>INT
 * <li>LONG
 * <li>FLOAT
 * <li>DOUBLE
 * <li>NUMBER
 * <li>GUID
 * <li>DATE
 * <li>CHAR
 * <li>VARCHAR
 * <li>TEXT
 * <li>NCHAR
 * <li>NVARCHAR
 * <li>NTEXT
 * <li>BINARY
 * <li>VARBINARY
 * <li>BLOB
 * </ul>
 * 
 * @author Jeff Tang
 * 
 */
public final class TypeFactory {

	/**
	 * 布尔型
	 */
	public static final DataType BOOLEAN = BooleanType.TYPE;
	/**
	 * 布尔型数组
	 */
	public static final ArrayDataType BOOLEAN_ARRAY = BooleanArrayDataType.TYPE;
	/**
	 * 字节型
	 */
	public static final DataType BYTE = ByteType.TYPE;
	/**
	 * 二进制类型
	 */
	public static final ArrayDataType BYTES = BytesType.TYPE;
	/**
	 * 短整型
	 */
	public static final DataType SHORT = ShortType.TYPE;
	/**
	 * 短整型数组
	 */
	public static final ObjectDataType SHORT_ARRAY = ShortArrayDataType.TYPE;
	/**
	 * 整型
	 */
	public static final DataType INT = IntType.TYPE;
	/**
	 * 整型数组
	 */
	public static final ArrayDataType INT_ARRAY = IntArrayDataType.TYPE;
	/**
	 * 长整型
	 */
	public static final DataType LONG = LongType.TYPE;
	/**
	 * 长整型
	 */
	public static final ArrayDataType LONG_ARRAY = LongArrayDataType.TYPE;
	/**
	 * 单精浮点类型
	 */
	public static final DataType FLOAT = FloatType.TYPE;
	/**
	 * 单精浮点类型
	 */
	public static final ArrayDataType FLOAT_ARRAY = FloatArrayDataType.TYPE;

	/**
	 * 双精浮点类型
	 */
	public static final DataType DOUBLE = DoubleType.TYPE;
	/**
	 * 双精浮点类型
	 */
	public static final ArrayDataType DOUBLE_ARRAY = DoubleArrayDataType.TYPE;
	/**
	 * 字符串类型
	 */
	public static final ObjectDataType STRING = StringType.TYPE;
	/**
	 * 字符串类型
	 */
	public static final ArrayDataType STRING_ARRAY = StringArrayDataType.TYPE;
	/**
	 * 日期类型
	 */
	public static final DataType DATE = DateType.TYPE;
	/**
	 * GUID类型
	 */
	public static final ObjectDataType GUID = GUIDType.TYPE;
	/**
	 * GUID类型
	 */
	public static final ArrayDataType GUID_ARRAY = GUIDArrayDataType.TYPE;
	/**
	 * 未知类型
	 */
	public static final DataType UNKNOWN = UnknownType.TYPE;
	/**
	 * 文本型
	 */
	public static final ObjectDataType TEXT = TextDBType.TYPE;
	/**
	 * Unicode文本型
	 */
	public static final ObjectDataType NTEXT = NTextDBType.TYPE;
	/**
	 * 大二进制型
	 */
	public static final ObjectDataType BLOB = BlobDBType.TYPE;
	/**
	 * 表引用类型，占位符
	 */
	public static final Type TABLE_REF = new Type() {

		public <TResult, TUserData> TResult detect(
				TypeDetector<TResult, TUserData> detector, TUserData userData)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		public final Type getRootType() {
			return this;
		}

		public final void digestType(Digester digester) {
			throw new UnsupportedOperationException();
		}
	};

	public static DataType CHAR(int length) {
		return CharDBType.map.get(length, 0, 0);
	}

	@Deprecated
	public static boolean isNVarChar(Type type) {
		return type instanceof NVarCharDBType;
	}

	public static DataType NCHAR(int length) {
		return NCharDBType.map.get(length, 0, 0);
	}

	@Deprecated
	public static boolean isVarChar(Type type) {
		return type instanceof VarCharDBType;
	}

	public static DataType VARCHAR(int length) {
		return VarCharDBType.map.get(length, 0, 0);
	}

	public static final DataType VARCHAR8 = VARCHAR(8);
	public static final DataType VARCHAR16 = VARCHAR(16);
	public static final DataType VARCHAR32 = VARCHAR(32);
	public static final DataType VARCHAR64 = VARCHAR(64);
	public static final DataType VARCHAR128 = VARCHAR(128);
	public static final DataType VARCHAR256 = VARCHAR(256);

	public static DataType NVARCHAR(int length) {
		return NVarCharDBType.map.get(length, 0, 0);
	}

	public static DataType NUMERIC(int precision, int scale) {
		return NumericDBType.map.get(0, precision, scale);
	}

	public static final DataType NUM19_2 = NUMERIC(19, 2);
	public static final DataType NUM19_4 = NUMERIC(19, 4);

	public static DataType BINARY(int length) {
		return FixBinDBType.map.get(length, 0, 0);
	}

	public static DataType VARBINARY(int length) {
		return VarBinDBType.map.get(length, 0, 0);
	}

	public static final DataType VARBINARY32 = VARBINARY(32);

	public static <TEnum extends Enum<TEnum>> EnumType<TEnum> ENUM(
			Class<TEnum> enumClass) {
		return EnumTypeImpl.ENUM(enumClass);
	}

	@Deprecated
	public static StructDefine Struct(Class<?> soClass) {
		return struct(soClass);
	}

	public static StructDefine struct(Class<?> soClass) {
		return DataTypeBase.getStaticStructDefine(soClass);
	}

	private static Map<String, DataType> typeMap = new HashMap<String, DataType>();
	private static Map<String, DataType> rootTypeTitleMap = new HashMap<String, DataType>();

	private static void putType(DataType type, String rootTitle) {
		typeMap.put(type.toString(), type);
		if (rootTitle != null && rootTitle.length() > 0) {
			rootTypeTitleMap.put(rootTitle, type);
		}
	}

	public static DataType typeOf(String typeName, ObjectQuerier querier) {
		DataType type = typeMap.get(typeName);
		if (type != null) {
			return type;
		}
		if ((type = CharsType.tryParse(typeName)) != null) {
			return type;
		}
		if ((type = BinDBType.tryParse(typeName)) != null) {
			return type;
		}
		if ((type = NumericDBType.tryParse(typeName)) != null) {
			return type;
		}
		if ((type = StaticStructDefineImpl.tryParse(typeName)) != null) {
			return type;
		}
		if ((type = ModelDefineImpl.tryParse(typeName, querier)) != null) {
			return type;
		}
		return UNKNOWN;
	}

	public static DataType rootTypeOf(String title) {
		DataType type = rootTypeTitleMap.get(title);
		return type != null ? type : UNKNOWN;
	}

	/**
	 * 由dnaType获取对应的sqlType
	 */
	private static final TypeDetector<Integer, Object> sqlTypeDetector = new TypeDetectorBase<Integer, Object>() {

		@Override
		public final Integer inBinary(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.BINARY;
		}

		@Override
		public final Integer inBlob(Object userData) {
			return SQLTypesWrapper.BLOB;
		}

		@Override
		public final Integer inBoolean(Object userData) {
			return SQLTypesWrapper.BIT;
		}

		@Override
		public final Integer inChar(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.CHAR;
		}

		@Override
		public final Integer inDate(Object userData) {
			return SQLTypesWrapper.TIMESTAMP;
		}

		@Override
		public final Integer inDouble(Object userData) {
			return SQLTypesWrapper.DOUBLE;
		}

		@Override
		public final Integer inFloat(Object userData) {
			return SQLTypesWrapper.FLOAT;
		}

		@Override
		public final Integer inGUID(Object userData) {
			return SQLTypesWrapper.BINARY;
		}

		@Override
		public final Integer inInt(Object userData) {
			return SQLTypesWrapper.INTEGER;
		}

		@Override
		public final Integer inLong(Object userData) {
			return SQLTypesWrapper.BIGINT;
		}

		@Override
		public final Integer inNChar(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.CHAR;
		}

		@Override
		public final Integer inNText(Object userData) {
			return SQLTypesWrapper.CLOB;
		}

		@Override
		public final Integer inNumeric(Object userData, int precision, int scale) {
			return SQLTypesWrapper.NUMERIC;
		}

		@Override
		public final Integer inNVarChar(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.VARCHAR;
		}

		@Override
		public final Integer inShort(Object userData) {
			return SQLTypesWrapper.SMALLINT;
		}

		@Override
		public final Integer inText(Object userData) {
			return SQLTypesWrapper.CLOB;
		}

		@Override
		public final Integer inVarBinary(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.VARBINARY;
		}

		@Override
		public final Integer inVarChar(Object userData, SequenceDataType type) {
			return SQLTypesWrapper.VARCHAR;
		}

		@Override
		public final Integer inByte(Object userData) throws Throwable {
			return SQLTypesWrapper.TINYINT;
		}

		@Override
		public final Integer inBytes(Object userData, SequenceDataType type)
				throws Throwable {
			return SQLTypesWrapper.VARBINARY;
		}

		@Override
		public final Integer inString(Object userData, SequenceDataType type)
				throws Throwable {
			return SQLTypesWrapper.VARCHAR;
		}

		@Override
		public final Integer inEnum(Object userData, EnumType<?> type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}
	};

	public static int sqlTypeOf(DataType type) {
		return type.detect(sqlTypeDetector, null);
	}

	static {
		putType(BOOLEAN, "Boolean");
		putType(BYTE, "Byte");
		putType(SHORT, "Short");
		putType(INT, "Int");
		putType(LONG, "Long");
		putType(FLOAT, "Float");
		putType(DOUBLE, "Double");
		putType(DATE, "Date");
		putType(STRING, "String");
		putType(BYTES, "Bytes");
		putType(GUID, "GUID");
		putType(TEXT, null);
		putType(NTEXT, null);
		putType(BLOB, null);
		putType(UNKNOWN, "Unknown");
	}

	private TypeFactory() {

	}

	public static final DataType CHARACTER = CharacterType.TYPE;
	public static final ArrayDataType CHARACTER_ARRAY = CharArrayDataType.TYPE;
	public static final ObjectDataType CLASS = ClassType.TYPE;

	/**
	 * 确保该类静态数据被JVM初始
	 */
	public final static void ensureStaticInited() {
	}
}
