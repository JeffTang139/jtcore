package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidOperandTypeException;
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
public final class FloatType extends NumberType {
	public static final FloatType TYPE = new FloatType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_FLOAT);
	}

	private FloatType() {
		super(float.class);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == DoubleType.TYPE;
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
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.SAME;
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
				|| t == ShortType.TYPE || t == IntType.TYPE
				|| t == LongType.TYPE) {
			return this;
		} else if (target instanceof NumericDBType) {
			return this;
		} else if (t == DoubleType.TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public final String toString() {
		return "float";
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inFloat(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.FLOAT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.FLOAT) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}