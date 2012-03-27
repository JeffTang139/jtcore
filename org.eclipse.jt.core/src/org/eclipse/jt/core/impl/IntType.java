package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.Undigester;


/**
 * 整数类型
 * 
 * @author Jeff Tang
 * 
 */
public final class IntType extends NumberType {
	public static final IntType TYPE = new IntType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_INT);
	}

	private IntType() {
		super(int.class);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == LongType.TYPE
				|| target == DoubleType.TYPE || target == FloatType.TYPE
				|| target instanceof NumericDBType
				&& ((NumericDBType) target).hasIntPrecision(10);
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.SAME;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inCharacter(final DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}
	};

	public final DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		}
		DataType t = target.getRootType();
		if (t == this || t == BooleanType.TYPE || t == ByteType.TYPE
				|| t == ShortType.TYPE) {
			return this;
		} else if (t == LongType.TYPE || t == FloatType.TYPE
				|| t == DoubleType.TYPE) {
			return target;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public final String toString() {
		return "int";
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inInt(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.INT);
	}

	static {
		DataTypeUndigester
				.regUndigester(new DataTypeUndigester(TypeCodeSet.INT) {
					@Override
					protected DataType doUndigest(Undigester undigester) {
						return TYPE;
					}
				});
	}
}