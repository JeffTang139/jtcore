package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLSyntaxException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.TypeFactory;

/**
 * 变量类型
 * 
 * @author Jeff Tang
 * 
 */
abstract class NDataType {
	static class EnumType extends NDataType {
		private final String className;

		public EnumType(String className) {
			this.className = className;
		}

		@SuppressWarnings("unchecked")
		@Override
		DataType getType(ObjectQuerier oQuerier) {
			Class enumClass = oQuerier.get(Class.class, this.className);
			return TypeFactory.ENUM(enumClass);
		}

		@Override
		public String toString() {
			return "enum<" + this.className + ">";
		}
	}

	static class BinaryType extends NDataType {
		final int length;

		public BinaryType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.BINARY(this.length);
		}

		@Override
		public String toString() {
			return "binary(" + this.length + ")";
		}
	}

	static class VarBinaryType extends NDataType {
		final int length;

		public VarBinaryType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.VARBINARY(this.length);
		}

		@Override
		public String toString() {
			return "varbinary(" + this.length + ")";
		}
	}

	static class CharType extends NDataType {
		final int length;

		public CharType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.CHAR(this.length);
		}

		@Override
		public String toString() {
			return "char(" + this.length + ")";
		}
	}

	static class VarCharType extends NDataType {
		final int length;

		public VarCharType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.VARCHAR(this.length);
		}

		@Override
		public String toString() {
			return "varchar(" + this.length + ")";
		}
	}

	static class NCharType extends NDataType {
		final int length;

		public NCharType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.NCHAR(this.length);
		}

		@Override
		public String toString() {
			return "nchar(" + this.length + ")";
		}
	}

	static class NVarCharType extends NDataType {
		final int length;

		public NVarCharType(int length) {
			this.length = length;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.NVARCHAR(this.length);
		}

		@Override
		public String toString() {
			return "nvarchar(" + this.length + ")";
		}
	}

	static class NumericType extends NDataType {
		final int precision;
		final int scale;

		public NumericType(int precision, int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Override
		DataType getType(ObjectQuerier oQuerier) {
			if (this.precision == 19) {
				if (this.scale == 2) {
					return TypeFactory.NUM19_2;
				} else if (this.scale == 4) {
					return TypeFactory.NUM19_4;
				}
			}
			return TypeFactory.NUMERIC(this.precision, this.scale);
		}

		@Override
		public String toString() {
			return "numeric(" + this.precision + ", " + this.scale + ")";
		}
	}

	final static NDataType BLOB = new NDataType() {
		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.BLOB;
		}

		@Override
		public String toString() {
			return "blob";
		}
	};

	final static NDataType TEXT = new NDataType() {
		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.TEXT;
		}

		@Override
		public String toString() {
			return "text";
		}
	};

	final static NDataType NTEXT = new NDataType() {
		@Override
		DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.NTEXT;
		}

		@Override
		public String toString() {
			return "ntext";
		}
	};

	final static NDataType BOOLEAN = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.BOOLEAN;
		}

		@Override
		public String toString() {
			return "boolean";
		}
	};

	final static NDataType BYTE = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.BYTE;
		}

		@Override
		public String toString() {
			return "byte";
		}
	};

	final static NDataType BYTES = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.BYTES;
		}

		@Override
		public String toString() {
			return "bytes";
		}
	};

	final static NDataType DATE = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.DATE;
		}

		@Override
		public String toString() {
			return "date";
		}
	};

	final static NDataType DOUBLE = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.DOUBLE;
		}

		@Override
		public String toString() {
			return "double";
		}
	};

	final static NDataType FLOAT = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.FLOAT;
		}

		@Override
		public String toString() {
			return "float";
		}
	};

	final static NDataType GUID = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.GUID;
		}

		@Override
		public String toString() {
			return "guid";
		}
	};

	final static NDataType INT = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.INT;
		}

		@Override
		public String toString() {
			return "int";
		}
	};

	final static NDataType LONG = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.LONG;
		}

		@Override
		public String toString() {
			return "long";
		}
	};

	final static NDataType SHORT = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.SHORT;
		}

		@Override
		public String toString() {
			return "short";
		}
	};

	final static NDataType STRING = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			return TypeFactory.STRING;
		}

		@Override
		public String toString() {
			return "string";
		}
	};

	final static EnumType ENUM(String className) {
		return new EnumType(className);
	}

	final static BinaryType BINARY(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new BinaryType(i.value);
	}

	final static VarBinaryType VARBINARY(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new VarBinaryType(i.value);
	}

	final static CharType CHAR(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new CharType(i.value);
	}

	final static VarCharType VARCHAR(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new VarCharType(i.value);
	}

	final static NCharType NCHAR(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new NCharType(i.value);
	}

	final static NVarCharType NVARCHAR(TInt i) {
		if (i.value <= 0) {
			throw new SQLNotSupportedException(i.line, i.col, "长度必须大于0");
		}
		return new NVarCharType(i.value);
	}

	final static NumericType NUMERIC(TInt p, TInt s) {
		if (p.value <= 0) {
			throw new SQLNotSupportedException(p.line, p.col, "精度必须大于0");
		}
		if (s.value < 0) {
			throw new SQLNotSupportedException(s.line, s.col, "小数位必须大于0");
		}
		return new NumericType(p.value, s.value);
	}

	final static NDataType UNKNOWN = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			throw new SQLSyntaxException();
		}

		@Override
		public String toString() {
			return "unknown";
		}
	};

	final static NDataType RECORDSET = new NDataType() {
		@Override
		public DataType getType(ObjectQuerier oQuerier) {
			throw new SQLSyntaxException();
		}

		@Override
		public String toString() {
			return "recordset";
		}
	};

	abstract DataType getType(ObjectQuerier oQuerier);
}
