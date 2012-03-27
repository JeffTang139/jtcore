package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;

/**
 * Unicode长文本
 * 
 * @author Jeff Tang
 * 
 */
public final class NTextDBType extends StringType {
	public static final NTextDBType TYPE = new NTextDBType();

	private NTextDBType() {
		super();
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	/**
	 * 是否是大对象
	 */
	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	boolean isN() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNText(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final String toString() {
		return "ntext";
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.NTEXT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.NTEXT) {
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
		} else if (target.getRootType() == StringType.TYPE) {
			return this;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

}