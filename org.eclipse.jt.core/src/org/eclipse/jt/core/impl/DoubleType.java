/**
 *
 */
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
 * 双精度类型
 * 
 * @author Jeff Tang
 * 
 */
public class DoubleType extends NumberType {
	public static final DoubleType TYPE = new DoubleType();

	@Override
	protected GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_DOUBLE);
	}

	DoubleType() {
		super(double.class);
	}

	@Override
	public boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == FloatType.TYPE;
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
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.SAME;
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

	public DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target instanceof NumberType) {
			return DoubleType.TYPE;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	@Override
	public String toString() {
		return "double";
	}

	@Override
	public final DoubleType getRootType() {
		final DoubleType type = TYPE;
		if (type != null) {
			return type;
		} else if (this.getClass() == DoubleType.class) {
			return this;
		} else {
			return TYPE;
		}
	}

	@Override
	public final Class<?> getRegClass() {
		return this.getRootType() == this ? super.getRegClass() : null;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inDouble(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.DOUBLE);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.DOUBLE) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}