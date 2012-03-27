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
 * GUID类型
 * 
 * @author Jeff Tang
 * 
 */
public final class GUIDType extends ObjectDataTypeBase {
	public static final GUIDType TYPE = new GUIDType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_GUID);
	}

	private GUIDType() {
		super(GUID.class);
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
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
		public AssignCapability inGUID(DataType to) throws Throwable {
			return AssignCapability.SAME;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inBytes(DataType to, SequenceDataType type)
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

	@Override
	public final String toString() {
		return "guid";
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inGUID(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.GUID);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(
				TypeCodeSet.GUID) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////
	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeGUIDData((GUID) object);
	}

}