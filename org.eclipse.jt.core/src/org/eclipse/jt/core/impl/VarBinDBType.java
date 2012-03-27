/**
 *
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;
import org.eclipse.jt.core.type.Undigester;


/**
 * 变长二进制类型
 * 
 * @author Jeff Tang
 * 
 */
public final class VarBinDBType extends BinDBType {
	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > BIN_LENGTH_MAX) {
				throw new IllegalArgumentException("length > " + BIN_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final DataTypeBase newType(int length, int precision, int scale) {
			return new VarBinDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inVarBinary(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "varbinary(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "varbinary");
		return length > 0 ? TypeFactory.VARBINARY(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.VARBINARY_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.VARBINARY_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.VARBINARY(undigester.extractShort());
			}
		});
	}

	VarBinDBType(int length) {
		super(length);
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target == BytesType.TYPE || target instanceof FixBinDBType) {
			return this;
		} else if (target instanceof VarBinDBType || target == BlobDBType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

}