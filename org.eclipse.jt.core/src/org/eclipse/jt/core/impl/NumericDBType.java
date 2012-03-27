package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.Undigester;


/**
 * 定点小数
 * 
 * @author Jeff Tang
 * 
 */
public final class NumericDBType extends DoubleType {

	static final int MAX_PRECISION = 31;
	static final int MAX_SCALE = 30;

	public final byte precision;
	public final byte scale;

	@Override
	protected final GUID calcTypeID() {
		return calcNumTypeID(this.precision, this.scale);
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this || target == FloatType.TYPE
				|| target == DoubleType.TYPE) {
			return true;
		}
		if (target instanceof NumericDBType) {
			NumericDBType tN = (NumericDBType) target;
			return this.precision <= tN.precision && this.scale <= tN.scale;
		}
		return false;
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		}
		DataType t = target.getRootType();
		if (t == this || t == BooleanType.TYPE || t == ByteType.TYPE
				|| t == ShortType.TYPE || t == IntType.TYPE
				|| t == LongType.TYPE) {
			return this;
		} else if (t == FloatType.TYPE || t == DoubleType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	/**
	 * 返回是否兼容整数精度
	 */
	public final boolean hasIntPrecision(int intP) {
		return (this.precision - this.scale) >= intP;
	}

	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (scale < 0) {
				throw new IllegalArgumentException("scale < 0");
			}
			if (scale > MAX_SCALE) {
				throw new IllegalArgumentException(MAX_SCALE + "< scale ");
			}
			if (precision == 0) {
				throw new IllegalArgumentException("precision == 0");
			}
			if (precision < scale) {
				throw new IllegalArgumentException("precision < scale");
			}
			if (MAX_PRECISION < precision) {
				throw new IllegalArgumentException(MAX_PRECISION
						+ " < precision");
			}
			return precision << 8 | scale;
		}

		@Override
		final DataTypeBase newType(int l, int precision, int scale) {
			return new NumericDBType(precision, scale);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNumeric(userData, this.precision, this.scale);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private String toString;

	@Override
	public final String toString() {
		if (this.toString == null) {
			this.toString = "numeric(" + this.precision + ',' + this.scale
					+ ')';
		}
		return this.toString;
	}

	public static DataType tryParse(String str) {
		final String numeric = "numeric";
		if (str.startsWith(numeric)) {
			for (int i = numeric.length(), c = str.length(); i < c; i++) {
				char chr = str.charAt(i);
				if (chr == ' ' || chr == '\t') {
					continue;
				}
				if (chr == '(') {
					for (int p = 0, j = i + 1; j < c; j++) {
						chr = str.charAt(j);
						if (chr == ' ' || chr == '\t') {
							continue;
						}
						if ('0' <= chr && chr <= '9') {
							p = p * 10 + chr - '0';
							continue;
						}
						if (chr == ',') {
							for (int s = 0, k = j + 1; k < c; k++) {
								chr = str.charAt(k);
								if (chr == ' ' || chr == '\t') {
									continue;
								}
								if ('0' <= chr && chr <= '9') {
									s = s * 10 + chr - '0';
									continue;
								}
								if (chr == ')') {
									return TypeFactory.NUMERIC(p, s);
								}
								return null;
							}
						}
						if (chr == ')') {
							if (p == 0) {
								p = 1;
							}
							return TypeFactory.NUMERIC(p, 0);
						}
						return null;
					}
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.NUMERIC_H);
		digester.update((short) (this.precision << 8 + this.scale));
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.NUMERIC_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				short st = undigester.extractShort();
				byte s = (byte) (st & 0xFF);
				byte p = (byte) ((st >>> 8) & 0xFF);
				return TypeFactory.NUMERIC(p, s);
			}
		});
	}

	@Override
	final void regThisDataTypeInConstructor() {
	};

	NumericDBType(int precision, int scale) {
		super();
		this.precision = (byte) precision;
		this.scale = (byte) scale;
		regDataType(this);
	}

}