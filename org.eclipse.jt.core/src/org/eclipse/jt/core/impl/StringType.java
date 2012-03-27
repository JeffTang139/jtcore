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
 * 字符串类型
 * 
 * @author Jeff Tang
 * 
 */
public class StringType extends ObjectDataTypeBase implements SequenceDataType {

	public static final StringType TYPE = new StringType();

	@Override
	protected final GUID calcTypeID() {
		return calcStrTypeID(this.isFixedLength(), this.isLOB(), this.isN(),
				this.getMaxLength());
	}

	public boolean isFixedLength() {
		return false;
	}

	boolean isN() {
		return false;
	}

	StringType() {
		super(String.class);
	}

	public int getMaxLength() {
		return 0;
	}

	@Override
	public String toString() {
		return "string";
	}

	@Override
	public final StringType getRootType() {
		final StringType type = TYPE;
		if (type != null) {
			return type;
		} else if (this.getClass() == StringType.class) {
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
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inBytes(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inCharacter(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			if (to == type) {
				return AssignCapability.SAME;
			}
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inDate(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inGUID(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

	};

	@Override
	public final boolean isString() {
		return true;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inString(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.STRING);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.STRING) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	public DataType calcPrecedence(DataType target) {
		if (target == NullType.TYPE) {
			return this;
		} else if (target.getRootType() == TYPE) {
			return target;
		}
		throw new InvalidOperandTypeException(this, target);
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeStringData((String) object);
	}

}