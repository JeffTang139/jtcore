package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.Undigester;


/**
 * Unicode变长字符窜
 * 
 * @author Jeff Tang
 * 
 */
public final class NVarCharDBType extends CharsType {
	@Override
	boolean isN() {
		return true;
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this) {
			return true;
		}
		if (target instanceof NCharDBType) {
			return this.length <= ((NCharDBType) target).length;
		} else if (target instanceof NVarCharDBType) {
			return this.length <= ((NVarCharDBType) target).length;
		}
		return false;
	}

	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > NCHAR_LENGTH_MAX) {
				throw new IllegalArgumentException("length > "
						+ NCHAR_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final DataTypeBase newType(int length, int precision, int scale) {
			return new NVarCharDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNVarChar(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "nvarchar(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "nvarchar");
		return length > 0 ? TypeFactory.NVARCHAR(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.NVARCHAR_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.NVARCHAR_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.NVARCHAR(undigester.extractShort());
			}
		});
	}

	NVarCharDBType(int length) {
		super(length);
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target == StringType.TYPE || target instanceof CharDBType
				|| target instanceof VarCharDBType
				|| target instanceof NCharDBType) {
			return this;
		} else if (target.getRootType() == StringType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

}