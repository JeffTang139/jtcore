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
 * 日起类型
 * 
 * @author Jeff Tang
 * 
 */
public final class DateType extends DataTypeBase {

	public static final DateType TYPE = new DateType();

	@Override
	public Class<?> getRegClass() {
		return null;
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_DATE);
	}

	private DateType() {
		super(long.class);
		this.setArrayOf(LongArrayDataType.TYPE);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inDate(DataType to) throws Throwable {
			return AssignCapability.SAME;
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
		} else if (target == this) {
			return this;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	public final boolean accept(DataType type) {
		return type == TYPE;
	}

	@Override
	public final String toString() {
		return "date";
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inDate(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.DATE);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.DATE) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}