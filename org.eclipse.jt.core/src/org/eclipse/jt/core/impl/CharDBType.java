package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.Undigester;


/**
 * 数据库定长字符类型
 * 
 * @author Jeff Tang
 * 
 */
public final class CharDBType extends CharsType {
	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this) {
			return true;
		}
		if (target instanceof CharDBType) {
			return this.length <= ((CharDBType) target).length;
		} else if (target instanceof VarCharDBType) {
			return this.length <= ((VarCharDBType) target).length;
		}
		return false;
	}

	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > CHAR_LENGTH_MAX) {
				throw new IllegalArgumentException("length > "
						+ CHAR_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final CharDBType newType(int length, int precision, int scale) {
			return new CharDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inChar(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "char(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "char");
		return length > 0 ? TypeFactory.CHAR(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.CHAR_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.CHAR_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.CHAR(undigester.extractShort());
			}
		});
	}

	CharDBType(int length) {
		super(length);
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target == StringType.TYPE) {
			return this;
		} else if (target.getRootType() == StringType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

}