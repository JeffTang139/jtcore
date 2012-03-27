/**
 *
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;

/**
 * 长文本类型
 * 
 * @author Jeff Tang
 * 
 */
public final class TextDBType extends StringType {
	public static final TextDBType TYPE = new TextDBType();

	private TextDBType() {
		super();
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inText(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final String toString() {
		return "text";
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.TEXT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.TEXT) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	@Override
	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target == StringType.TYPE || target instanceof CharDBType
				|| target instanceof VarCharDBType
				|| target instanceof NCharDBType
				|| target instanceof NVarCharDBType) {
			return this;
		} else if (target.getRootType() == StringType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

}